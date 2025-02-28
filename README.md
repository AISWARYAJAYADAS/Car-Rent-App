# CarRentalApp
A sleek and simple Android application designed to help users search for car rentals on Kayak with ease.

## Overview
This app allows users to plan their car rentals by entering key details and generating a Kayak search link. It features a modern UI built with Jetpack Compose, location autosuggestions powered by the Google Places API, and a dark mode toggle for enhanced usability.

### Features
- **Pickup and Drop-off Locations**: Enter a required pickup location and an optional drop-off location.
- **Date Selection**: Choose rental pickup and drop-off dates using intuitive date pickers.
- **Kayak Integration**: Generates and opens a Kayak search URL in the browser using `Intent` for navigation.
- **Location Autosuggestions**: Provides location suggestions via Google Places API (currently non-functional due to billing requirements).
- **Dark Mode**: Toggle between light and dark themes for a personalized experience.

## How It Works

The app follows a structured flow to process user inputs and deliver a seamless experience:

1. **Initialization**: `MyApplication.kt` initializes the Google Places API with an API key.
2. **UI Rendering**: `MainActivity.kt` launches the main screen (`CarRentalSearchScreen.kt`) using Jetpack Compose.
3. **Logic Handling**: `CarRentalViewModel.kt` manages user inputs, validates data, and fetches location suggestions (when billing is enabled).
4. **URL Generation**: `CarRentalRepository.kt` constructs a Kayak URL based on the provided rental details.
5. **Navigation**: Opens the generated URL in the user’s default browser via an `Intent`.

## Testing

- **`CarRentalRepositoryTest.kt`**: Contains unit tests to ensure the Kayak URL is generated correctly and handles edge cases (e.g., blank pickup locations or past dates).

## Tools Used
- Jetpack Compose (for the UI).
- Hilt (to connect parts).
- Google Places API (for location suggestions).

## How to Run
1. Clone the repo.
2. Open in Android Studio.
3. Run on an emulator/device.