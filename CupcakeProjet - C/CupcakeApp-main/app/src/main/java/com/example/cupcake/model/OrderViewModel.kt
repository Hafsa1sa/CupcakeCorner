package com.example.cupcake.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

// Price for a single cupcake
private const val PRICE_PER_CUPCAKE = 2.00

// Additional cost for same day pickup of an order
private const val PRICE_FOR_SAME_DAY_PICKUP = 3.00

class OrderViewModel : ViewModel() {
    private val _quantity = MutableLiveData<Int>()
    val quantity: LiveData<Int> = _quantity

    private val _flavor = MutableLiveData<String>()
    val flavor: LiveData<String> = _flavor

    private val _pickupDate = MutableLiveData<Date?>() // Change to Date? for nullability
    val pickupDate: LiveData<String> = Transformations.map(_pickupDate) {
        it?.let { date ->
            SimpleDateFormat("E MMM d", Locale.getDefault()).format(date)
        } ?: "" // Provide an empty string if date is null
    }

    private val _price = MutableLiveData<Double>()
    val price: LiveData<String> = Transformations.map(_price) {
        NumberFormat.getCurrencyInstance().format(it)
    }

    val dateOptions = getPickupOptions()

    init {
        resetOrder()
    }

    fun setQuantity(numberOfCupcakes: Int) {
        _quantity.value = numberOfCupcakes
        updatePrice()
    }

    fun setFlavor(desiredFlavor: String) {
        _flavor.value = desiredFlavor
    }

    fun setPickupDate(pickupDate: Date) {
        _pickupDate.value = pickupDate
        updatePrice()
    }

    fun hasNoFlavorSet(): Boolean {
        return _flavor.value.isNullOrEmpty()
    }

    fun hasNoPickupDateSet(): Boolean {
        return _pickupDate.value == null // Check if pickupDate is null
    }

    private fun getPickupOptions(): List<Date> {
        val options = mutableListOf<Date>()
        val calendar = Calendar.getInstance()

        // Create a list of dates starting with the current date and the following 3 dates
        repeat(4) {
            options.add(calendar.time) // Add the Date object
            calendar.add(Calendar.DATE, 1)
        }

        return options
    }

    private fun updatePrice() {
        var calculatedPrice = (_quantity.value ?: 0) * PRICE_PER_CUPCAKE

        // If the user selected the first option (today) for pickup, add the surcharge
        if (_pickupDate.value == dateOptions[0]) // Compare with Date
            calculatedPrice += PRICE_FOR_SAME_DAY_PICKUP

        _price.value = calculatedPrice
    }

    fun resetOrder() {
        _quantity.value = 0
        _flavor.value = ""
        _pickupDate.value = null // Now this is valid since _pickupDate is nullable
        _price.value = 0.0
    }
}
