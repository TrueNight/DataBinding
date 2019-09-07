package xyz.truenight.databinding.lifecycle

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity

/**
 * Created by true
 * date: 14/05/2017
 * time: 16:42
 *
 *
 * Copyright © Mikhail Frolov
 */

object BindingUtil {
    /**
     * Helper to throw an exception when [androidx.databinding.ViewDataBinding.setVariable] returns false.
     */
    fun throwMissingVariable(binding: ViewDataBinding, bindingVariable: Int, @LayoutRes layoutRes: Int) {
        val context = binding.root.context
        val resources = context.resources
        val layoutName = resources.getResourceName(layoutRes)
        // Yeah reflection is slow, but this only happens when there is a programmer error.
        var bindingVariableName: String
        try {
            bindingVariableName = getBindingVariableName(context, bindingVariable)
        } catch (e: Resources.NotFoundException) {
            // Fall back to int
            bindingVariableName = "" + bindingVariable
        }

        throw IllegalStateException("Could not bind variable '$bindingVariableName' in layout '$layoutName'")
    }

    /**
     * Returns the name for the given binding variable int. Warning! This uses reflection so it
     * should *only* be used for debugging.
     *
     * @throws Resources.NotFoundException if the name cannot be found.
     */
    @Throws(Resources.NotFoundException::class)
    internal fun getBindingVariableName(context: Context, bindingVariable: Int): String {
        try {
            return getBindingVariableByDataBinderMapper(bindingVariable)
        } catch (e1: Exception) {
            try {
                return getBindingVariableByBR(context, bindingVariable)
            } catch (e2: Exception) {
                throw Resources.NotFoundException("" + bindingVariable)
            }

        }

    }

    /**
     * Attempt to getInternal the name from a non-public method on the generated DataBinderMapper class.
     * This method does exactly what we want, but who knows if it will be there in future versions.
     */
    @Throws(Exception::class)
    private fun getBindingVariableByDataBinderMapper(bindingVariable: Int): String {
        val dataBinderMapper = Class.forName("android.databinding.DataBinderMapper")
        val convertIdMethod = dataBinderMapper.getDeclaredMethod("convertBrIdToString", Int::class.javaPrimitiveType!!)
        convertIdMethod.isAccessible = true
        val constructor = dataBinderMapper.getDeclaredConstructor()
        constructor.isAccessible = true
        val instance = constructor.newInstance()
        val result = convertIdMethod.invoke(instance, bindingVariable)
        return result as String
    }

    /**
     * Attempt to getInternal the name by using reflection on the generated BR class. Unfortunately, we
     * don't know BR's package name so this may fail if it's not the same as the apps package name.
     */
    @Throws(Exception::class)
    private fun getBindingVariableByBR(context: Context, bindingVariable: Int): String {
        val packageName = context.packageName
        val BRClass = Class.forName("$packageName.BR")
        val fields = BRClass.fields
        for (field in fields) {
            val value = field.getInt(null)
            if (value == bindingVariable) {
                return field.name
            }
        }
        throw Exception("not found")
    }
}

fun Context.asFragmentActivitySafe(): FragmentActivity? {
    return when (this) {
        is FragmentActivity -> this
        is Activity -> null
        is ContextWrapper -> baseContext.asFragmentActivitySafe()
        else -> null
    }
}

fun Context.asFragmentActivity(): FragmentActivity {
    return when (this) {
        is FragmentActivity -> this
        is Activity -> throw IllegalStateException("Context $this NOT support-v4 Activity")
        is ContextWrapper -> baseContext.asFragmentActivity()
        else -> throw IllegalStateException("Context $this NOT contains activity!")
    }
}