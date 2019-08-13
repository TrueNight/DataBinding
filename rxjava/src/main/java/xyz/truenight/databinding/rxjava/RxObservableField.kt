package xyz.truenight.databinding.rxjava

import androidx.databinding.ObservableField
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import xyz.truenight.utils.optional.Optional
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

@JvmOverloads
fun <T : Any> Observable<Optional<T>>.toBindingOptional(default: T? = null) =
        RxObservableField(this, default)

@JvmOverloads
fun <T : Any> Flowable<Optional<T>>.toBinding(default: T? = null) =
        RxObservableField(this.toObservable(), default)
