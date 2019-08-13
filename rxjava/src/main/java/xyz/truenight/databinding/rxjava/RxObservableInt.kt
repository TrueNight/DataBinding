package xyz.truenight.databinding.rxjava

import androidx.databinding.ObservableInt
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import xyz.truenight.utils.optional.Optional
import xyz.truenight.utils.optional.safe
import java.util.concurrent.CopyOnWriteArraySet

/**
 * Copyright (C) 2017 Mikhail Frolov
 */

class RxObservableInt internal constructor(val observable: Observable<Int>, default: Int) : ObservableInt(default) {

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

private fun Optional<Int>?.safe() = this?.value.safe()

fun Observable<Int>.toBinding(default: Int = 0) =
        RxObservableInt(this, default)

fun Observable<Optional<Int>>.toBinding(default: Optional<Int> = Optional.empty()) =
        RxObservableInt(this.map { it.safe() }, default.safe())

fun Flowable<Int>.toBinding(default: Int = 0) =
        RxObservableInt(this.toObservable(), default)

fun Flowable<Optional<Int>>.toBinding(default: Optional<Int> = Optional.empty()) =
        RxObservableInt(this.map { it.safe() }.toObservable(), default.safe())
