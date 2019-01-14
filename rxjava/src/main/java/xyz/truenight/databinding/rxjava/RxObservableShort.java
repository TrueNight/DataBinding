package xyz.truenight.databinding.rxjava;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import androidx.databinding.ObservableShort;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;
import xyz.truenight.utils.Optional;
import xyz.truenight.utils.Utils;

/**
 * Copyright (C) 2017 Mikhail Frolov
 */

public class RxObservableShort extends ObservableShort {

    private Set<OnPropertyChangedCallback> count = new CopyOnWriteArraySet<>();

    private Disposable subscription;

    private Observable<Optional<Short>> observable;

    public RxObservableShort(Observable<Optional<Short>> observable) {
        super();
        this.observable = observable;
    }

    public RxObservableShort(short value, Observable<Optional<Short>> observable) {
        super(value);
        this.observable = observable;
    }

    public RxObservableShort(BehaviorSubject<Optional<Short>> subject) {
        super(safe(subject.getValue()));
        this.observable = subject;
    }

    private static short safe(Optional<Short> value) {
        return Utils.safe(value == null ? null : value.orElse(null));
    }

    @Deprecated
    @Override
    public void set(short value) {
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
