package com.example.objectboxsync.sync

import android.util.Log
import io.objectbox.BoxStore
import io.objectbox.sync.Sync
import io.objectbox.sync.SyncChange
import io.objectbox.sync.SyncClient
import io.objectbox.sync.SyncCredentials
import io.objectbox.sync.listener.SyncListener

/**
 * Created by H.Mousavioun on 12/24/2025
 */

class ObjectBoxSyncManager(
    private val boxStore: BoxStore,
    private val syncUrl: String,
    private val credentials: SyncCredentials = SyncCredentials.none()
) {

    companion object { private const val TAG = "ObjectBoxSync" }

    private var client: SyncClient? = null

    fun start() {
        if (!Sync.isAvailable()) {
            Log.e(TAG, "Sync NOT available")
            return
        }

        try {
            client = Sync.client(boxStore, syncUrl, credentials).build()
            client?.setSyncListener(object : SyncListener {

                override fun onLoggedIn() {
                    Log.d(TAG, "Logged In")

                }

                override fun onLoginFailed(syncLoginCode: Long){
                    Log.e(TAG, "Login failed: $syncLoginCode")
                }

                override fun onUpdatesCompleted() {
                    Log.d(TAG, "Updates completed")
                }

                override fun onDisconnected() {
                    Log.e(TAG, "Disconnected")
                }

                override fun onSyncChanges(syncChanges: Array<out SyncChange?>?) {
                    Log.d(TAG, "onSyncChanges size=${syncChanges?.size ?: 0}")
                    SyncEvents.notifyChanged()
                }

                override fun onServerTimeUpdate(serverTimeNanos: Long) {
                    Log.e(TAG, "onServerTimeUpdate")
                }
            })

            client?.start()
            Log.d(TAG, "Sync started")
        } catch (e: Exception) {
            Log.e(TAG, "Sync start failed", e)
        }
    }

    fun close() {
        try { client?.close() } catch (e: Exception) { Log.e(TAG, "Close failed", e) }
    }
}
