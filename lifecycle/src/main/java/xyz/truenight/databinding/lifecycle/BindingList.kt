package xyz.truenight.databinding.lifecycle

import java.util.*

/**
 * Created by true
 * date: 21/01/2018
 * time: 15:04
 *
 *
 * Copyright © Mikhail Frolov
 */

class BindingList : ArrayList<ViewModelBindingProvider<*>>, BindingProvider {

    override val items: List<ViewModelBindingProvider<*>>
        get() = this

    constructor() {}

    constructor(vararg bindings: ViewModelBindingProvider<*>) {
        Collections.addAll(this, *bindings)
    }
}
