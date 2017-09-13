package xyz.truenight.databinding.realm;

import android.databinding.ListChangeRegistry;
import android.databinding.ObservableList;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollection;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * An {@link ObservableList} that uses {@link OrderedRealmCollectionChangeListener} to calculate and dispatch it's change
 * updates.
 */
public class RealmObservableList<T extends RealmModel> extends AbstractList<T> implements ObservableList<T> {

    public static <E extends RealmModel> OnApplyQuery<E> all() {
        //noinspection unchecked
        return RealmObservableField.all();
    }

    private final Class<T> cls;
    private final OnApplyQuery<T> applyQuery;
    private final RealmConfiguration realmConfig;
    private Realm realm;
    private OrderedRealmCollection<T> items;
    private final OrderedRealmCollectionChangeListener listener;
    private final ListChangeRegistry listeners = new ListChangeRegistry();
    private List<T> mirror;
    private boolean updateOnModification;
    private FindFunction<T> findFunction;

    /**
     * Creates a new AutoQueryRealmObservableList of type T.
     *
     * @param apply Realm query.
     */
    public RealmObservableList(Class<T> cls, OnApplyQuery<T> apply) {
        this(cls, apply, null);
    }

    /**
     * Creates a new AutoQueryRealmObservableList of type T.
     *
     * @param apply Realm query.
     */
    public RealmObservableList(RealmConfiguration configuration, Class<T> cls, OnApplyQuery<T> apply) {
        this(configuration, cls, apply, null);
    }

    /**
     * Creates a new AutoQueryRealmObservableList of type T.
     *
     * @param apply Realm query.
     */
    public RealmObservableList(Class<T> cls, OnApplyQuery<T> apply, FindFunction<T> function) {
        this(cls, apply, function, true);
    }

    /**
     * Creates a new AutoQueryRealmObservableList of type T.
     *
     * @param apply Realm query.
     */
    public RealmObservableList(RealmConfiguration configuration, Class<T> cls, OnApplyQuery<T> apply, FindFunction<T> function) {
        this(configuration, cls, apply, function, true);
    }

    public RealmObservableList(Class<T> cls, OnApplyQuery<T> apply, FindFunction<T> function, boolean updateOnModification) {
        this(Realm.getDefaultConfiguration(), cls, apply, function, updateOnModification);
    }

    /**
     * Creates a new AutoQueryRealmObservableList of type T.
     *
     * @param apply Realm query.
     */
    public RealmObservableList(RealmConfiguration configuration, Class<T> cls, OnApplyQuery<T> apply, FindFunction<T> function, boolean updateOnModification) {
        this.realmConfig = configuration;
        this.cls = cls;
        this.applyQuery = apply;
        this.findFunction = function;
        this.mirror = Collections.emptyList();
        this.listener = createListener();
        this.updateOnModification = updateOnModification;
    }

    @Deprecated
    @Override
    public T set(int index, T element) {
        return super.set(index, element);
    }

    @Deprecated
    @Override
    public void clear() {
        super.clear();
    }

    @Deprecated
    @Override
    public boolean add(T t) {
        return super.add(t);
    }

    @Deprecated
    @Override
    public void add(int index, T element) {
        super.add(index, element);
    }

    @Deprecated
    @Override
    public boolean addAll(Collection<? extends T> c) {
        return super.addAll(c);
    }

    @Deprecated
    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        return super.addAll(index, c);
    }

    @Deprecated
    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        super.removeRange(fromIndex, toIndex);
    }

    @Deprecated
    @Override
    public boolean remove(Object o) {
        return super.remove(o);
    }

    @Deprecated
    @Override
    public boolean removeAll(Collection<?> c) {
        return super.removeAll(c);
    }

    @Deprecated
    @Override
    public T remove(int index) {
        return super.remove(index);
    }

    @Deprecated
    @Override
    public boolean retainAll(Collection<?> c) {
        return super.retainAll(c);
    }

    private OrderedRealmCollectionChangeListener createListener() {
        return new OrderedRealmCollectionChangeListener() {

            // mirror list state and apply new only after onChange called
            @Override
            public void onChange(Object collection, OrderedCollectionChangeSet changeSet) {
                mirror = new ArrayList<>(items);
                // null Changes means the async query returns the first time.
                if (changeSet == null) {
                    listeners.notifyChanged(RealmObservableList.this);
                    return;
                }
                // For deletions, the adapter has to be notified in reverse order.
                OrderedCollectionChangeSet.Range[] deletions = changeSet.getDeletionRanges();
                for (int i = deletions.length - 1; i >= 0; i--) {

                    OrderedCollectionChangeSet.Range range = deletions[i];
                    modCount += 1;
                    listeners.notifyRemoved(RealmObservableList.this, range.startIndex, range.length);
                }

                OrderedCollectionChangeSet.Range[] insertions = changeSet.getInsertionRanges();
                for (OrderedCollectionChangeSet.Range range : insertions) {
                    modCount += 1;
                    listeners.notifyInserted(RealmObservableList.this, range.startIndex, range.length);
                }

                if (!updateOnModification) {
                    return;
                }

                OrderedCollectionChangeSet.Range[] modifications = changeSet.getChangeRanges();
                for (OrderedCollectionChangeSet.Range range : modifications) {
                    listeners.notifyChanged(RealmObservableList.this, range.startIndex, range.length);
                }
            }
        };
    }

    @Override
    public void addOnListChangedCallback(OnListChangedCallback<? extends ObservableList<T>> listener) {
        boolean first = listeners.isEmpty();
        listeners.add(listener);
        if (first) {
            realm = Realm.getInstance(realmConfig);
            RealmQuery<T> realmQuery = applyQuery.onApply(realm.where(cls));
            if (findFunction == null) {
                items = realmQuery.findAllAsync();
            } else {
                items = findFunction.find(realmQuery);
            }
            mirror = new ArrayList<>(items);
            addRealmChangeListener();
            listeners.notifyChanged(RealmObservableList.this);
        }
    }

    @Override
    public void removeOnListChangedCallback(OnListChangedCallback<? extends ObservableList<T>> listener) {
        listeners.remove(listener);
        if (listeners.isEmpty()) {
            removeRealmChangeListener();
            items = null;
            mirror = Collections.emptyList();
            realm.close();
        }
    }

    private void addRealmChangeListener() {
        if (items instanceof RealmResults) {
            RealmResults<T> results = (RealmResults<T>) items;
            //noinspection unchecked
            results.addChangeListener(listener);
        } else if (items instanceof RealmList) {
            RealmList<T> list = (RealmList<T>) items;
            //noinspection unchecked
            list.addChangeListener(listener);
        } else {
            throw new IllegalArgumentException("RealmCollection not supported: " + items.getClass());
        }
    }

    private void removeRealmChangeListener() {
        if (items instanceof RealmResults) {
            RealmResults<T> results = (RealmResults<T>) items;
            //noinspection unchecked
            results.removeChangeListener(listener);
        } else if (items instanceof RealmList) {
            RealmList<T> list = (RealmList<T>) items;
            //noinspection unchecked
            list.removeChangeListener(listener);
        } else {
            throw new IllegalArgumentException("RealmCollection not supported: " + items.getClass());
        }
    }

    @Override
    public T get(int i) {
        return mirror.get(i);
    }

    @Override
    public int size() {
        return mirror.size();
    }

}