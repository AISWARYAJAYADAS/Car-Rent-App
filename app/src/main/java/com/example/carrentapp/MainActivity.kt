package com.example.carrentapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.carrentapp.presentation.ui.CarRentalSearchScreen
import com.example.carrentapp.presentation.viewmodel.CarRentalViewModel
import com.example.carrentapp.ui.theme.CarRentAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val viewModel: CarRentalViewModel = hiltViewModel()
            val state by viewModel.state.collectAsState()
            val pickupSuggestions by viewModel.pickupSuggestions.collectAsState()
            val dropoffSuggestions by viewModel.dropOffSuggestions.collectAsState()
            var isDarkMode by remember { mutableStateOf<Boolean?>(null) }
            CarRentAppTheme(useDarkTheme = isDarkMode) {
                CarRentalSearchScreen(
                    state = state,
                    pickupSuggestions = pickupSuggestions,
                    dropOffSuggestions = dropoffSuggestions,
                    onPickupLocationChanged = viewModel::onPickupLocationChanged,
                    onDropOffLocationChanged = viewModel::onDropOffLocationChanged,
                    onPickupDateChanged = viewModel::onPickupDateChanged,
                    onDropOffDateChanged = viewModel::onDropOffDateChanged,
                    onSearchClicked = viewModel::onSearchClicked,
                    onToggleDarkMode = { isDarkMode = it }
                )
            }
        }
    }
}