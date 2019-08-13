package xyz.truenight.databinding.rxjava

import androidx.databinding.ObservableByte
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import xyz.truenight.utils.optional.Optional
import xyz.truenight.utils.optional.safe
import java.util.concurrent.CopyOnWriteArraySet

/**
 * Copyright (C) 2017 Mikhail Frolov
 */

class RxObservableByte internal constructor(val observable: Observable<Byte>, default: Byte) : ObservableByte(default) {

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

private fun Optional<Byte>?.safe() = this?.value.safe()

fun Observable<Byte>.toBinding(default: Byte = 0) =
        RxObservableByte(this, default)

fun Observable<Optional<Byte>>.toBinding(default: Optional<Byte> = Optional.empty()) =
        RxObservableByte(this.map { it.safe() }, default.safe())

fun Flowable<Byte>.toBinding(default: Byte = 0) =
        RxObservableByte(this.toObservable(), default)

fun Flowable<Optional<Byte>>.toBinding(default: Optional<Byte> = Optional.empty()) =
        RxObservableByte(this.map { it.safe() }.toObservable(), default.safe())
