package xyz.truenight.databinding.rxjava;

import android.databinding.ObservableBoolean;

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

public class RxObservableBoolean extends ObservableBoolean {

    private Set<OnPropertyChangedCallback> count = new CopyOnWriteArraySet<>();

    private Disposable subscription;

    private Observable<Optional<Boolean>> observable;

    public RxObservableBoolean(Observable<Optional<Boolean>> observable) {
        super();
        this.observable = observable;
    }

    public RxObservableBoolean(boolean value, Observable<Optional<Boolean>> observable) {
        super(value);
        this.observable = observable;
    }

    public RxObservableBoolean(BehaviorSubject<Optional<Boolean>> subject) {
        super(safe(subject.getValue()));
        this.observable = subject;
    }

    private static Boolean safe(Optional<Boolean> value) {
        return Utils.safe(value == null ? null : value.orElse(null));
    }

    @Deprecated
    @Override
    public void set(boolean value) {
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
        if (count.size() == 0) {
            if (subscription != null && !subscription.isDisposed()) {
                subscription.dispose();
            }
        }
    }
}
