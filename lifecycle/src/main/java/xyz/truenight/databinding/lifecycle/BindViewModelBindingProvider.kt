package xyz.truenight.databinding.lifecycle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import java.util.*

/**
 * Created by true
 * date: 06/09/2017
 * time: 19:36
 *
 *
 * Copyright © Mikhail Frolov
 */

class BindViewModelBindingProvider<T : ViewModel> : ViewModelBindingProvider<T> {

    private val owner: ViewModelStoreOwner
    private val vmClass: Class<T>
    private val factory: (() -> ViewModel)?

    override val variableId: Int

    constructor(owner: ViewModelStoreOwner, variableId: Int, vmClass: Class<T>) {
        this.owner = owner
        this.vmClass = vmClass
        this.variableId = variableId
        factory = null
    }

    constructor(owner: ViewModelStoreOwner, variableId: Int, vmClass: Class<T>, factory: () -> T) {
        this.owner = owner
        this.vmClass = vmClass
        this.variableId = variableId
        this.factory = factory
    }

    override fun get(): T {
        val provider =
                factory?.let { ViewModelProvider(owner, SingleProviderFactory(it)) }
                        ?: ViewModelProvider(owner)
        return provider.get(vmClass)
    }

    override val items: List<BindViewModelBindingProvider<*>>
        get() = Collections.singletonList(this)
}
