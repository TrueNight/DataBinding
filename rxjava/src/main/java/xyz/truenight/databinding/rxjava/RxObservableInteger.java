package xyz.truenight.databinding.rxjava;

import android.databinding.ObservableInt;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;
import xyz.truenight.utils.Optional;
import xyz.truenight.utils.Utils;

/**
 * Copyright (C) 2017 Mikhail Frolov
 */

public class RxObservableInteger extends ObservableInt {

    private Set<OnPropertyChangedCallback> count = new CopyOnWriteArraySet<>();

    private Disposable subscription;

    private Observable<Optional<Integer>> observable;

    public RxObservableInteger(Observable<Optional<Integer>> observable) {
        super();
        this.observable = observable;
    }

    public RxObservableInteger(int value, Observable<Optional<Integer>> observable) {
        super(value);
        this.observable = observable;
    }

    public RxObservableInteger(BehaviorSubject<Optional<Integer>> subject) {
        super(safe(subject.getValue()));
        this.observable = subject;
    }

    private static int safe(Optional<Integer> value) {
        return Utils.safe(value == null ? null : value.orElse(null));
    }

    @Deprecated
    @Override
    public void set(int value) {
        super.set(value);
    }

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        super.addOnPropertyChangedCallback(callback);
        if (count.isEmpty()) {
            subscription = observable.subscribe(value -> super.set(safe(value)));
        }
        count.add(callback);
    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        super.removeOnPropertyChangedCallback(callback);
        count.remove(callback);
        if (count.isEmpty()) {
            if (subscription != null && !subscription.isDisposed()) {
                subscription.dispose();
            }
        }
    }
}
