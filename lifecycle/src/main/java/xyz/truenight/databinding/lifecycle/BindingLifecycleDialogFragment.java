package xyz.truenight.databinding.lifecycle;

import android.app.Activity;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.ContextWrapper;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.SimpleArrayMap;
import android.util.Log;
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

public abstract class BindingLifecycleDialogFragment<B extends ViewDataBinding> extends DialogFragment {

    private static final String TAG = BindingLifecycleDialogFragment.class.getSimpleName();

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

    public void show(Context context) {
        try {
            show(getFragmentActivity(context).getSupportFragmentManager(), getClass().getCanonicalName());
        } catch (Throwable th) {
            Log.e(TAG, "", th);
        }
    }

    public static FragmentActivity getFragmentActivity(Context context) {
        return getFragmentActivity(context, false);
    }

    public static FragmentActivity getFragmentActivity(Context context, boolean safe) {
        if (context instanceof FragmentActivity) {
            return (FragmentActivity) context;
        } else if (context instanceof Activity) {
            throw new IllegalStateException("Context " + context + " NOT support-v4 Activity");
        } else if (context instanceof ContextWrapper) {
            return getFragmentActivity(((ContextWrapper) context).getBaseContext(), safe);
        }
        if (safe) {
            return null;
        }
        throw new IllegalStateException("Context " + context + " NOT contains activity!");
    }
}
