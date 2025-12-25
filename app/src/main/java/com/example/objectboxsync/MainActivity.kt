package com.example.objectboxsync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.objectboxsync.app.MyApp
import com.example.objectboxsync.ui.CustomerScreen
import com.example.objectboxsync.ui.CustomerViewModel


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val app = (LocalContext.current.applicationContext as MyApp)

            val vm = androidx.lifecycle.viewmodel.compose.viewModel<CustomerViewModel>(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return CustomerViewModel(app.repo) as T
                    }
                }
            )

            CustomerScreen(vm)
        }
    }
}
