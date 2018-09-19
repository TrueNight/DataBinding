package xyz.truenight.databinding.rxjava;

import android.databinding.BaseObservable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableByte;
import android.databinding.ObservableChar;
import android.databinding.ObservableDouble;
import android.databinding.ObservableField;
import android.databinding.ObservableFloat;
import android.databinding.ObservableInt;
import android.databinding.ObservableList;
import android.databinding.ObservableLong;
import android.databinding.ObservableMap;
import android.databinding.ObservableShort;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import xyz.truenight.utils.Optional;

/**
 * Copyright (C) 2017 Mikhail Frolov
 */

public class RxDataBinding {
    private RxDataBinding() {
    }

    public static Observable<Boolean> asObservable(ObservableBoolean field) {
        return Observable.create(emitter -> {
            android.databinding.Observable.OnPropertyChangedCallback callback = new android.databinding.Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(android.databinding.Observable sender, int propertyId) {
                    emitter.onNext(field.get());
                }
            };
            emitter.setCancellable(() -> field.removeOnPropertyChangedCallback(callback));
            emitter.onNext(field.get());
            field.addOnPropertyChangedCallback(callback);
        });
    }

    public static Observable<Integer> asObservable(ObservableInt field) {
        return Observable.create(emitter -> {
            android.databinding.Observable.OnPropertyChangedCallback callback = new android.databinding.Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(android.databinding.Observable sender, int propertyId) {
                    emitter.onNext(field.get());
                }
            };
            emitter.setCancellable(() -> field.removeOnPropertyChangedCallback(callback));
            emitter.onNext(field.get());
            field.addOnPropertyChangedCallback(callback);
        });
    }

    public static Observable<Long> asObservable(ObservableLong field) {
        return Observable.create(emitter -> {
            android.databinding.Observable.OnPropertyChangedCallback callback = new android.databinding.Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(android.databinding.Observable sender, int propertyId) {
                    emitter.onNext(field.get());
                }
            };
            emitter.setCancellable(() -> field.removeOnPropertyChangedCallback(callback));
            emitter.onNext(field.get());
            field.addOnPropertyChangedCallback(callback);
        });
    }

    public static Observable<Short> asObservable(ObservableShort field) {
        return Observable.create(emitter -> {
            android.databinding.Observable.OnPropertyChangedCallback callback = new android.databinding.Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(android.databinding.Observable sender, int propertyId) {
                    emitter.onNext(field.get());
                }
            };
            emitter.setCancellable(() -> field.removeOnPropertyChangedCallback(callback));
            emitter.onNext(field.get());
            field.addOnPropertyChangedCallback(callback);
        });
    }

    public static Observable<Float> asObservable(ObservableFloat field) {
        return Observable.create(emitter -> {
            android.databinding.Observable.OnPropertyChangedCallback callback = new android.databinding.Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(android.databinding.Observable sender, int propertyId) {
                    emitter.onNext(field.get());
                }
            };
            emitter.setCancellable(() -> field.removeOnPropertyChangedCallback(callback));
            emitter.onNext(field.get());
            field.addOnPropertyChangedCallback(callback);
        });
    }

    public static Observable<Double> asObservable(ObservableDouble field) {
        return Observable.create(emitter -> {
            android.databinding.Observable.OnPropertyChangedCallback callback = new android.databinding.Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(android.databinding.Observable sender, int propertyId) {
                    emitter.onNext(field.get());
                }
            };
            emitter.setCancellable(() -> field.removeOnPropertyChangedCallback(callback));
            emitter.onNext(field.get());
            field.addOnPropertyChangedCallback(callback);
        });
    }

    public static Observable<Character> asObservable(ObservableChar field) {
        return Observable.create(emitter -> {
            android.databinding.Observable.OnPropertyChangedCallback callback = new android.databinding.Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(android.databinding.Observable sender, int propertyId) {
                    emitter.onNext(field.get());
                }
            };
            emitter.setCancellable(() -> field.removeOnPropertyChangedCallback(callback));
            emitter.onNext(field.get());
            field.addOnPropertyChangedCallback(callback);
        });
    }

    public static Observable<Byte> asObservable(ObservableByte field) {
        return Observable.create(emitter -> {
            android.databinding.Observable.OnPropertyChangedCallback callback = new android.databinding.Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(android.databinding.Observable sender, int propertyId) {
                    emitter.onNext(field.get());
                }
            };
            emitter.setCancellable(() -> field.removeOnPropertyChangedCallback(callback));
            emitter.onNext(field.get());
            field.addOnPropertyChangedCallback(callback);
        });
    }

    public static <T> Observable<Optional<T>> asObservable(ObservableField<T> field) {
        return Observable.create(emitter -> {
            android.databinding.Observable.OnPropertyChangedCallback callback = new android.databinding.Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(android.databinding.Observable sender, int propertyId) {
                    emitter.onNext(Optional.ofNullable(field.get()));
                }
            };
            emitter.setCancellable(() -> field.removeOnPropertyChangedCallback(callback));
            emitter.onNext(Optional.ofNullable(field.get()));
            field.addOnPropertyChangedCallback(callback);
        });
    }

    public static <T extends BaseObservable> Observable<T> asObservable(T field) {
        return Observable.create(emitter -> {
            android.databinding.Observable.OnPropertyChangedCallback callback = new android.databinding.Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(android.databinding.Observable sender, int propertyId) {
                    emitter.onNext(field);
                }
            };
            emitter.setCancellable(() -> field.removeOnPropertyChangedCallback(callback));
            emitter.onNext(field);
            field.addOnPropertyChangedCallback(callback);
        });
    }

    public static class ListChange<T> {
        public static final int ALL = 0;
        public static final int CHANGED = 1;
        public static final int INSERTED = 2;
        public static final int MOVED = 3;
        public static final int REMOVED = 4;

        private ObservableList<T> list;
        private int action;
        public int start;
        public int count;
        public int to;

        public ObservableList<T> getList() {
            return list;
        }

        @Action
        public int getAction() {
            return action;
        }

        public boolean isAction(@Action int action) {
            return this.action == action;
        }

        public int getStart() {
            return start;
        }

        public int getCount() {
            return count;
        }

        public int getTo() {
            return to;
        }

        public ListChange(ObservableList<T> ts) {
            list = ts;
        }

        public static <T> ListChange<T> changed(ObservableList<T> ts, int start, int count) {
            ListChange<T> change = new ListChange<>(ts);
            change.action = CHANGED;
            change.start = start;
            change.count = count;
            return change;
        }

        public static <T> ListChange<T> moved(ObservableList<T> ts, int start, int to, int count) {
            ListChange<T> change = new ListChange<>(ts);
            change.action = MOVED;
            change.start = start;
            change.to = to;
            change.count = count;
            return change;
        }

        public static <T> ListChange<T> inserted(ObservableList<T> ts, int start, int count) {
            ListChange<T> change = new ListChange<>(ts);
            change.action = INSERTED;
            change.start = start;
            change.count = count;
            return change;
        }

        public static <T> ListChange<T> removed(ObservableList<T> ts, int start, int count) {
            ListChange<T> change = new ListChange<>(ts);
            change.action = REMOVED;
            change.start = start;
            change.count = count;
            return change;
        }

        @IntDef({ALL, CHANGED, INSERTED, MOVED, REMOVED})
        @Retention(RetentionPolicy.SOURCE)
        public @interface Action {

        }
    }

    public static <T> Observable<ListChange<T>> asObservable(ObservableList<T> list) {
        return Observable.create(emitter -> {
            ObservableList.OnListChangedCallback<ObservableList<T>> callback = new ObservableList.OnListChangedCallback<ObservableList<T>>() {
                @Override
                public void onChanged(ObservableList<T> ts) {
                    next(new ListChange<>(ts));
                }

                @Override
                public void onItemRangeChanged(ObservableList<T> ts, int i, int i1) {
                    next(ListChange.changed(ts, i, i1));
                }

                @Override
                public void onItemRangeInserted(ObservableList<T> ts, int i, int i1) {
                    next(ListChange.inserted(ts, i, i1));
                }

                @Override
                public void onItemRangeMoved(ObservableList<T> ts, int i, int i1, int i2) {
                    next(ListChange.moved(ts, i, i1, i2));
                }

                @Override
                public void onItemRangeRemoved(ObservableList<T> ts, int i, int i1) {
                    next(ListChange.removed(ts, i, i1));
                }

                private void next(ListChange<T> change) {
                    emitter.onNext(change);
                }
            };
            emitter.setCancellable(() -> list.removeOnListChangedCallback(callback));
            emitter.onNext(new ListChange<>(list));
            list.addOnListChangedCallback(callback);
        });
    }

    public static <K, V> Observable<ObservableMap<K, V>> asObservable(ObservableMap<K, V> field) {
        return Observable.create(emitter -> {
            ObservableMap.OnMapChangedCallback<ObservableMap<K, V>, K, V> callback = new ObservableMap.OnMapChangedCallback<ObservableMap<K, V>, K, V>() {
                @Override
                public void onMapChanged(ObservableMap<K, V> sender, K key) {
                    emitter.onNext(field);
                }
            };
            emitter.setCancellable(() -> {
                field.removeOnMapChangedCallback(callback);
            });
            emitter.onNext(field);
            field.addOnMapChangedCallback(callback);
        });
    }

    public static <T> ObservableTransformer<T, T> loadingNext(ObservableBoolean loading) {
        return observable -> observable
                .doOnSubscribe((d) -> loading.set(true))
                .doOnNext((t) -> loading.set(false));
    }

    public static <T> ObservableTransformer<T, T> error(ObservableBoolean error) {
        return observable -> observable
                .doOnNext((t) -> error.set(false))
                .doOnError((th) -> error.set(true));
    }

    public static <T> void setField(ObservableField<T> field, T data) {
        if (field.get() != data) {
            field.set(data);
        } else {
            field.notifyChange();
        }
    }

    public static void setField(ObservableBoolean field, boolean data) {
        if (field.get() != data) {
            field.set(data);
        } else {
            field.notifyChange();
        }
    }
}