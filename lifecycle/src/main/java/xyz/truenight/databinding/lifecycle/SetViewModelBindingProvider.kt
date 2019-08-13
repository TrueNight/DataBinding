package xyz.truenight.databinding.lifecycle

import androidx.lifecycle.ViewModel
import java.util.*

/**
 * Created by true
 * date: 06/09/2017
 * time: 19:36
 *
 *
 * Copyright © Mikhail Frolov
 */

class SetViewModelBindingProvider<T : ViewModel> : ViewModelBindingProvider<T> {

    override val variableId: Int

    private var factory: () -> T

    constructor(variableId: Int, factory: () -> T) {
        this.variableId = variableId
        this.factory = factory
    }

    override fun get(): T = factory()

    override val items: List<SetViewModelBindingProvider<*>>
        get() = Collections.singletonList(this)
}
