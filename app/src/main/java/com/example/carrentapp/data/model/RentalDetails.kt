package com.example.carrentapp.data.model

data class RentalDetails(
    val pickupLocation: String,   // e.g., "Los Angeles, CA"
    val dropOffLocation: String?, // Optional, can be null
    val pickupDate: String,       // e.g., "2025-03-01"
    val dropOffDate: String       // e.g., "2025-03-05"
)
