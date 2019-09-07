package xyz.truenight.databinding.lifecycle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel

/**
 * Created by true
 * date: 02/09/2017
 * time: 16:51
 *
 *
 * Copyright © Mikhail Frolov
 */

abstract class BindingLifecycleFragment<B : ViewDataBinding> : Fragment() {

    private var mBinding: B? = null
    private var mBinded: Boolean = false

    @get:LayoutRes
    abstract val bindingLayoutRes: Int

    abstract val viewModelBindings: BindingProvider?

    @CallSuper
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (mBinding == null) {
            mBinding = DataBindingUtil.inflate(inflater, bindingLayoutRes, container, false)
        }

        if (!mBinded) {

            viewModelBindings?.items?.forEach { holder ->
                holder.get().also {
                    onPrepareViewModel(it)
                    (it as? LifecycleTrackingViewModel)?.registerLifecycle(this)

                    if (!mBinding!!.setVariable(holder.variableId, it)) {
                        BindingUtil.throwMissingVariable(mBinding!!, holder.variableId, bindingLayoutRes)
                    }
                }
            }
            mBinded = true
        }

        return mBinding?.root
    }

    protected open fun onPrepareViewModel(viewModel: ViewModel) {

    }

    override fun onDestroy() {
        super.onDestroy()
        if (mBinding != null) {
            mBinding!!.unbind()
            mBinded = false
        }
    }

    fun binding(): B? {
        return mBinding
    }

    protected fun listOf(vararg binding: ViewModelBindingProvider<*>): BindingList {
        return BindingList(*binding)
    }
}
