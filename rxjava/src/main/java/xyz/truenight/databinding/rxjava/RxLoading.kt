package xyz.truenight.databinding.rxjava

import androidx.databinding.ObservableBoolean
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger


class RxLoading private constructor() : ObservableBoolean() {

    private val mCount = AtomicInteger()

    fun inc() {
        if (mCount.incrementAndGet() == 1) {
            super.set(true)
        }
    }

    fun dec() {
        if (mCount.decrementAndGet() == 0) {
            super.set(false)
        }
    }

    companion object {

        fun create(): RxLoading {
            return RxLoading()
        }
    }
}

fun <T> Single<T>.loading(loading: RxLoading) =
        doOnSubscribe { loading.inc() }.doFinally { loading.dec() }

fun <T> Flowable<T>.loading(loading: RxLoading) =
        doOnSubscribe { loading.inc() }.doFinally { loading.dec() }

fun <T> Flowable<T>.loadingNext(loading: RxLoading): Flowable<T> {
    val isLoading = AtomicBoolean()
    return doOnSubscribe {
        if (!isLoading.get()) {
            isLoading.set(true)
            loading.inc()
        }
    }.doOnNext {
        if (isLoading.get()) {
            isLoading.set(false)
            loading.dec()
        }
    }.doFinally {
        if (isLoading.get()) {
            isLoading.set(false)
            loading.dec()
        }
    }
}

fun <T> Observable<T>.loading(loading: RxLoading) =
        doOnSubscribe { loading.inc() }.doFinally { loading.dec() }

fun <T> Observable<T>.loadingNext(loading: RxLoading): Observable<T> {
    val isLoading = AtomicBoolean()
    return doOnSubscribe {
        if (!isLoading.get()) {
            isLoading.set(true)
            loading.inc()
        }
    }.doOnNext {
        if (isLoading.get()) {
            isLoading.set(false)
            loading.dec()
        }
    }.doFinally {
        if (isLoading.get()) {
            isLoading.set(false)
            loading.dec()
        }
    }
}

fun Completable.loading(loading: RxLoading) =
        doOnSubscribe { loading.inc() }.doFinally { loading.dec() }