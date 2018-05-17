package xyz.truenight.databinding.rxjava;

import android.databinding.ObservableBoolean;

import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.ObservableTransformer;


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
        return upstream -> upstream
                .doOnSubscribe(this::inc)
                .doOnNext(next -> dec());
    }
}