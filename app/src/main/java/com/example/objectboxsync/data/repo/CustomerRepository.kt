package com.example.objectboxsync.data.repo

import com.example.objectboxsync.data.entity.Customer
import kotlinx.coroutines.flow.Flow

/**
 * Created by H.Mousavioun on 12/24/2025
 */
interface CustomerRepository {
    fun observeCustomers(): Flow<List<Customer>>
    suspend fun upsert(id: Long?, name: String, description: String)
    suspend fun delete(customer: Customer)
}