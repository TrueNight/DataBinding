package xyz.truenight.databinding.rxjava

import androidx.databinding.ObservableFloat
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import xyz.truenight.utils.optional.Optional
import xyz.truenight.utils.optional.safe
import java.util.concurrent.CopyOnWriteArraySet

/**
 * Copyright (C) 2017 Mikhail Frolov
 */

class RxObservableFloat internal constructor(val observable: Observable<Float>, default: Float) : ObservableFloat(default) {

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
        internal fun safe(value: Optional<Float>?) = value?.value.safe()
    }
}

@JvmOverloads
fun Observable<Float>.toBinding(default: Float = 0f) =
        RxObservableFloat(this, default)

@JvmOverloads
fun Observable<Optional<Float>>.toBindingOptional(default: Float = 0f) =
        RxObservableFloat(this.map { RxObservableFloat.safe(it) }, default)

@JvmOverloads
fun Flowable<Float>.toBinding(default: Float = 0f) =
        RxObservableFloat(this.toObservable(), default)

@JvmOverloads
fun Flowable<Optional<Float>>.toBindingOptional(default: Float = 0f) =
        RxObservableFloat(this.map { RxObservableFloat.safe(it) }.toObservable(), default)

