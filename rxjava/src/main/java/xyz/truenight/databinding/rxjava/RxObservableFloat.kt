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

class RxObservableFloat @JvmOverloads internal constructor(val observable: Observable<Float>, default: Float, private val setter: ((Float) -> Unit)? = null) : ObservableFloat(default) {

    private val count = CopyOnWriteArraySet<androidx.databinding.Observable.OnPropertyChangedCallback>()

    private var subscription: Disposable? = null

    override fun set(value: Float) {
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

private fun Optional<Float>?.safe() = this?.value.safe()

fun Observable<Float>.toBinding(default: Float = 0f) =
        RxObservableFloat(this, default)

fun Observable<Optional<Float>>.toBinding(default: Optional<Float> = Optional.empty()) =
        RxObservableFloat(this.map { it.safe() }, default.safe())

fun Flowable<Float>.toBinding(default: Float = 0f) =
        RxObservableFloat(this.toObservable(), default)

fun Flowable<Optional<Float>>.toBinding(default: Optional<Float> = Optional.empty()) =
        RxObservableFloat(this.map { it.safe() }.toObservable(), default.safe())

