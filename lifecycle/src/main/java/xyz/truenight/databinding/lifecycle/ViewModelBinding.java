package xyz.truenight.databinding.lifecycle;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

/**
 * Created by true
 * date: 06/09/2017
 * time: 19:36
 * <p>
 * Copyright © Mikhail Frolov
 */

public class ViewModelBinding {

    private final FragmentActivity mActivity;
    private final Fragment mFragment;
    private final Class<? extends ViewModel> mVmClass;
    private final int mVariableId;
    private Factory<? extends ViewModel> mFactory;

    public ViewModelBinding(FragmentActivity activity, int variableId, Class<? extends ViewModel> vmClass) {
        mActivity = activity;
        mFragment = null;
        mVmClass = vmClass;
        mVariableId = variableId;
    }

    public <T extends ViewModel> ViewModelBinding(FragmentActivity activity, int variableId, Class<T> vmClass, Factory<T> factory) {
        mActivity = activity;
        mFragment = null;
        mVmClass = vmClass;
        mVariableId = variableId;
        mFactory = factory;
    }

    public ViewModelBinding(Fragment fragment, int variableId, Class<? extends ViewModel> vmClass) {
        mActivity = null;
        mFragment = fragment;
        mVmClass = vmClass;
        mVariableId = variableId;
    }

    public <T extends ViewModel> ViewModelBinding(Fragment fragment, int variableId, Class<T> vmClass, Factory<T> factory) {
        mActivity = null;
        mFragment = fragment;
        mVmClass = vmClass;
        mVariableId = variableId;
        mFactory = factory;
    }

    public FragmentActivity getActivity() {
        return mActivity;
    }

    public Fragment getFragment() {
        return mFragment;
    }

    public Class<? extends ViewModel> getVmClass() {
        return mVmClass;
    }

    public int getVariableId() {
        return mVariableId;
    }

    public Factory getRawFactory() {
        return mFactory;
    }

    public ViewModelProvider.Factory getFactory() {
        return mFactory == null ? null : new SingleProviderFactory<>(mFactory);
    }


    public interface Factory<T extends ViewModel> {
        @NonNull
        T create();
    }
}
