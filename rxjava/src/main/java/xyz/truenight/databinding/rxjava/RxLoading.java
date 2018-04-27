package xyz.truenight.databinding.rxjava;

import android.databinding.ObservableBoolean;

import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.ObservableTransformer;


public class RxLoading extends ObservableBoolean {
    private ObservableTransformer transformer;
    private AtomicInteger mCount;

    public static RxLoading create() {
        return new RxLoading();
    }

    private RxLoading() {
        mCount = new AtomicInteger();
        transformer = observable -> observable
                .doOnSubscribe(this::inc)
                .doFinally(this::dec);
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

    @SuppressWarnings("unchecked")
    public <T> ObservableTransformer<T, T> transformer() {
        return transformer;
    }
}