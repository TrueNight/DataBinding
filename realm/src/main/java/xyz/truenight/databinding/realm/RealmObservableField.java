package xyz.truenight.databinding.realm;

import android.databinding.ObservableField;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

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

    private final Set<OnPropertyChangedCallback> count = new CopyOnWriteArraySet<>();

    private static final Set<String> EXCLUDE = new HashSet<>();

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
    private final RealmChangeListener<RealmResults<T>> listener;

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
        super.set(value);
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

    public static void excludeCallback(Class<? extends OnPropertyChangedCallback> cls) {
        EXCLUDE.add(cls.getName());
    }

    public static void removeExclusion(Class<? extends OnPropertyChangedCallback> cls) {
        EXCLUDE.remove(cls.getName());
    }

    public static void excludeCallback(String className) {
        EXCLUDE.add(className);
    }

    public static void removeExclusion(String className) {
        EXCLUDE.remove(className);
    }

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        super.addOnPropertyChangedCallback(callback);
        if (!EXCLUDE.contains(callback.getClass().getName())) {
            if (count.isEmpty()) {
                closable = Realm.getInstance(realmConfig);
                RealmQuery<T> realmQuery = applyQuery.onApply(closable.where(cls));
                // workaround for NULL
                all = realmQuery.findAll();
                super.set(Utils.unmanage(realmConfig, Utils.first(all)));
                all.addChangeListener(listener);
            }
            count.add(callback);
        }
    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        super.removeOnPropertyChangedCallback(callback);
        count.remove(callback);
        if (count.isEmpty()) {
            all.removeChangeListener(listener);
            all = null;
            closable.close();
            closable = null;
        }
    }
}