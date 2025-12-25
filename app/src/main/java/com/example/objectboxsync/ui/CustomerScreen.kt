package com.example.objectboxsync.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.objectboxsync.data.entity.Customer

/**
 * Created by H.Mousavioun on 12/24/2025
 */

@Composable
fun CustomerScreen(vm: CustomerViewModel) {
    val customers by vm.customers.collectAsState()

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf<Customer?>(null) }

    fun resetForm() {
        name = ""
        description = ""
        selected = null
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Customer Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                vm.saveOrUpdate(selected?.id, name, description)
                resetForm()
            }) {
                Text(if (selected == null) "Save" else "Update")
            }

            if (selected != null) {
                OutlinedButton(onClick = { resetForm() }) { Text("Cancel") }
            }
        }

        Spacer(Modifier.height(24.dp))

        LazyColumn {
            items(customers, key = { it.id }) { customer ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selected = customer
                            name = customer.customerName
                            description = customer.description
                        }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(customer.customerName)
                        Text(customer.description)
                    }
                    Button(onClick = { vm.delete(customer) }) { Text("Delete") }
                }
            }
        }
    }
}
