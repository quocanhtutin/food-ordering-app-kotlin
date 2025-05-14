package com.example.foodorderingapp.model

import android.os.Parcel
import android.os.Parcelable

class OrderModel(
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
//): Parcelable{
//
//    constructor(parcel: Parcel) : this() {
//        userId = parcel.readString()
//        userName = parcel.readString()
//        address = parcel.readString()
//        totalPrice = parcel.readString()
//        phone = parcel.readString()
//        orderAccepted = parcel.readByte() != 0.toByte()
//        paymentReceived = parcel.readByte() != 0.toByte()
//        itemPushKey = parcel.readString()
//        time = parcel.readString()
//    }
//
//    override fun describeContents(): Int {
//        TODO("Not yet implemented")
//    }
//
//    override fun writeToParcel(dest: Parcel, flags: Int) {
//        TODO("Not yet implemented")
//    }
//
//    companion object CREATOR : Parcelable.Creator<OrderModel> {
//        override fun createFromParcel(parcel: Parcel): OrderModel {
//            return OrderModel(parcel)
//        }
//
//        override fun newArray(size: Int): Array<OrderModel?> {
//            return arrayOfNulls(size)
//        }
//    }
//
//}
