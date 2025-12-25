package com.example.objectboxsync.data.repo

import com.example.objectboxsync.data.entity.Customer
import com.example.objectboxsync.data.entity.Customer_
import com.example.objectboxsync.sync.SyncEvents
import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor
import io.objectbox.query.QueryBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by H.Mousavioun on 12/24/2025
 */
class ObjectBoxCustomerRepository(
    boxStore: BoxStore
) : CustomerRepository {

    private val box = boxStore.boxFor<Customer>()

    private fun load(): List<Customer> =
        box.query().order(Customer_.id, QueryBuilder.DESCENDING).build().use { it.find() }

    override fun observeCustomers(): Flow<List<Customer>> = channelFlow {

        send(load())

        val job = launch(Dispatchers.IO) {
            SyncEvents.refresh
                .onStart { emit(Unit) }
                .collectLatest {
                    send(load())
                }
        }

        awaitClose { job.cancel() }
    }

    override suspend fun upsert(id: Long?, name: String, description: String) {
        withContext(Dispatchers.IO) {
            val entity = if (id == null || id == 0L) {
                Customer(customerName = name, description = description)
            } else {
                Customer(id = id, customerName = name, description = description)
            }
            box.put(entity)
        }

        SyncEvents.notifyChanged()
    }

    override suspend fun delete(customer: Customer) {
        withContext(Dispatchers.IO) {
            box.remove(customer)
        }
        SyncEvents.notifyChanged()
    }
}
