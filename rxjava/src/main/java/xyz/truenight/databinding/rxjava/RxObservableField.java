package xyz.truenight.databinding.rxjava;

import android.databinding.ObservableField;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;
import xyz.truenight.utils.Optional;

/**
 * Copyright (C) 2017 Mikhail Frolov
 */

public class RxObservableField<T> extends ObservableField<T> {

    private Set<OnPropertyChangedCallback> count = new CopyOnWriteArraySet<>();

    private Disposable subscription;

    private final Observable<Optional<T>> observable;

    public RxObservableField(Observable<Optional<T>> observable) {
        super();
        this.observable = observable;
    }

    public RxObservableField(T value, Observable<Optional<T>> observable) {
        super(value);
        this.observable = observable;
    }

    public RxObservableField(BehaviorSubject<Optional<T>> subject) {
        super(safe(subject.getValue()));
        this.observable = subject;
    }

    private static <T> T safe(Optional<T> value) {
        return value == null ? null : value.orElse(null);
    }

    private void safeSet(Optional<T> value) {
        super.set(safe(value));
    }

    @Deprecated
    @Override
    public void set(T value) {
        super.set(value);
    }

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        super.addOnPropertyChangedCallback(callback);
        if (count.isEmpty()) {
            subscription = observable.subscribe(this::safeSet);
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
