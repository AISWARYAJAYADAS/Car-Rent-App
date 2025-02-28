package com.example.carrentapp.presentation.viewmodel

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carrentapp.data.model.RentalDetails
import com.example.carrentapp.data.repository.CarRentalRepository
import com.example.carrentapp.presentation.state.CarRentalState
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CarRentalViewModel @Inject constructor(
    private val repository: CarRentalRepository,
    private val placesClient: PlacesClient
) : ViewModel() {

    private val _state = MutableStateFlow<CarRentalState>(CarRentalState.Inputs())
    val state: StateFlow<CarRentalState> = _state.asStateFlow()

    private val _pickupSuggestions = MutableStateFlow<List<String>>(emptyList())
    val pickupSuggestions: StateFlow<List<String>> = _pickupSuggestions.asStateFlow()

    private val _dropOffSuggestions = MutableStateFlow<List<String>>(emptyList())
    val dropOffSuggestions: StateFlow<List<String>> = _dropOffSuggestions.asStateFlow()

    private val token = AutocompleteSessionToken.newInstance()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    fun onPickupLocationChanged(location: String) {
        updateInputs { copy(pickupLocation = location.trim()) }
        fetchSuggestions(location, isPickup = true)
    }

    fun onDropOffLocationChanged(location: String) {
        updateInputs { copy(dropOffLocation = location.trim().ifBlank { null }) }
        fetchSuggestions(location, isPickup = false)
    }

    fun onPickupDateChanged(date: String) {
        updateInputs { copy(pickupDate = date.trim()) }
    }

    fun onDropOffDateChanged(date: String) {
        updateInputs { copy(dropOffDate = date.trim()) }
    }

    fun onSearchClicked() {
        viewModelScope.launch {
            val inputs = _state.value as? CarRentalState.Inputs ?: return@launch
            if (!inputs.isValid()) {
                _state.value = CarRentalState.Error("Pickup location and pickup date are required.")
                return@launch
            }

            try {
                val pickupDate = dateFormat.parse(inputs.pickupDate)
                    ?: throw IllegalArgumentException("Invalid pickup date format (use YYYY-MM-DD).")
                val dropOffDate = dateFormat.parse(inputs.dropOffDate)
                    ?: throw IllegalArgumentException("Invalid drop-off date format (use YYYY-MM-DD).")
                val today = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.time

                if (pickupDate.before(today)) throw IllegalArgumentException("Pickup date cannot be in the past.")
                if (dropOffDate.before(pickupDate)) throw IllegalArgumentException("Drop-off date must be after pickup date.")

                val details = RentalDetails(
                    pickupLocation = inputs.pickupLocation,
                    dropOffLocation = inputs.dropOffLocation,
                    pickupDate = inputs.pickupDate,
                    dropOffDate = inputs.dropOffDate
                )
                val url = repository.createKayakLink(details)
                _state.value = CarRentalState.Navigate(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            } catch (e: IllegalArgumentException) {
                _state.value = CarRentalState.Error(e.message ?: "Invalid input. Please check your entries.")
            } catch (e: Exception) {
                _state.value = CarRentalState.Error("Failed to generate link: ${e.message}")
            }
        }
    }

    private fun updateInputs(update: CarRentalState.Inputs.() -> CarRentalState.Inputs) {
        val current = _state.value as? CarRentalState.Inputs ?: CarRentalState.Inputs()
        _state.value = current.update()
    }

    private fun fetchSuggestions(query: String, isPickup: Boolean) {
        if (query.length < 3) {
            if (isPickup) _pickupSuggestions.value = emptyList()
            else _dropOffSuggestions.value = emptyList()
            return
        }

        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .setSessionToken(token)
            .setCountries("US") // Restrict to US for Kayak compatibility
            .build()

        viewModelScope.launch {
            placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener { response ->
                    val suggestions = response.autocompletePredictions.map { it.getFullText(null).toString() }
                    if (isPickup) _pickupSuggestions.value = suggestions
                    else _dropOffSuggestions.value = suggestions
                }
                .addOnFailureListener {
                    if (isPickup) _pickupSuggestions.value = emptyList()
                    else _dropOffSuggestions.value = emptyList()
                }
        }
    }
}