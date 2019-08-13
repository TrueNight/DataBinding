package xyz.truenight.databinding.rxjava

import io.reactivex.*
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.functions.Predicate
import io.reactivex.processors.PublishProcessor
import java.util.concurrent.TimeUnit

/**
 * Created by true
 * date: 17/11/2017
 * time: 20:19
 *
 *
 * Copyright © Mikhail Frolov
 */

class RxRefresh private constructor() : Action {

    internal val refresh = PublishProcessor.create<Boolean>().toSerialized()

    internal var handler: Consumer<Throwable>? = null

    fun errorHandler(handler: Consumer<Throwable>): RxRefresh {
        this.handler = handler
        return this
    }

    fun <T> transformer(): ObservableTransformer<T, T> {
        return ObservableTransformer { observable ->
            observable.doOnError { handler?.accept(it) }
                    .retryWhen { it.flatMap { refresh.firstElement().toObservable() } }
                    .repeatWhen { it.flatMap { refresh.firstElement().toObservable() } }
        }
    }

    fun <T> mergedTransformer(vararg toMerge: RxRefresh): ObservableTransformer<T, T> {
        return mergeWith(*toMerge)
    }

    fun <T> transformerWithCondition(predicate: Predicate<Boolean>): ObservableTransformer<T, T> {
        return ObservableTransformer { observable ->
            observable.doOnError { handler?.accept(it) }
                    .retryWhen { it.flatMap { refresh.filter(predicate).firstElement().toObservable() } }
                    .repeatWhen { it.flatMap { refresh.filter(predicate).firstElement().toObservable() } }
        }
    }

    override fun run() {
        refresh.onNext(false)
    }

    fun withDelay(delay: Long, timeUnit: TimeUnit): Action {
        return Action { Observable.timer(delay, timeUnit).doFinally(this).subscribe() }
    }

    companion object {

        fun create(): RxRefresh {
            return RxRefresh()
        }

        private fun <T> RxRefresh.mergeWith(vararg toMerge: RxRefresh): ObservableTransformer<T, T> {

            val merge = Flowable.merge(
                    mutableListOf<Flowable<Boolean>>().apply {
                        add(refresh)
                        toMerge.mapTo(this) { it.refresh }
                    })
            return ObservableTransformer { observable ->
                // handler will be taken from primary RxRefresh
                observable.doOnError { handler?.accept(it) }
                        .retryWhen { it.flatMap { merge.firstElement().toObservable() } }
                        .repeatWhen { it.flatMap { merge.firstElement().toObservable() } }
            }
        }
    }
}

fun <T> Single<T>.refresh(refresh: RxRefresh) =
        doOnError { refresh.handler?.accept(it) }
                .retryWhen { it.flatMap { refresh.refresh.firstElement().toFlowable() } }
                .repeatWhen { it.flatMap { refresh.refresh.firstElement().toFlowable() } }

fun <T> Flowable<T>.refresh(refresh: RxRefresh) =
        doOnError { refresh.handler?.accept(it) }
                .retryWhen { it.flatMap { refresh.refresh.firstElement().toFlowable() } }
                .repeatWhen { it.flatMap { refresh.refresh.firstElement().toFlowable() } }

fun <T> Observable<T>.refresh(refresh: RxRefresh) =
        doOnError { refresh.handler?.accept(it) }
                .retryWhen { it.flatMap { refresh.refresh.firstElement().toObservable() } }
                .repeatWhen { it.flatMap { refresh.refresh.firstElement().toObservable() } }

fun Completable.refresh(refresh: RxRefresh) =
        doOnError { refresh.handler?.accept(it) }
                .retryWhen { it.flatMap { refresh.refresh.firstElement().toFlowable() } }
                .repeatWhen { it.flatMap { refresh.refresh.firstElement().toFlowable() } }
