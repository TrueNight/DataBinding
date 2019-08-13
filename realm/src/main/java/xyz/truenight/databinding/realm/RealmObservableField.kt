package xyz.truenight.databinding.realm

import androidx.databinding.Observable
import androidx.databinding.ObservableField
import io.realm.*
import java.util.*
import java.util.concurrent.CopyOnWriteArraySet

/**
 * An [ObservableField] that uses [RealmChangeListener] to calculate and dispatch it's change
 * updates.
 */
class RealmObservableField<T : RealmModel> private constructor(private val realmConfig: RealmConfiguration, private val cls: Class<T>, private val applyQuery: (RealmQuery<T>) -> RealmQuery<T> = { it }, private val async: Boolean = false) : ObservableField<T>() {

    private val count = CopyOnWriteArraySet<Observable.OnPropertyChangedCallback>()
    private var closable: Realm? = null
    private var all: RealmResults<T>? = null
    private val listener: RealmChangeListener<RealmResults<T>> = createListener()

    private fun createListener(): RealmChangeListener<RealmResults<T>> {
        return RealmChangeListener { t ->
            if (t.isValid) {
                super@RealmObservableField.set(t.firstOrNull())
            }
        }
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        super.addOnPropertyChangedCallback(callback)
        if (!EXCLUDE.contains(callback.javaClass.name)) {
            if (count.isEmpty()) {
                closable = Realm.getInstance(realmConfig)
                val realmQuery = applyQuery(closable!!.where(cls))
                // workaround for NULL
                val items = if (async) {
                    realmQuery.findAllAsync()
                } else {
                    realmQuery.findAll()
                }
                all = items
                if (!async) {
                    super.set(items.firstOrNull())
                }
                items.addChangeListener(listener)
            }
            count.add(callback)
        }
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        super.removeOnPropertyChangedCallback(callback)
        count.remove(callback)
        if (count.isEmpty()) {
            all?.removeChangeListener(listener)
            all = null
            closable?.close()
            closable = null
        }
    }

    companion object {

        private val EXCLUDE = HashSet<String>()

        fun excludeCallback(cls: Class<out Observable.OnPropertyChangedCallback>) {
            EXCLUDE.add(cls.name)
        }

        fun removeExclusion(cls: Class<out Observable.OnPropertyChangedCallback>) {
            EXCLUDE.remove(cls.name)
        }

        fun excludeCallback(className: String) {
            EXCLUDE.add(className)
        }

        fun removeExclusion(className: String) {
            EXCLUDE.remove(className)
        }

        inline fun <reified T : RealmModel> create(
                realmConfig: RealmConfiguration = Realm.getDefaultConfiguration()!!,
                noinline query: (RealmQuery<T>) -> RealmQuery<T>,
                async: Boolean = true
        ) = create(realmConfig, T::class.java, query, async)

        @JvmStatic
        @JvmOverloads
        fun <T : RealmModel> create(
                realmConfig: RealmConfiguration = Realm.getDefaultConfiguration()!!,
                cls: Class<T>, query: (RealmQuery<T>) -> RealmQuery<T>,
                async: Boolean = true
        ) = RealmObservableField(realmConfig, cls, query, async)
    }
}