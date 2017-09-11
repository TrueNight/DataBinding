package xyz.truenight.databinding.realm;

import io.realm.RealmModel;
import io.realm.RealmQuery;

public interface OnApplyQuery<E extends RealmModel> {
    RealmQuery<E> onApply(RealmQuery<E> query);
}