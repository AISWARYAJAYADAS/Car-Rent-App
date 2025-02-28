package com.example.carrentapp.data.repository

import com.example.carrentapp.data.model.RentalDetails
import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CarRentalRepositoryTest {

    private val repository = CarRentalRepository()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    @Test
    fun `createKayakLink generates correct URL with drop-off location`() {
        val details = RentalDetails(
            pickupLocation = "Los Angeles, CA",
            dropOffLocation = "SFO",
            pickupDate = "2025-03-01",
            dropOffDate = "2025-03-05"
        )
        val expected = "https://www.kayak.com/in?a=awesomecars&url=/cars/Los Angeles, CA/SFO/2025-03-01/2025-03-05"
        val result = repository.createKayakLink(details)
        assertEquals(expected, result)
    }

    @Test
    fun `createKayakLink uses pickup location when drop-off is null`() {
        val details = RentalDetails(
            pickupLocation = "Los Angeles, CA",
            dropOffLocation = null,
            pickupDate = "2025-03-01",
            dropOffDate = "2025-03-05"
        )
        val expected = "https://www.kayak.com/in?a=awesomecars&url=/cars/Los Angeles, CA/Los Angeles, CA/2025-03-01/2025-03-05"
        val result = repository.createKayakLink(details)
        assertEquals(expected, result)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `createKayakLink throws exception for blank pickup location`() {
        val details = RentalDetails(
            pickupLocation = "",
            dropOffLocation = "SFO",
            pickupDate = "2025-03-01",
            dropOffDate = "2025-03-05"
        )
        repository.createKayakLink(details)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `createKayakLink throws exception for past pickup date`() {
        val pastDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
        val details = RentalDetails(
            pickupLocation = "Los Angeles, CA",
            dropOffLocation = "SFO",
            pickupDate = dateFormat.format(pastDate.time),
            dropOffDate = "2025-03-05"
        )
        repository.createKayakLink(details)
    }
}