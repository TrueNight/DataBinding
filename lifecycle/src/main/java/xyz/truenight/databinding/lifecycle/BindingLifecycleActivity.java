package xyz.truenight.databinding.lifecycle;

import android.os.Bundle;

import java.util.List;

import androidx.annotation.CallSuper;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.SimpleArrayMap;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import xyz.truenight.utils.Utils;

/**
 * Created by true
 * date: 02/09/2017
 * time: 15:46
 * <p>
 * Copyright © Mikhail Frolov
 */

public abstract class BindingLifecycleActivity<B extends ViewDataBinding> extends AppCompatActivity {

    private final SimpleArrayMap<String, ViewModel> map = new SimpleArrayMap<>();

    private B mBinding;

    public BindingLifecycleActivity() {
    }

    @CallSuper
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mBinding == null) {
            mBinding = DataBindingUtil.setContentView(this, getBindingLayoutRes());
        }

        BindingProvider viewModelBindings = getViewModelBindings();
        List<ViewModelBinding> bindings = viewModelBindings == null ? null : viewModelBindings.getItems();

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
                onPrepareViewModel(holder.getVmClass(), viewModel);
                if (viewModel instanceof LifecycleTrackingViewModel) {
                    ((LifecycleTrackingViewModel) viewModel).registerLifecycle(this);
                }

                if (!mBinding.setVariable(holder.getVariableId(), viewModel)) {
                    BindingUtil.throwMissingVariable(mBinding, holder.getVariableId(), getBindingLayoutRes());
                }
            }
        }
    }

    protected void onPrepareViewModel(Class<? extends ViewModel> vmClass, ViewModel viewModel) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBinding != null) {
            mBinding.unbind();
        }
    }

    @LayoutRes
    public abstract int getBindingLayoutRes();

    @SuppressWarnings("unchecked")
    public abstract BindingProvider getViewModelBindings();

    public <VM extends ViewModel> VM getViewModel(Class<VM> viewModelClass) {
        //noinspection unchecked
        return (VM) map.get(viewModelClass.getCanonicalName());
    }

    public B binding() {
        return mBinding;
    }

    protected final BindingList list(ViewModelBinding... binding) {
        return new BindingList(binding);
    }

    protected final ViewModelBinding bind(FragmentActivity activity, int variableId,
                                          Class<? extends ViewModel> vmClass) {
        return new ViewModelBinding(activity, variableId, vmClass);
    }

    protected final <T extends ViewModel> ViewModelBinding bind(FragmentActivity activity,
                                                                int variableId, Class<T> vmClass,
                                                                ViewModelBinding.Factory<T> factory) {
        return new ViewModelBinding(activity, variableId, vmClass, factory);
    }

    protected final ViewModelBinding bind(Fragment fragment, int variableId,
                                          Class<? extends ViewModel> vmClass) {
        return new ViewModelBinding(fragment, variableId, vmClass);
    }

    protected final <T extends ViewModel> ViewModelBinding bind(Fragment fragment,
                                                                int variableId, Class<T> vmClass,
                                                                ViewModelBinding.Factory<T> factory) {
        return new ViewModelBinding(fragment, variableId, vmClass, factory);
    }
}
