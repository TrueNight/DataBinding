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

class RxObservableShort internal constructor(val observable: Observable<Short>, default: Short) : ObservableShort(default) {

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
        internal fun safe(value: Optional<Short>?) = value?.value.safe()
    }
}

private fun Optional<Short>?.safe() = this?.value.safe()

fun Observable<Short>.toBinding(default: Short = 0) =
        RxObservableShort(this, default)

fun Observable<Optional<Short>>.toBinding(default: Optional<Short> = Optional.empty()) =
        RxObservableShort(this.map { it.safe() }, default.safe())

fun Flowable<Short>.toBinding(default: Short = 0) =
        RxObservableShort(this.toObservable(), default)

fun Flowable<Optional<Short>>.toBinding(default: Optional<Short> = Optional.empty()) =
        RxObservableShort(this.map { it.safe() }.toObservable(), default.safe())