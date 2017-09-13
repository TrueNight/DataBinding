package xyz.truenight.databinding.realm;

import android.databinding.ObservableField;

import java.util.concurrent.atomic.AtomicInteger;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmConfiguration;
import io.realm.RealmModel;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * An {@link ObservableField} that uses {@link RealmChangeListener} to calculate and dispatch it's change
 * updates.
 */
public class RealmObservableField<T extends RealmModel> extends ObservableField<T> {


    public static <E extends RealmModel> OnApplyQuery<E> all() {
        //noinspection unchecked
        return ALL;
    }

    private static OnApplyQuery ALL = new OnApplyQuery() {
        @Override
        public RealmQuery onApply(RealmQuery query) {
            return query;
        }
    };


    private final RealmConfiguration realmConfig;
    private final Class<T> cls;
    private final OnApplyQuery<T> applyQuery;
    private Realm closable;
    private RealmResults<T> all;
    private T item;
    private final RealmChangeListener<RealmResults<T>> listener;
    private AtomicInteger count = new AtomicInteger();

    /**
     * Creates a new AutoQueryRealmObservableField of type T.
     *
     * @param apply Realm query.
     */
    public RealmObservableField(Class<T> cls, OnApplyQuery<T> apply) {
        this(Realm.getDefaultConfiguration(), cls, apply);
    }

    /**
     * Creates a new AutoQueryRealmObservableField of type T.
     *
     * @param apply Realm query.
     */
    public RealmObservableField(RealmConfiguration realmConfig, Class<T> cls, OnApplyQuery<T> apply) {
        this.realmConfig = realmConfig;
        this.cls = cls;
        this.applyQuery = apply;
        this.listener = createListener();
    }

    @Deprecated
    @Override
    public void set(T value) {

    }

    private RealmChangeListener<RealmResults<T>> createListener() {
        return new RealmChangeListener<RealmResults<T>>() {
            @Override
            public void onChange(RealmResults<T> t) {
                if (t.isValid()) {
                    RealmObservableField.super.set(Utils.unmanage(realmConfig, Utils.first(t)));
                }
            }
        };
    }

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        super.addOnPropertyChangedCallback(callback);
        if (count.incrementAndGet() == 1) {
            closable = Realm.getInstance(realmConfig);
            RealmQuery<T> realmQuery = applyQuery.onApply(closable.where(cls));
            // workaround for NULL
            all = realmQuery.findAll();
            item = Utils.first(all);
            set(Utils.unmanage(realmConfig, item));
            all.addChangeListener(listener);
        }
    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        super.removeOnPropertyChangedCallback(callback);
        if (count.decrementAndGet() == 0) {
            all.removeChangeListener(listener);
            all = null;
            item = null;
            closable.close();
            closable = null;
        }
    }
}