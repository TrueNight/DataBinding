package xyz.truenight.databinding.realm;

import io.realm.RealmModel;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public interface FindFunction<T extends RealmModel> {
    RealmResults<T> find(RealmQuery<T> query);
}