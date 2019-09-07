package xyz.truenight.databinding.lifecycle

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel

/**
 * Created by true
 * date: 02/09/2017
 * time: 16:51
 *
 *
 * Copyright © Mikhail Frolov
 */

abstract class BindingLifecycleDialogFragment<B : ViewDataBinding> : DialogFragment() {

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

    private val fragmentTag get() = javaClass.name

    fun show(context: Context) {
        try {
            show(context.asFragmentActivity().supportFragmentManager, fragmentTag)
        } catch (th: Throwable) {
            Log.e(TAG, "", th)
        }
    }

    fun show(fragment: Fragment) {
        try {
            show(fragment.fragmentManager!!, fragmentTag)
        } catch (th: Throwable) {
            Log.e(TAG, "", th)
        }
    }

    fun showFromParent(fragment: Fragment) {
        try {
            show(fragment.childFragmentManager, fragmentTag)
        } catch (th: Throwable) {
            Log.e(TAG, "", th)
        }
    }

    fun show(manager: FragmentManager) {
        try {
            show(manager, fragmentTag)
        } catch (th: Throwable) {
            Log.e(TAG, "", th)
        }

    }

    protected fun listOf(vararg binding: ViewModelBindingProvider<*>): BindingList {
        return BindingList(*binding)
    }

    companion object {

        private val TAG = BindingLifecycleDialogFragment::class.java.simpleName
    }
}
