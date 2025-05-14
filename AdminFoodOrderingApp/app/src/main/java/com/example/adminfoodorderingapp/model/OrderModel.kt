package com.example.adminfoodorderingapp.model

data class OrderModel (
    var userId: String? = null,
    var userName: String? = null,
    var foodItems: MutableList<CartItems>? = null,
    var address: String? = null,
    var totalPrice: Int? = null,
    var phone: String? = null,
    var orderAccepted: Boolean = false,
    var paymentReceived: Boolean = false,
    var itemPushKey: String? = null,
    var time: String? = null,
    var status: String = "")