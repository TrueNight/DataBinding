package xyz.truenight.databinding.rxjava

import androidx.databinding.ObservableShort
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import xyz.truenight.utils.optional.Optional
import xyz.truenight.utils.optional.safe
import java.util.concurrent.CopyOnWriteArraySet

/**
 * Copyright (C) 2017 Mikhail Frolov
 */

class RxObservableShort @JvmOverloads internal constructor(val observable: Observable<Short>, default: Short, private val setter: ((Short) -> Unit)? = null) : ObservableShort(default) {

    private val count = CopyOnWriteArraySet<androidx.databinding.Observable.OnPropertyChangedCallback>()

    private var subscription: Disposable? = null

    override fun set(value: Short) {
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
        internal fun safe(value: Optional<Short>?) = value?.value.safe()
    }
}

private fun Optional<Short>?.safe() = this?.value.safe()

fun Observable<Short>.toBinding(default: Short = 0, setter: ((Short) -> Unit)? = null) =
        RxObservableShort(this, default, setter)

fun Observable<Optional<Short>>.toBinding(default: Optional<Short> = Optional.empty(), setter: ((Short) -> Unit)? = null) =
        RxObservableShort(this.map { it.safe() }, default.safe(), setter)

fun Flowable<Short>.toBinding(default: Short = 0, setter: ((Short) -> Unit)? = null) =
        RxObservableShort(this.toObservable(), default, setter)

fun Flowable<Optional<Short>>.toBinding(default: Optional<Short> = Optional.empty(), setter: ((Short) -> Unit)? = null) =
        RxObservableShort(this.map { it.safe() }.toObservable(), default.safe(), setter)