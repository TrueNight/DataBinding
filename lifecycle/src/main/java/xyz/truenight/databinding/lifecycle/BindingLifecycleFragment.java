package xyz.truenight.databinding.lifecycle;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.SimpleArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import xyz.truenight.utils.Utils;

/**
 * Created by true
 * date: 02/09/2017
 * time: 16:51
 * <p>
 * Copyright © Mikhail Frolov
 */

public abstract class BindingLifecycleFragment<B extends ViewDataBinding> extends Fragment {

    private final SimpleArrayMap<String, ViewModel> map = new SimpleArrayMap<>();

    private B mBinding;

    @CallSuper
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mBinding == null) {
            mBinding = DataBindingUtil.inflate(inflater, getBindingLayoutRes(), container, false);
        }

        List<ViewModelBinding> bindings = getViewModelBindings();

        if (Utils.isNotEmpty(bindings)) {
            for (ViewModelBinding holder : bindings) {
                ViewModelProvider provider;
                if (holder.getFactory() == null) {
                    if (holder.getActivity() == null) {
                        provider = ViewModelProviders.of(holder.getFragment());
                    } else {
                        provider = ViewModelProviders.of(holder.getActivity());
                    }
                } else {
                    if (holder.getActivity() == null) {
                        provider = ViewModelProviders.of(holder.getFragment(), holder.getFactory());
                    } else {
                        provider = ViewModelProviders.of(holder.getActivity(), holder.getFactory());
                    }
                }
                ViewModel viewModel = provider.get(holder.getVmClass());
                map.put(holder.getVmClass().getCanonicalName(), viewModel);
                if (viewModel instanceof LifecycleTrackingViewModel) {
                    ((LifecycleTrackingViewModel) viewModel).registerLifecycle(this);
                }

                if (!mBinding.setVariable(holder.getVariableId(), viewModel)) {
                    BindingUtil.throwMissingVariable(mBinding, holder.getVariableId(), getBindingLayoutRes());
                }
            }
        }

        return mBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mBinding != null) {
            mBinding.unbind();
        }
    }

    @LayoutRes
    public abstract int getBindingLayoutRes();

    public abstract BindingList getViewModelBindings();

    public <VM extends ViewModel> VM getViewModel(Class<VM> viewModelClass) {
        //noinspection unchecked
        return (VM) map.get(viewModelClass.getCanonicalName());
    }

    protected BindingList list(ViewModelBinding... binding) {
        return new BindingList(binding);
    }

    public B binding() {
        return mBinding;
    }
}