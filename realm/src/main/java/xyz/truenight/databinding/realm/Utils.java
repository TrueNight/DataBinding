package xyz.truenight.databinding.realm;

import io.realm.Realm;
import io.realm.RealmCollection;
import io.realm.RealmConfiguration;
import io.realm.RealmModel;
import io.realm.internal.RealmObjectProxy;

/**
 * Copyright (C) 2017 Mikhail Frolov
 */

class Utils {
    private Utils() {
    }


    @SuppressWarnings("unchecked")
    public static <E> E unmanage(RealmConfiguration config, E object) {
        if (object == null) {
            return null;
        }
        if (object instanceof RealmObjectProxy) {
            Realm instance = Realm.getInstance(config);
            E e = (E) instance.copyFromRealm((RealmModel) object);
            instance.close();
            return e;
        } else if (object instanceof RealmCollection || (object instanceof Iterable && first((Iterable) object) instanceof RealmModel)) {
            Realm instance = Realm.getInstance(config);
            E e = (E) instance.copyFromRealm((Iterable<? extends RealmModel>) object);
            instance.close();
            return e;
        } else {
            return object;
        }
    }

    /**
     * NULL safe isEmpty()
     */
    public static boolean isEmpty(Iterable iterable) {
        return iterable == null || !iterable.iterator().hasNext();
    }


    /**
     * @return FIRST element of list or NULL if list is empty
     */
    public static <T> T first(Iterable<T> list) {
        return isEmpty(list) ? null : list.iterator().next();
    }
}
