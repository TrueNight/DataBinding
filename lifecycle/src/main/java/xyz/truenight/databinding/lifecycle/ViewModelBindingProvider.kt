package xyz.truenight.databinding.lifecycle

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
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

interface ViewModelBindingProvider<T : ViewModel> : BindingProvider {

    fun get(): T

    val variableId: Int

    override val items: List<ViewModelBindingProvider<*>>
        get() = Collections.singletonList(this)
}


inline fun <reified T : ViewModel> ViewModelStoreOwner.bindVm(variableId: Int) =
        BindViewModelBindingProvider(this, variableId, T::class.java)

inline fun <reified T : ViewModel> ViewModelStoreOwner.bindVm(
        variableId: Int,
        noinline factory: () -> T
) = BindViewModelBindingProvider(this, variableId, T::class.java, factory)

inline fun <reified T : ViewModel> Fragment.bindVmActivity(variableId: Int) =
        activity!!.bindVm<T>(variableId)

inline fun <reified T : ViewModel> Fragment.bindVmActivity(
        variableId: Int,
        noinline factory: () -> T
) = activity!!.bindVm(variableId, factory)

inline fun <reified T : ViewModel> Fragment.bindVmParent(variableId: Int) =
        parentFragment!!.bindVm<T>(variableId)

inline fun <reified T : ViewModel> Fragment.bindVmParent(
        variableId: Int,
        noinline factory: () -> T
) = parentFragment!!.bindVm(variableId, factory)

fun <T : ViewModel> ViewModelStoreOwner.setVm(
        variableId: Int,
        factory: () -> T
) = SetViewModelBindingProvider(variableId, factory)