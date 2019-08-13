@file:JvmName("RxDataBinding")

package xyz.truenight.databinding.rxjava


import androidx.databinding.*
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import xyz.truenight.utils.optional.Optional
import xyz.truenight.utils.optional.toOptional

/**
 * Copyright (C) 2017 Mikhail Frolov
 */

fun ObservableBoolean.asFlowable(): Flowable<Boolean> {
    return Flowable.create({ emitter ->
        val callback = object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable, propertyId: Int) {
                emitter.onNext(get())
            }
        }
        emitter.setCancellable { removeOnPropertyChangedCallback(callback) }
        emitter.onNext(get())
        addOnPropertyChangedCallback(callback)
    }, BackpressureStrategy.LATEST)
}

fun ObservableInt.asFlowable(): Flowable<Int> {
    return Flowable.create({ emitter ->
        val callback = object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable, propertyId: Int) {
                emitter.onNext(get())
            }
        }
        emitter.setCancellable { removeOnPropertyChangedCallback(callback) }
        emitter.onNext(get())
        addOnPropertyChangedCallback(callback)
    }, BackpressureStrategy.LATEST)
}

fun ObservableLong.asFlowable(): Flowable<Long> {
    return Flowable.create({ emitter ->
        val callback = object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable, propertyId: Int) {
                emitter.onNext(get())
            }
        }
        emitter.setCancellable { removeOnPropertyChangedCallback(callback) }
        emitter.onNext(get())
        addOnPropertyChangedCallback(callback)
    }, BackpressureStrategy.LATEST)
}

fun ObservableShort.asFlowable(): Flowable<Short> {
    return Flowable.create({ emitter ->
        val callback = object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable, propertyId: Int) {
                emitter.onNext(get())
            }
        }
        emitter.setCancellable { removeOnPropertyChangedCallback(callback) }
        emitter.onNext(get())
        addOnPropertyChangedCallback(callback)
    }, BackpressureStrategy.LATEST)
}

fun ObservableFloat.asFlowable(): Flowable<Float> {
    return Flowable.create({ emitter ->
        val callback = object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable, propertyId: Int) {
                emitter.onNext(get())
            }
        }
        emitter.setCancellable { removeOnPropertyChangedCallback(callback) }
        emitter.onNext(get())
        addOnPropertyChangedCallback(callback)
    }, BackpressureStrategy.LATEST)
}

fun ObservableDouble.asFlowable(): Flowable<Double> {
    return Flowable.create({ emitter ->
        val callback = object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable, propertyId: Int) {
                emitter.onNext(get())
            }
        }
        emitter.setCancellable { removeOnPropertyChangedCallback(callback) }
        emitter.onNext(get())
        addOnPropertyChangedCallback(callback)
    }, BackpressureStrategy.LATEST)
}

fun ObservableChar.asFlowable(): Flowable<Char> {
    return Flowable.create({ emitter ->
        val callback = object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable, propertyId: Int) {
                emitter.onNext(get())
            }
        }
        emitter.setCancellable { removeOnPropertyChangedCallback(callback) }
        emitter.onNext(get())
        addOnPropertyChangedCallback(callback)
    }, BackpressureStrategy.LATEST)
}

fun ObservableByte.asFlowable(): Flowable<Byte> {
    return Flowable.create({ emitter ->
        val callback = object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable, propertyId: Int) {
                emitter.onNext(get())
            }
        }
        emitter.setCancellable { removeOnPropertyChangedCallback(callback) }
        emitter.onNext(get())
        addOnPropertyChangedCallback(callback)
    }, BackpressureStrategy.LATEST)
}

fun <T : Any> ObservableField<T?>.asFlowable(): Flowable<Optional<T>> {
    return Flowable.create({ emitter ->
        val callback = object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable, propertyId: Int) {
                emitter.onNext(get().toOptional())
            }
        }
        emitter.setCancellable { removeOnPropertyChangedCallback(callback) }
        emitter.onNext(get().toOptional())
        addOnPropertyChangedCallback(callback)
    }, BackpressureStrategy.LATEST)
}

fun <T : BaseObservable> T.asFlowable(): Flowable<T> {
    return Flowable.create({ emitter ->
        val callback = object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable, propertyId: Int) {
                emitter.onNext(this@asFlowable)
            }
        }
        emitter.setCancellable { removeOnPropertyChangedCallback(callback) }
        emitter.onNext(this)
        addOnPropertyChangedCallback(callback)
    }, BackpressureStrategy.LATEST)
}

class ListChange<T> private constructor(val list: ObservableList<T>, val action: Action = Action.ALL, val start: Int = 0, val count: Int = 0, val to: Int = 0) {

    fun isAction(action: Action): Boolean {
        return this.action == action
    }

    companion object {

        internal fun <T> all(ts: ObservableList<T>) = ListChange(ts)

        internal fun <T> changed(ts: ObservableList<T>, start: Int, count: Int) =
                ListChange(
                        list = ts,
                        action = Action.CHANGED,
                        start = start,
                        count = count
                )

        internal fun <T> moved(ts: ObservableList<T>, start: Int, to: Int, count: Int) =
                ListChange(
                        list = ts,
                        action = Action.MOVED,
                        start = start,
                        to = to,
                        count = count
                )

        internal fun <T> inserted(ts: ObservableList<T>, start: Int, count: Int) =
                ListChange(
                        list = ts,
                        action = Action.INSERTED,
                        start = start,
                        count = count
                )

        internal fun <T> removed(ts: ObservableList<T>, start: Int, count: Int) =
                ListChange(
                        list = ts,
                        action = Action.REMOVED,
                        start = start,
                        count = count
                )
    }
}

enum class Action {
    ALL,
    CHANGED,
    INSERTED,
    MOVED,
    REMOVED
}

fun <T> ObservableList<T>.asFlowableChanges(): Flowable<ListChange<T>> = Flowable.create({ emitter ->
    val callback = object : ObservableList.OnListChangedCallback<ObservableList<T>>() {
        override fun onChanged(ts: ObservableList<T>) = next(ListChange.all(ts))

        override fun onItemRangeChanged(ts: ObservableList<T>, i: Int, i1: Int) =
                next(ListChange.changed(ts, i, i1))

        override fun onItemRangeInserted(ts: ObservableList<T>, i: Int, i1: Int) =
                next(ListChange.inserted(ts, i, i1))

        override fun onItemRangeMoved(ts: ObservableList<T>, i: Int, i1: Int, i2: Int) =
                next(ListChange.moved(ts, i, i1, i2))

        override fun onItemRangeRemoved(ts: ObservableList<T>, i: Int, i1: Int) =
                next(ListChange.removed(ts, i, i1))

        private fun next(change: ListChange<T>) = emitter.onNext(change)
    }
    emitter.setCancellable { removeOnListChangedCallback(callback) }
    emitter.onNext(ListChange.all(this))
    addOnListChangedCallback(callback)
}, BackpressureStrategy.LATEST)

fun <T> ObservableList<T>.asFlowable(): Flowable<ObservableList<T>> = Flowable.create({ emitter ->
    val callback = object : ObservableList.OnListChangedCallback<ObservableList<T>>() {
        override fun onChanged(ts: ObservableList<T>) = next(ts)

        override fun onItemRangeChanged(ts: ObservableList<T>, i: Int, i1: Int) = next(ts)

        override fun onItemRangeInserted(ts: ObservableList<T>, i: Int, i1: Int) = next(ts)

        override fun onItemRangeMoved(ts: ObservableList<T>, i: Int, i1: Int, i2: Int) = next(ts)

        override fun onItemRangeRemoved(ts: ObservableList<T>, i: Int, i1: Int) = next(ts)

        private fun next(change: ObservableList<T>) = emitter.onNext(change)
    }
    emitter.setCancellable { removeOnListChangedCallback(callback) }
    emitter.onNext(this)
    addOnListChangedCallback(callback)
}, BackpressureStrategy.LATEST)

fun <K, V> ObservableMap<K, V>.asFlowable(): Flowable<ObservableMap<K, V>> =
        Flowable.create({ emitter ->
            val callback = object : ObservableMap.OnMapChangedCallback<ObservableMap<K, V>, K, V>() {
                override fun onMapChanged(sender: ObservableMap<K, V>, key: K) {
                    emitter.onNext(this@asFlowable)
                }
            }
            emitter.setCancellable { removeOnMapChangedCallback(callback) }
            emitter.onNext(this)
            addOnMapChangedCallback(callback)
        }, BackpressureStrategy.LATEST)