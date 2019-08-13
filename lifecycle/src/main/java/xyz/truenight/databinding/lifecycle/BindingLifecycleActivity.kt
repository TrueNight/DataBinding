package xyz.truenight.databinding.lifecycle

import android.os.Bundle

import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel

/**
 * Created by true
 * date: 02/09/2017
 * time: 15:46
 *
 *
 * Copyright © Mikhail Frolov
 */

abstract class BindingLifecycleActivity<B : ViewDataBinding> : AppCompatActivity() {

    private var mBinding: B? = null

    @get:LayoutRes
    abstract val bindingLayoutRes: Int

    abstract val viewModelBindings: BindingProvider?

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (mBinding == null) {
            mBinding = DataBindingUtil.setContentView(this, bindingLayoutRes)
        }

        viewModelBindings?.items?.forEach { holder ->
            holder.get().also {
                onPrepareViewModel(it)
                (it as? LifecycleTrackingViewModel)?.registerLifecycle(this)

                if (!mBinding!!.setVariable(holder.variableId, it)) {
                    BindingUtil.throwMissingVariable(mBinding!!, holder.variableId, bindingLayoutRes)
                }
            }
        }
    }

    protected open fun onPrepareViewModel(viewModel: ViewModel) {

    }

    override fun onDestroy() {
        super.onDestroy()
        if (mBinding != null) {
            mBinding!!.unbind()
        }
    }

    fun binding(): B? {
        return mBinding
    }

    protected fun listOf(vararg binding: ViewModelBindingProvider<*>): BindingList {
        return BindingList(*binding)
    }

}
