package xyz.truenight.databinding.rxjava

import androidx.databinding.ObservableLong
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import xyz.truenight.utils.optional.Optional
import xyz.truenight.utils.optional.safe
import java.util.concurrent.CopyOnWriteArraySet

/**
 * Copyright (C) 2017 Mikhail Frolov
 */

class RxObservableLong @JvmOverloads internal constructor(val observable: Observable<Long>, default: Long, private val setter: ((Long) -> Unit)? = null) : ObservableLong(default) {

    private val count = CopyOnWriteArraySet<androidx.databinding.Observable.OnPropertyChangedCallback>()

    private var subscription: Disposable? = null

    override fun set(value: Long) {
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

    companion object {
        internal fun safe(value: Optional<Long>?) = value?.value.safe()
    }
}

private fun Optional<Long>?.safe() = this?.value.safe()

fun Observable<Long>.toBinding(default: Long = 0, setter: ((Long) -> Unit)? = null) =
        RxObservableLong(this, default, setter)

fun Observable<Optional<Long>>.toBinding(default: Optional<Long> = Optional.empty(), setter: ((Long) -> Unit)? = null) =
        RxObservableLong(this.map { it.safe() }, default.safe(), setter)

fun Flowable<Long>.toBinding(default: Long = 0, setter: ((Long) -> Unit)? = null) =
        RxObservableLong(this.toObservable(), default, setter)

fun Flowable<Optional<Long>>.toBinding(default: Optional<Long> = Optional.empty(), setter: ((Long) -> Unit)? = null) =
        RxObservableLong(this.map { it.safe() }.toObservable(), default.safe(), setter)
