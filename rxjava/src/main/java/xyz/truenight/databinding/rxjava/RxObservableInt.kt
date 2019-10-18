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

class RxObservableInt @JvmOverloads internal constructor(val observable: Observable<Int>, default: Int, private val setter: ((Int) -> Unit)? = null) : ObservableInt(default) {

    private val count = CopyOnWriteArraySet<androidx.databinding.Observable.OnPropertyChangedCallback>()

    private var subscription: Disposable? = null

    override fun set(value: Int) {
        super.set(value)
        setter?.let { it(value) }
    }

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

fun Observable<Int>.toBinding(default: Int = 0, setter: ((Int) -> Unit)? = null) =
        RxObservableInt(this, default, setter)

fun Observable<Optional<Int>>.toBinding(default: Optional<Int> = Optional.empty(), setter: ((Int) -> Unit)? = null) =
        RxObservableInt(this.map { it.safe() }, default.safe(), setter)

fun Flowable<Int>.toBinding(default: Int = 0, setter: ((Int) -> Unit)? = null) =
        RxObservableInt(this.toObservable(), default, setter)

fun Flowable<Optional<Int>>.toBinding(default: Optional<Int> = Optional.empty(), setter: ((Int) -> Unit)? = null) =
        RxObservableInt(this.map { it.safe() }.toObservable(), default.safe(), setter)
