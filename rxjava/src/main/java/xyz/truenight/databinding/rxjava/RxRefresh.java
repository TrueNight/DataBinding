package xyz.truenight.databinding.rxjava;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import xyz.truenight.utils.Utils;

/**
 * Created by true
 * date: 17/11/2017
 * time: 20:19
 * <p>
 * Copyright © Mikhail Frolov
 */

public class RxRefresh implements Action {

    private final Subject<Boolean> refresh = PublishSubject.<Boolean>create().toSerialized();

    private Consumer<Throwable> handler;

    private RxRefresh() {

    }

    public static RxRefresh create() {
        return new RxRefresh();
    }

    public static <T> ObservableTransformer<T, T> merge(RxRefresh refresh, RxRefresh... toMerge) {
        List<Subject<Boolean>> merged = new ArrayList<>();
        merged.add(refresh.refresh);
        if (Utils.isNotEmpty(toMerge)) {
            for (RxRefresh rxRefresh : toMerge) {
                merged.add(rxRefresh.refresh);
            }
        }
        Observable<Boolean> merge = Observable.merge(merged);
        return observable -> {
            // handler will be taken from primary RxRefresh
            if (refresh.handler != null) {
                observable = observable.doOnError(refresh.handler);
            }
            return observable
                    .retryWhen(o -> o.flatMap(t -> merge.firstElement().toObservable()))
                    .repeatWhen(o -> o.flatMap(t -> merge.firstElement().toObservable()));
        };
    }

    public RxRefresh errorHandler(Consumer<Throwable> handler) {
        this.handler = handler;
        return this;
    }

    public <T> ObservableTransformer<T, T> transformer() {
        return observable -> {
            if (handler != null) {
                observable = observable.doOnError(handler);
            }
            return observable
                    .retryWhen(o -> o.flatMap(t -> refresh.firstElement().toObservable()))
                    .repeatWhen(o -> o.flatMap(t -> refresh.firstElement().toObservable()));
        };
    }

    public <T> ObservableTransformer<T, T> mergedTransformer(RxRefresh... toMerge) {
        return merge(this, toMerge);
    }

    public <T> ObservableTransformer<T, T> transformerWithCondition(Predicate<Boolean> predicate) {
        return observable -> {
            if (handler != null) {
                observable = observable.doOnError(handler);
            }
            return observable
                    .retryWhen(o -> o.flatMap(t -> refresh
                            .takeWhile(predicate).firstElement().toObservable()))
                    .repeatWhen(o -> o.flatMap(t -> refresh.takeWhile(predicate).firstElement().toObservable()));
        };
    }

    @Override
    public void run() {
        refresh.onNext(false);
    }

    public Action withDelay(long delay, TimeUnit timeUnit) {
        return () -> Observable.timer(delay, timeUnit).doFinally(this).subscribe();
    }
}
