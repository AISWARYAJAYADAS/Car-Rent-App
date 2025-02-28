package com.example.carrentapp.data.repository

import com.example.carrentapp.data.model.RentalDetails
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

/** Repository for generating Kayak car rental deep links. */
class CarRentalRepository @Inject constructor() {

    companion object {
        private const val KAYAK_DOMAIN = "www.kayak.com"
        private const val AFFILIATE_ID = "awesomecars"
    }

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    /**
     * Generates a Kayak deep link URL based on rental details.
     * @param details The rental details including locations and dates.
     * @return A fully constructed Kayak URL.
     * @throws IllegalArgumentException if inputs are invalid.
     */
    fun createKayakLink(details: RentalDetails): String {
        if (details.pickupLocation.isBlank()) {
            throw IllegalArgumentException("Pickup location is required.")
        }

        val pickupDate = dateFormat.parse(details.pickupDate)
            ?: throw IllegalArgumentException("Invalid pickup date format (use YYYY-MM-DD).")
        val dropOffDate = dateFormat.parse(details.dropOffDate)
            ?: throw IllegalArgumentException("Invalid drop-off date format (use YYYY-MM-DD).")

        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        if (pickupDate.before(today)) {
            throw IllegalArgumentException("Pickup date cannot be in the past.")
        }
        if (dropOffDate.before(pickupDate)) {
            throw IllegalArgumentException("Drop-off date must be after pickup date.")
        }

        val dropOff = details.dropOffLocation?.ifBlank { details.pickupLocation } ?: details.pickupLocation
        val path = "/cars/${details.pickupLocation}/$dropOff/${details.pickupDate}/${details.dropOffDate}"
        return "https://$KAYAK_DOMAIN/in?a=$AFFILIATE_ID&url=$path"
    }
}