package xyz.truenight.databinding.rxjava;

import android.util.Log;

import java.lang.reflect.Field;

import androidx.databinding.ObservableInt;

/**
 * Created by true
 * date: 19/10/2017
 * time: 23:42
 * <p>
 * Copyright © Mikhail Frolov
 */

public class ChangeObservableInt extends ObservableInt {
    private static final String TAG = ChangeObservableInt.class.getSimpleName();

    public ChangeObservableInt() {
    }

    public ChangeObservableInt(int value) {
        super(value);
    }

    public void setSilent(int value) {
        try {
            Field field = ObservableInt.class.getDeclaredField("mValue");
            field.setAccessible(true);
            field.set(this, value);
        } catch (NoSuchFieldException e) {
            Log.e(TAG, "", e);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "", e);
        }
    }
}
