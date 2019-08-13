package xyz.truenight.databinding.rxjava

import androidx.databinding.ObservableField
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import xyz.truenight.utils.optional.Optional
import xyz.truenight.utils.optional.toOptional
import java.util.concurrent.CopyOnWriteArraySet

/**
 * Copyright (C) 2017 Mikhail Frolov
 */

class RxObservableField<T : Any> internal constructor(private val observable: Observable<Optional<T>>, value: T? = null) : ObservableField<T?>(value) {

    private val count = CopyOnWriteArraySet<androidx.databinding.Observable.OnPropertyChangedCallback>()

    private var subscription: Disposable? = null

    override fun addOnPropertyChangedCallback(callback: androidx.databinding.Observable.OnPropertyChangedCallback) {
        super.addOnPropertyChangedCallback(callback)
        if (count.isEmpty()) {
            subscription = observable.subscribe { super.set(it.value) }
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

fun <T : Any> Observable<Optional<T>>.toBinding(default: Optional<T> = Optional.empty()) =
        RxObservableField(this, default.value)

fun <T : Any> Flowable<Optional<T>>.toBinding(default: Optional<T> = Optional.empty()) =
        RxObservableField(this.toObservable(), default.value)

fun <T : Any> Observable<T>.toBinding(default: T? = null) =
        RxObservableField(this.map { it.toOptional() }, default)

fun <T : Any> Flowable<T>.toBinding(default: T? = null) =
        RxObservableField(this.toObservable().map { it.toOptional() }, default)
