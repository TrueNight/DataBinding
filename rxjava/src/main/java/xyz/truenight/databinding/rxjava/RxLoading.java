package xyz.truenight.databinding.rxjava;

import android.databinding.ObservableBoolean;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.ObservableTransformer;
import io.reactivex.disposables.Disposable;


public class RxLoading extends ObservableBoolean {

    private AtomicInteger mCount = new AtomicInteger();

    public static RxLoading create() {
        return new RxLoading();
    }

    private RxLoading() {

    }

    private void inc(Object disposable) {
        if (mCount.incrementAndGet() == 1) {
            super.set(true);
        }
    }

    private void dec() {
        if (mCount.decrementAndGet() == 0) {
            super.set(false);
        }
    }

    @Deprecated
    @Override
    public void set(boolean value) {

    }

    public <T> ObservableTransformer<T, T> transformer() {
        return upstream -> upstream
                .doOnSubscribe(this::inc)
                .doFinally(this::dec);
    }

    public <T> ObservableTransformer<T, T> transformerOnNext() {
        return upstream -> {
            AtomicBoolean loading = new AtomicBoolean();
            return upstream
                    .doOnSubscribe(disposable -> {
                        loadingStarted(loading, disposable);
                    })
                    .doOnNext(t -> {
                        loadingFinished(loading);
                    })
                    .doOnError(throwable -> {
                        loadingFinished(loading);
                    })
                    .doOnDispose(() -> {
                        loadingFinished(loading);
                    });
        };
    }

    private void loadingStarted(AtomicBoolean loading, Disposable disposable) {
        if (!loading.get()) {
            loading.set(true);
            inc(disposable);
        }
    }

    private void loadingFinished(AtomicBoolean loading) {
        if (loading.get()) {
            loading.set(false);
            dec();
        }
    }
}