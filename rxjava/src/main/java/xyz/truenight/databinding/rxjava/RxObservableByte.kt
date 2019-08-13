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

    companion object {
        internal fun safe(value: Optional<Byte>?) = value?.value.safe()
    }
}

@JvmOverloads
fun Observable<Byte>.toBinding(default: Byte = 0) =
        RxObservableByte(this, default)

@JvmOverloads
fun Observable<Optional<Byte>>.toBindingOptional(default: Byte = 0) =
        RxObservableByte(this.map { RxObservableByte.safe(it) }, default)

@JvmOverloads
fun Flowable<Byte>.toBinding(default: Byte = 0) =
        RxObservableByte(this.toObservable(), default)

@JvmOverloads
fun Flowable<Optional<Byte>>.toBindingOptional(default: Byte = 0) =
        RxObservableByte(this.map { RxObservableByte.safe(it) }.toObservable(), default)
