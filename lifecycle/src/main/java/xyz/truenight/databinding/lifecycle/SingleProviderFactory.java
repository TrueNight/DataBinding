package xyz.truenight.databinding.lifecycle;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

/**
 * Created by true
 * date: 17/10/2017
 * time: 01:36
 * <p>
 * Copyright © Mikhail Frolov
 */

class SingleProviderFactory<E extends ViewModel> implements ViewModelProvider.Factory {


    private ViewModelBinding.Factory<E> mFactory;

    SingleProviderFactory(@NonNull ViewModelBinding.Factory<E> factory) {
        mFactory = factory;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        try {
            return modelClass.cast(create());
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Model class NOT supported");
        }
    }

    public E create() {
        return mFactory.create();
    }
}
