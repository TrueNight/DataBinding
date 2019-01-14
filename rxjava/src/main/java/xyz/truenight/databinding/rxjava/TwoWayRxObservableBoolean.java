package xyz.truenight.databinding.rxjava;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import androidx.databinding.ObservableBoolean;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import xyz.truenight.utils.Optional;
import xyz.truenight.utils.Utils;

/**
 * Copyright (C) 2017 Mikhail Frolov
 */

public class TwoWayRxObservableBoolean extends ObservableBoolean {

    private Set<OnPropertyChangedCallback> count = new CopyOnWriteArraySet<>();

    private Disposable subscription;

    private final Observable<Optional<Boolean>> observable;
    private final Subject<Boolean> subject;

    public TwoWayRxObservableBoolean(Observable<Optional<Boolean>> observable) {
        super();
        this.observable = observable;
        this.subject = PublishSubject.create();
    }

    public TwoWayRxObservableBoolean(boolean value, Observable<Optional<Boolean>> observable) {
        super(value);
        this.observable = observable;
        this.subject = PublishSubject.create();
    }

    private static Boolean safe(Optional<Boolean> value) {
        return Utils.safe(value == null ? null : value.orElse(null));
    }

    @Override
    public void set(boolean value) {
        if (value != get()) {
            super.set(value);
            subject.onNext(value);
        }
    }

    public void silentSet(boolean value) {
        super.set(value);
    }

    /**
     * Returns Observable which emits only set calls
     */
    public Observable<Boolean> asObservable() {
        return subject;
    }

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        super.addOnPropertyChangedCallback(callback);
        if (count.isEmpty()) {
            subscription = observable.subscribe(value -> silentSet(safe(value)));
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
