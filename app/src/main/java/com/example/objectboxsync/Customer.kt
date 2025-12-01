package com.example.objectboxsync

/**
 * Created by H.Mousavioun on 11/29/2025
 */
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Sync

@Sync
@Entity
data class Customer(
    @Id var id: Long = 0,
    var customerName: String = "",
    var description: String = ""
)
