package xyz.truenight.databinding.rxjava

import androidx.databinding.ObservableChar
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import xyz.truenight.utils.optional.Optional
import xyz.truenight.utils.optional.safe
import java.util.concurrent.CopyOnWriteArraySet

/**
 * Copyright (C) 2017 Mikhail Frolov
 */

class RxObservableChar @JvmOverloads internal constructor(val observable: Observable<Char>, default: Char, private val setter: ((Char) -> Unit)? = null) : ObservableChar(default) {

    private val count = CopyOnWriteArraySet<androidx.databinding.Observable.OnPropertyChangedCallback>()

    private var subscription: Disposable? = null

    override fun set(value: Char) {
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

private const val DEFAULT = '\u0000'

private fun Optional<Char>?.safe() = this?.value.safe { DEFAULT }

fun Observable<Char>.toBinding(default: Char = DEFAULT, setter: ((Char) -> Unit)? = null) =
        RxObservableChar(this, default, setter)

fun Observable<Optional<Char>>.toBinding(default: Optional<Char> = Optional.empty(), setter: ((Char) -> Unit)? = null) =
        RxObservableChar(this.map { it.safe() }, default.safe(), setter)

fun Flowable<Char>.toBinding(default: Char = DEFAULT, setter: ((Char) -> Unit)? = null) =
        RxObservableChar(this.toObservable(), default, setter)

fun Flowable<Optional<Char>>.toBinding(default: Optional<Char> = Optional.empty(), setter: ((Char) -> Unit)? = null) =
        RxObservableChar(this.map { it.safe() }.toObservable(), default.safe(), setter)
