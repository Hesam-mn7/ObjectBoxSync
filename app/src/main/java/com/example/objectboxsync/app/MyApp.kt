package com.example.objectboxsync.app

import android.app.Application
import com.example.objectboxsync.data.entity.MyObjectBox
import com.example.objectboxsync.data.repo.ObjectBoxCustomerRepository
import com.example.objectboxsync.sync.ObjectBoxSyncManager
import io.objectbox.BoxStore

class MyApp : Application() {

    lateinit var boxStore: BoxStore
        private set

    lateinit var repo: ObjectBoxCustomerRepository
        private set

    private var syncManager: ObjectBoxSyncManager? = null

    override fun onCreate() {
        super.onCreate()

        boxStore = MyObjectBox.builder().androidContext(this).build()
        repo = ObjectBoxCustomerRepository(boxStore)

        syncManager = ObjectBoxSyncManager(
            boxStore = boxStore,
            syncUrl = "ws://192.168.1.35:9999"
        ).also { it.start() }
    }

    override fun onTerminate() {
        syncManager?.close()
        boxStore.close()
        super.onTerminate()
    }
}
