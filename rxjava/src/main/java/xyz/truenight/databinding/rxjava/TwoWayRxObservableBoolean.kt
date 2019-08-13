package xyz.truenight.databinding.rxjava

import androidx.databinding.ObservableBoolean
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.processors.FlowableProcessor
import io.reactivex.processors.PublishProcessor
import java.util.concurrent.CopyOnWriteArraySet

/**
 * Copyright (C) 2017 Mikhail Frolov
 */

class TwoWayRxObservableBoolean(private val observable: Observable<Boolean>, value: Boolean = false) : ObservableBoolean(value) {

    private val count = CopyOnWriteArraySet<androidx.databinding.Observable.OnPropertyChangedCallback>()

    private var subscription: Disposable? = null

    private val processor: FlowableProcessor<Boolean> = PublishProcessor.create()

    override fun set(value: Boolean) {
        if (value != get()) {
            super.set(value)
            processor.onNext(value)
        }
    }

    fun silentSet(value: Boolean) {
        super.set(value)
    }

    /**
     * Returns Observable which emits only set calls
     */
    fun asFlowable(): Flowable<Boolean> {
        return processor
    }

    override fun addOnPropertyChangedCallback(callback: androidx.databinding.Observable.OnPropertyChangedCallback) {
        super.addOnPropertyChangedCallback(callback)
        if (count.isEmpty()) {
            subscription = observable.subscribe { value -> silentSet(value) }
        }
        count.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: androidx.databinding.Observable.OnPropertyChangedCallback) {
        super.removeOnPropertyChangedCallback(callback)
        count.remove(callback)
        if (count.size == 0) {
            subscription?.dispose()
        }
    }
}
