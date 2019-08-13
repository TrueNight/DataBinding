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

class RxObservableLong internal constructor(val observable: Observable<Long>, default: Long) : ObservableLong(default) {

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
        internal fun safe(value: Optional<Long>?) = value?.value.safe()
    }
}


@JvmOverloads
fun Observable<Long>.toBinding(default: Long = 0) =
        RxObservableLong(this, default)

@JvmOverloads
fun Observable<Optional<Long>>.toBindingOptional(default: Long = 0) =
        RxObservableLong(this.map { RxObservableLong.safe(it) }, default)

@JvmOverloads
fun Flowable<Long>.toBinding(default: Long = 0) =
        RxObservableLong(this.toObservable(), default)

@JvmOverloads
fun Flowable<Optional<Long>>.toBindingOptional(default: Long = 0) =
        RxObservableLong(this.map { RxObservableLong.safe(it) }.toObservable(), default)
