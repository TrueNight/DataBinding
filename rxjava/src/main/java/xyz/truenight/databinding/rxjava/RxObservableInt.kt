package xyz.truenight.databinding.rxjava

import androidx.databinding.ObservableInt
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import xyz.truenight.utils.optional.Optional
import xyz.truenight.utils.optional.safe
import java.util.concurrent.CopyOnWriteArraySet

/**
 * Copyright (C) 2017 Mikhail Frolov
 */

class RxObservableInt internal constructor(val observable: Observable<Int>, default: Int) : ObservableInt(default) {

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
        internal fun safe(value: Optional<Int>?) = value?.value.safe()
    }
}

@JvmOverloads
fun Observable<Int>.toBinding(default: Int = 0) =
        RxObservableInt(this, default)

@JvmOverloads
fun Observable<Optional<Int>>.toBindingOptional(default: Int = 0) =
        RxObservableInt(this.map { RxObservableInt.safe(it) }, default)

@JvmOverloads
fun Flowable<Int>.toBinding(default: Int = 0) =
        RxObservableInt(this.toObservable(), default)

@JvmOverloads
fun Flowable<Optional<Int>>.toBindingOptional(default: Int = 0) =
        RxObservableInt(this.map { RxObservableInt.safe(it) }.toObservable(), default)
