package xyz.truenight.databinding.lifecycle;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by true
 * date: 21/01/2018
 * time: 15:04
 * <p>
 * Copyright © Mikhail Frolov
 */

public class BindingList extends ArrayList<ViewModelBinding> implements BindingProvider {

    public BindingList() {
    }

    public BindingList(ViewModelBinding... bindings) {
        Collections.addAll(this, bindings);
    }

    @Override
    public BindingList getItems() {
        return this;
    }
}
