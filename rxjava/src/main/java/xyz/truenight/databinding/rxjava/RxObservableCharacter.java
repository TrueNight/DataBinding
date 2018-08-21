package xyz.truenight.databinding.rxjava;

import android.databinding.ObservableChar;

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

public class RxObservableCharacter extends ObservableChar {

    private Set<OnPropertyChangedCallback> count = new CopyOnWriteArraySet<>();

    private Disposable subscription;

    private Observable<Optional<Character>> observable;

    public RxObservableCharacter(Observable<Optional<Character>> observable) {
        super();
        this.observable = observable;
    }

    public RxObservableCharacter(char value, Observable<Optional<Character>> observable) {
        super(value);
        this.observable = observable;
    }

    public RxObservableCharacter(BehaviorSubject<Optional<Character>> subject) {
        super(safe(subject.getValue()));
        this.observable = subject;
    }

    private static char safe(Optional<Character> value) {
        return Utils.safe(value == null ? null : value.orElse(null), '\u0000');
    }

    @Deprecated
    @Override
    public void set(char value) {
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
