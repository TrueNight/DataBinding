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

class RxObservableBoolean internal constructor(val observable: Observable<Boolean>, default: Boolean) : ObservableBoolean(default) {

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
        internal fun safe(value: Optional<Boolean>?) = value?.value.safe()
    }
}

@JvmOverloads
fun Observable<Boolean>.toBinding(default: Boolean = false) =
        RxObservableBoolean(this, default)

@JvmOverloads
fun Observable<Optional<Boolean>>.toBindingOptional(default: Boolean = false) =
        RxObservableBoolean(this.map { RxObservableBoolean.safe(it) }, default)

@JvmOverloads
fun Flowable<Boolean>.toBinding(default: Boolean = false) =
        RxObservableBoolean(this.toObservable(), default)

@JvmOverloads
fun Flowable<Optional<Boolean>>.toBindingOptional(default: Boolean = false) =
        RxObservableBoolean(this.map { RxObservableBoolean.safe(it) }.toObservable(), default)
