package com.example.carrentapp.presentation.state

import android.content.Intent

sealed class CarRentalState {
    data class Inputs(
        val pickupLocation: String = "",
        val dropOffLocation: String? = "", // Nullable, matches UI and ViewModel
        val pickupDate: String = "",
        val dropOffDate: String = ""
    ) : CarRentalState() {
        fun isValid(): Boolean = pickupLocation.isNotBlank() && pickupDate.isNotBlank() && dropOffDate.isNotBlank()
    }

    data class Error(val message: String) : CarRentalState()

    data class Navigate(val intent: Intent) : CarRentalState()
}