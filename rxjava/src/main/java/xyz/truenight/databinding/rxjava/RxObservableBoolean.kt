package xyz.truenight.databinding.rxjava

import androidx.databinding.ObservableBoolean
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import xyz.truenight.utils.optional.Optional
import xyz.truenight.utils.optional.safe
import java.util.concurrent.CopyOnWriteArraySet

/**
 * Copyright (C) 2017 Mikhail Frolov
 */

class RxObservableBoolean @JvmOverloads internal constructor(val observable: Observable<Boolean>, default: Boolean = false, private val setter: ((Boolean) -> Unit)? = null) : ObservableBoolean(default) {

    private val count = CopyOnWriteArraySet<androidx.databinding.Observable.OnPropertyChangedCallback>()

    private var subscription: Disposable? = null

    override fun set(value: Boolean) {
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

private fun Optional<Boolean>?.safe() = this?.value.safe()

fun Observable<Boolean>.toBinding(default: Boolean = false, setter: ((Boolean) -> Unit)? = null) =
        RxObservableBoolean(this, default, setter)

fun Observable<Optional<Boolean>>.toBinding(default: Optional<Boolean> = Optional.empty(), setter: ((Boolean) -> Unit)? = null) =
        RxObservableBoolean(this.map { it.safe() }, default.safe(), setter)

fun Flowable<Boolean>.toBinding(default: Boolean = false, setter: ((Boolean) -> Unit)? = null) =
        RxObservableBoolean(this.toObservable(), default, setter)

fun Flowable<Optional<Boolean>>.toBinding(default: Optional<Boolean> = Optional.empty(), setter: ((Boolean) -> Unit)? = null) =
        RxObservableBoolean(this.map { it.safe() }.toObservable(), default.safe(), setter)
