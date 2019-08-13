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

    companion object {
        internal const val DEFAULT = '\u0000'

        internal fun safe(value: Optional<Char>?) = value?.value.safe { DEFAULT }
    }
}

@JvmOverloads
fun Observable<Char>.toBinding(default: Char = RxObservableChar.DEFAULT) =
        RxObservableChar(this, default)

@JvmOverloads
fun Observable<Optional<Char>>.toBindingOptional(default: Char = RxObservableChar.DEFAULT) =
        RxObservableChar(this.map { RxObservableChar.safe(it) }, default)

@JvmOverloads
fun Flowable<Char>.toBinding(default: Char = RxObservableChar.DEFAULT) =
        RxObservableChar(this.toObservable(), default)

@JvmOverloads
fun Flowable<Optional<Char>>.toBindingOptional(default: Char = RxObservableChar.DEFAULT) =
        RxObservableChar(this.map { RxObservableChar.safe(it) }.toObservable(), default)
