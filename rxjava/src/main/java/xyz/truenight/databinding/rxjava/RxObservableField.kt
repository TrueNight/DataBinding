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

class RxObservableField<T : Any> @JvmOverloads internal constructor(private val observable: Observable<Optional<T>>, value: T? = null, private val setter: ((T?) -> Unit)? = null) : ObservableField<T?>(value) {

    private val count = CopyOnWriteArraySet<androidx.databinding.Observable.OnPropertyChangedCallback>()

    private var subscription: Disposable? = null

    override fun set(value: T?) {
        super.set(value)
        setter?.let { it(value) }
    }

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

fun <T : Any> Observable<Optional<T>>.toBinding(default: Optional<T> = Optional.empty(), setter: ((T?) -> Unit)? = null) =
        RxObservableField(this, default.value, setter)

fun <T : Any> Flowable<Optional<T>>.toBinding(default: Optional<T> = Optional.empty(), setter: ((T?) -> Unit)? = null) =
        RxObservableField(this.toObservable(), default.value, setter)

fun <T : Any> Observable<T>.toBinding(default: T? = null, setter: ((T?) -> Unit)? = null) =
        RxObservableField(this.map { it.toOptional() }, default, setter)

fun <T : Any> Flowable<T>.toBinding(default: T? = null, setter: ((T?) -> Unit)? = null) =
        RxObservableField(this.toObservable().map { it.toOptional() }, default, setter)
