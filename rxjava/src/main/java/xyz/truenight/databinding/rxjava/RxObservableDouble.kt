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
}

private fun Optional<Double>?.safe() = this?.value.safe()

fun Observable<Double>.toBinding(default: Double = 0.0) =
        RxObservableDouble(this, default)

fun Observable<Optional<Double>>.toBinding(default: Optional<Double> = Optional.empty()) =
        RxObservableDouble(this.map { it.safe() }, default.safe())

fun Flowable<Double>.toBinding(default: Double = 0.0) =
        RxObservableDouble(this.toObservable(), default)

fun Flowable<Optional<Double>>.toBinding(default: Optional<Double> = Optional.empty()) =
        RxObservableDouble(this.map { it.safe() }.toObservable(), default.safe())