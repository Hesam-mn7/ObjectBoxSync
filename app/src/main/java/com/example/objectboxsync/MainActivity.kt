package com.example.objectboxsync

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.objectbox.Box

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            val app = context.applicationContext as MyApp
            val customerBox = remember { app.boxStore.boxFor(Customer::class.java) }

            CustomerScreen(customerBox = customerBox, customersState = app.customersState)
        }
    }
}

@Composable
fun CustomerScreen(customerBox: Box<Customer>, customersState: MutableState<List<Customer>>) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCustomer by remember { mutableStateOf<Customer?>(null) }

    val customers = customersState.value

    // عملکرد ذخیره مشتری
    fun saveCustomer() {
        if (name.isBlank()) {
            Log.d("CustomerUI", "saveCustomer: name is blank, ignore")
            return
        }

        if (selectedCustomer == null) {
            val newCustomer = Customer(customerName = name, description = description)
            customerBox.put(newCustomer)
            Log.d("CustomerUI", "saveCustomer: INSERT new customer ${newCustomer.customerName}")
        } else {
            val updatedCustomer = selectedCustomer!!.copy(customerName = name, description = description)
            customerBox.put(updatedCustomer)
            Log.d("CustomerUI", "saveCustomer: UPDATE customer ${updatedCustomer.customerName}")
        }

        name = ""
        description = ""
        selectedCustomer = null
    }

    // عملکرد حذف مشتری
    fun deleteCustomer(customer: Customer) {
        customerBox.remove(customer)
        Log.d("CustomerUI", "deleteCustomer: Deleted customer ${customer.customerName}")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

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

        Row {
            Button(onClick = { saveCustomer() }) {
                Text(if (selectedCustomer == null) "Save" else "Update")
            }
        }

        Spacer(Modifier.height(24.dp))

        // نمایش لیست مشتری‌ها
        LazyColumn {
            items(customers) { customer ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedCustomer = customer
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

                    Button(onClick = { deleteCustomer(customer) }) {
                        Text("Delete")
                    }
                }
            }
        }
    }
}
