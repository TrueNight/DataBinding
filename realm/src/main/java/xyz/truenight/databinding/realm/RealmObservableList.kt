package xyz.truenight.databinding.realm

import androidx.databinding.ListChangeRegistry
import androidx.databinding.ObservableList
import io.realm.*
import java.util.*
import java.util.concurrent.CopyOnWriteArraySet

/**
 * An [ObservableList] that uses [OrderedRealmCollectionChangeListener] to calculate and dispatch it's change
 * updates.
 */
class RealmObservableList<T : RealmModel> private constructor(private val realmConfig: RealmConfiguration, private val cls: Class<T>, private val query: (RealmQuery<T>) -> RealmResults<T>, private val updateOnModification: Boolean) : AbstractList<T>(), ObservableList<T> {

    private val count = CopyOnWriteArraySet<ObservableList.OnListChangedCallback<*>>()
    private var realm: Realm? = null
    private var items: RealmResults<T>? = null
    private val listener: OrderedRealmCollectionChangeListener<RealmResults<T>> = createListener()
    private val listeners = ListChangeRegistry()
    private var mirror: List<T> = emptyList()

    @Deprecated("Method not supported")
    override fun set(index: Int, element: T): T {
        return super.set(index, element)
    }

    @Deprecated("Method not supported")
    override fun clear() {
        super.clear()
    }

    @Deprecated("Method not supported")
    override fun add(t: T): Boolean {
        return super.add(t)
    }

    @Deprecated("Method not supported")
    override fun add(index: Int, element: T) {
        super.add(index, element)
    }

    @Deprecated("Method not supported")
    override fun addAll(c: Collection<T>): Boolean {
        return super.addAll(c)
    }

    @Deprecated("Method not supported")
    override fun addAll(index: Int, c: Collection<T>): Boolean {
        return super.addAll(index, c)
    }

    @Deprecated("Method not supported")
    override fun removeRange(fromIndex: Int, toIndex: Int) {
        super.removeRange(fromIndex, toIndex)
    }

    @Deprecated("Method not supported")
    override fun remove(element: T?): Boolean {
        return super.remove(element)
    }

    @Deprecated("Method not supported")
    override fun removeAll(c: Collection<T>): Boolean {
        return super.removeAll(c)
    }

    @Deprecated("Method not supported")
    override fun removeAt(index: Int): T {
        return super.removeAt(index)
    }

    @Deprecated("Method not supported")
    override fun retainAll(elements: Collection<T>): Boolean {
        return super.retainAll(elements)
    }

    private fun createListener(): OrderedRealmCollectionChangeListener<RealmResults<T>> {
        return object : OrderedRealmCollectionChangeListener<RealmResults<T>> {

            // mirror list state and apply new only after onChange called
            override fun onChange(collection: RealmResults<T>, changeSet: OrderedCollectionChangeSet) {
                mirror = ArrayList(items!!)
                // null Changes means the async query returns the first time.
                if (changeSet == null) {
                    listeners.notifyChanged(this@RealmObservableList)
                    return
                }
                // For deletions, the adapter has to be notified in reverse order.
                val deletions = changeSet.deletionRanges
                for (i in deletions.indices.reversed()) {

                    val range = deletions[i]
                    modCount += 1
                    listeners.notifyRemoved(this@RealmObservableList, range.startIndex, range.length)
                }

                val insertions = changeSet.insertionRanges
                for (range in insertions) {
                    modCount += 1
                    listeners.notifyInserted(this@RealmObservableList, range.startIndex, range.length)
                }

                if (!updateOnModification) {
                    return
                }

                val modifications = changeSet.changeRanges
                for (range in modifications) {
                    listeners.notifyChanged(this@RealmObservableList, range.startIndex, range.length)
                }
            }
        }
    }

    override fun addOnListChangedCallback(listener: ObservableList.OnListChangedCallback<out ObservableList<T>>) {
        listeners.add(listener)
        if (!EXCLUDE.contains(listener.javaClass.name)) {
            if (count.isEmpty()) {
                val realm = Realm.getInstance(realmConfig)
                this.realm = realm
                val results = query(realm.where(cls))

                mirror = ArrayList(items)
                results.addChangeListener(this.listener)
                items = results
                listeners.notifyChanged(this@RealmObservableList)
            }
            count.add(listener)
        }
    }

    override fun removeOnListChangedCallback(listener: ObservableList.OnListChangedCallback<out ObservableList<T>>) {
        listeners.remove(listener)
        count.remove(listener)
        if (count.isEmpty()) {
            items?.removeChangeListener(this.listener)
            items = null
            mirror = emptyList()
            realm?.close()
        }
    }

    override fun get(index: Int): T {
        return mirror[index]
    }

    override val size: Int
        get() = mirror.size

    companion object {

        private val EXCLUDE = HashSet<String>()

        fun excludeCallback(cls: Class<out ObservableList.OnListChangedCallback<*>>) {
            EXCLUDE.add(cls.name)
        }

        fun removeExclusion(cls: Class<out ObservableList.OnListChangedCallback<*>>) {
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
                noinline query: (RealmQuery<T>) -> RealmResults<T>,
                updateOnModification: Boolean = true
        ) = create(realmConfig, T::class.java, query, updateOnModification)

        @JvmStatic
        @JvmOverloads
        fun <T : RealmModel> create(
                realmConfig: RealmConfiguration = Realm.getDefaultConfiguration()!!,
                cls: Class<T>, query: (RealmQuery<T>) -> RealmResults<T>,
                updateOnModification: Boolean = true
        ) = RealmObservableList(realmConfig, cls, query, updateOnModification)
    }

}