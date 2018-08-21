package xyz.truenight.databinding.rxjava;

import android.databinding.ObservableByte;

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

public class RxObservableByte extends ObservableByte {

    private Set<OnPropertyChangedCallback> count = new CopyOnWriteArraySet<>();

    private Disposable subscription;

    private Observable<Optional<Byte>> observable;

    public RxObservableByte(Observable<Optional<Byte>> observable) {
        super();
        this.observable = observable;
    }

    public RxObservableByte(byte value, Observable<Optional<Byte>> observable) {
        super(value);
        this.observable = observable;
    }

    public RxObservableByte(BehaviorSubject<Optional<Byte>> subject) {
        super(safe(subject.getValue()));
        this.observable = subject;
    }

    private static byte safe(Optional<Byte> value) {
        return Utils.safe(value == null ? null : value.orElse(null), (byte) 0);
    }

    @Deprecated
    @Override
    public void set(byte value) {
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
