package xyz.truenight.databinding.rxjava

import androidx.databinding.ObservableDouble
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import xyz.truenight.utils.optional.Optional
import xyz.truenight.utils.optional.safe
import java.util.concurrent.CopyOnWriteArraySet

/**
 * Copyright (C) 2017 Mikhail Frolov
 */

class RxObservableDouble internal constructor(val observable: Observable<Double>, default: Double) : ObservableDouble(default) {

    private val count = CopyOnWriteArraySet<androidx.databinding.Observable.OnPropertyChangedCallback>()

    private var subscription: Disposable? = null

    override fun addOnPropertyChangedCallback(callback: androidx.databinding.Observable.OnPropertyChangedCallback) {
        super.addOnPropertyChangedCallback(callback)
        if (count.isEmpty()) {
            subscription = observable.subscribe { super.set(it) }
        }
        count.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: androidx.databinding.Observable.OnPropertyChangedCallback) {
        super.removeOnPropertyChangedCallback(callback)
        count.remove(callback)
        if (count.isEmpty()) {
            subscription?.dispose()
        }
    }

    companion object {
        internal fun safe(value: Optional<Double>?) = value?.value.safe()
    }
}

@JvmOverloads
fun Observable<Double>.toBinding(default: Double = 0.0) =
        RxObservableDouble(this, default)

@JvmOverloads
fun Observable<Optional<Double>>.toBindingOptional(default: Double = 0.0) =
        RxObservableDouble(this.map { RxObservableDouble.safe(it) }, default)

@JvmOverloads
fun Flowable<Double>.toBinding(default: Double = 0.0) =
        RxObservableDouble(this.toObservable(), default)

@JvmOverloads
fun Flowable<Optional<Double>>.toBindingOptional(default: Double = 0.0) =
        RxObservableDouble(this.map { RxObservableDouble.safe(it) }.toObservable(), default)