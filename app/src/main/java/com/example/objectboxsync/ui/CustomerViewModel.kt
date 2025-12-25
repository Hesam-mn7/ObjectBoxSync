package com.example.objectboxsync.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.objectboxsync.data.entity.Customer
import com.example.objectboxsync.data.repo.CustomerRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Created by H.Mousavioun on 12/24/2025
 */
class CustomerViewModel(
    private val repo: CustomerRepository
) : ViewModel() {

    val customers =
        repo.observeCustomers()
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun saveOrUpdate(selectedId: Long?, name: String, description: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repo.upsert(selectedId, name, description)
        }
    }

    fun delete(customer: Customer) {
        viewModelScope.launch {
            repo.delete(customer)
        }
    }
}