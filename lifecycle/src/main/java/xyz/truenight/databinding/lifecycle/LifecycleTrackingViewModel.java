package xyz.truenight.databinding.lifecycle;

import android.arch.lifecycle.GenericLifecycleObserver;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ViewModel;

import static android.arch.lifecycle.Lifecycle.State.DESTROYED;
import static android.arch.lifecycle.Lifecycle.State.STARTED;

/**
 * Created by true
 * date: 02/09/2017
 * time: 23:17
 * <p>
 * Copyright © Mikhail Frolov
 */

public abstract class LifecycleTrackingViewModel extends ViewModel {

    private int mActiveCount;

    public LifecycleTrackingViewModel() {
    }

    public void registerLifecycle(final LifecycleOwner owner) {
        owner.getLifecycle().addObserver(new GenericLifecycleObserver() {
            boolean mActive = false;

            @Override
            @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
            public void onStateChanged(LifecycleOwner source, Lifecycle.Event event) {
                if (source.getLifecycle().getCurrentState() == DESTROYED) {
                    owner.getLifecycle().removeObserver(this);
                    return;
                }

                activeStateChanged(isActiveState(source.getLifecycle().getCurrentState()));
            }

            private void activeStateChanged(boolean active) {
                if (active == mActive) {
                    return;
                }

                mActive = active;
                boolean wasInactive = mActiveCount == 0;
                mActiveCount += active ? 1 : -1;
                if (wasInactive && active) {
                    onActive();
                }
                if (mActiveCount == 0 && !active) {
                    onInactive();
                }
            }
        });
    }

    private static boolean isActiveState(Lifecycle.State state) {
        return state.isAtLeast(STARTED);
    }

    public abstract void onActive();

    public abstract void onInactive();
}
