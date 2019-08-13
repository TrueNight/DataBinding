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

class RxObservableChar internal constructor(val observable: Observable<Char>, default: Char) : ObservableChar(default) {

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
}

private const val DEFAULT = '\u0000'

private fun Optional<Char>?.safe() = this?.value.safe { DEFAULT }

fun Observable<Char>.toBinding(default: Char = DEFAULT) =
        RxObservableChar(this, default)

fun Observable<Optional<Char>>.toBinding(default: Optional<Char> = Optional.empty()) =
        RxObservableChar(this.map { it.safe() }, default.safe())

fun Flowable<Char>.toBinding(default: Char = DEFAULT) =
        RxObservableChar(this.toObservable(), default)

fun Flowable<Optional<Char>>.toBinding(default: Optional<Char> = Optional.empty()) =
        RxObservableChar(this.map { it.safe() }.toObservable(), default.safe())
