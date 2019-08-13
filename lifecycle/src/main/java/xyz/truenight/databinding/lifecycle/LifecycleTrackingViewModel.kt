package xyz.truenight.databinding.lifecycle

import androidx.lifecycle.*

import androidx.lifecycle.Lifecycle.State.DESTROYED
import androidx.lifecycle.Lifecycle.State.STARTED

/**
 * Created by true
 * date: 02/09/2017
 * time: 23:17
 *
 *
 * Copyright © Mikhail Frolov
 */

abstract class LifecycleTrackingViewModel : ViewModel() {

    private var mActiveCount: Int = 0

    fun registerLifecycle(owner: LifecycleOwner) {
        owner.lifecycle.addObserver(object : LifecycleEventObserver {
            var mActive = false

            @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (source.lifecycle.currentState == DESTROYED) {
                    owner.lifecycle.removeObserver(this)
                    return
                }

                activeStateChanged(isActiveState(source.lifecycle.currentState))
            }

            private fun activeStateChanged(active: Boolean) {
                if (active == mActive) {
                    return
                }

                mActive = active
                val wasInactive = mActiveCount == 0
                mActiveCount += if (active) 1 else -1
                if (wasInactive && active) {
                    onActive()
                }
                if (mActiveCount == 0 && !active) {
                    onInactive()
                }
            }
        })
    }

    private fun isActiveState(state: Lifecycle.State): Boolean {
        return state.isAtLeast(STARTED)
    }

    abstract fun onActive()

    abstract fun onInactive()
}
