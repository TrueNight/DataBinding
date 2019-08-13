package xyz.truenight.databinding.rxjava

import androidx.databinding.ObservableBoolean
import io.reactivex.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger


class RxBoolean private constructor() : ObservableBoolean() {

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

        fun create(): RxBoolean {
            return RxBoolean()
        }
    }
}

fun <T> Maybe<T>.loading(loading: RxBoolean) =
        doOnSubscribe { loading.inc() }.doFinally { loading.dec() }

fun <T> Single<T>.loading(loading: RxBoolean) =
        doOnSubscribe { loading.inc() }.doFinally { loading.dec() }

fun <T> Flowable<T>.loading(loading: RxBoolean) =
        doOnSubscribe { loading.inc() }.doFinally { loading.dec() }

fun <T> Flowable<T>.loadingNext(loading: RxBoolean): Flowable<T> {
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

fun <T> Observable<T>.loading(loading: RxBoolean) =
        doOnSubscribe { loading.inc() }.doFinally { loading.dec() }

fun <T> Observable<T>.loadingNext(loading: RxBoolean): Observable<T> {
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

fun Completable.loading(loading: RxBoolean) =
        doOnSubscribe { loading.inc() }.doFinally { loading.dec() }

fun <T> Maybe<T>.error(error: RxBoolean): Maybe<T> {
    val isError = AtomicBoolean()
    return doOnSuccess {
        if (isError.get()) {
            isError.set(false)
            error.dec()
        }
    }.doOnError {
        if (!isError.get()) {
            isError.set(true)
            error.inc()
        }
    }
}

fun <T> Single<T>.error(error: RxBoolean): Single<T> {
    val isError = AtomicBoolean()
    return doOnSuccess {
        if (isError.get()) {
            isError.set(false)
            error.dec()
        }
    }.doOnError {
        if (!isError.get()) {
            isError.set(true)
            error.inc()
        }
    }
}

fun <T> Flowable<T>.error(error: RxBoolean): Flowable<T> {
    val isError = AtomicBoolean()
    return doOnNext {
        if (isError.get()) {
            isError.set(false)
            error.dec()
        }
    }.doOnError {
        if (!isError.get()) {
            isError.set(true)
            error.inc()
        }
    }
}

fun <T> Observable<T>.error(error: RxBoolean): Observable<T> {
    val isError = AtomicBoolean()
    return doOnNext {
        if (isError.get()) {
            isError.set(false)
            error.dec()
        }
    }.doOnError {
        if (!isError.get()) {
            isError.set(true)
            error.inc()
        }
    }
}

fun Completable.error(error: RxBoolean): Completable {
    val isError = AtomicBoolean()
    return doOnComplete {
        if (isError.get()) {
            isError.set(false)
            error.dec()
        }
    }.doOnError {
        if (!isError.get()) {
            isError.set(true)
            error.inc()
        }
    }
}