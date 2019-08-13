package xyz.truenight.databinding.lifecycle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SingleProviderFactory<E : ViewModel>(private val factory: () -> E) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val create = factory()
        try {
            return modelClass.cast(create)
        } catch (e: ClassCastException) {
            throw IllegalArgumentException("Model class NOT supported", e)
        }

    }
}