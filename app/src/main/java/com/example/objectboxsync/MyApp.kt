package com.example.objectboxsync

import android.app.Application
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import io.objectbox.BoxStore
import io.objectbox.sync.Sync
import io.objectbox.sync.SyncChange
import io.objectbox.sync.SyncClient
import io.objectbox.sync.SyncCredentials
import io.objectbox.sync.listener.SyncListener

class MyApp : Application() {

    companion object {
        private const val TAG = "MyAppSync"
    }

    lateinit var boxStore: BoxStore
        private set

    var syncClient: SyncClient? = null
        private set

    var customersState: MutableState<List<Customer>> = mutableStateOf(emptyList())

    override fun onCreate() {
        super.onCreate()

        Log.d(TAG, "Creating BoxStore")
        boxStore = MyObjectBox.builder()
            .androidContext(this)
            .build()
        Log.d(TAG, "BoxStore created OK")

        if (!Sync.isAvailable()) {
            Log.e(TAG, "Sync NOT available")
            return
        }

        val syncUrl = "ws://172.25.145.10:9999"
        val credentials = SyncCredentials.none()

        try {
            syncClient = Sync.client(boxStore, syncUrl, credentials)
                .build()

            Log.d(TAG, "SyncClient built OK")
        } catch (e: Exception) {
            Log.e(TAG, "SyncClient BUILD FAILED", e)
        }

        try {
            syncClient?.start()
            Log.d(TAG, "SyncClient.start() called")

            syncClient?.setSyncListener(object : SyncListener {
                override fun onLoggedIn() {
                    Log.d(TAG, "Sync: Logged In")
                }

                override fun onLoginFailed(syncLoginCode: Long) {
                    Log.e(TAG, "Sync: Login failed with code $syncLoginCode")
                }

                override fun onUpdatesCompleted() {
                    Log.d(TAG, "Sync: Updates completed")
                }

                override fun onSyncChanges(syncChanges: Array<out SyncChange?>?) {
                    Log.d(TAG, "onSyncChanges: sync changed ${syncChanges?.get(0)}")

                    val updatedCustomers = boxStore.boxFor(Customer::class.java).all //get customers from db

                    customersState.value = updatedCustomers  // Update UI with new data
                }

                override fun onDisconnected() {
                    Log.e(TAG, "Sync: Disconnected from server")
                }

                override fun onServerTimeUpdate(serverTimeNanos: Long) {
                    Log.d(TAG, "Sync: Server time updated: $serverTimeNanos")
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Sync START FAILED", e)
        }

        loadCustomers()
    }

    private fun loadCustomers() {
        val customers = boxStore.boxFor(Customer::class.java).all
        customersState.value = customers  // Update the state with initial customers
    }

    override fun onTerminate() {
        try {
            syncClient?.close()
        } catch (e: Exception) {
            Log.e(TAG, "Error closing SyncClient", e)
        }

        boxStore.close()
        super.onTerminate()
    }
}
