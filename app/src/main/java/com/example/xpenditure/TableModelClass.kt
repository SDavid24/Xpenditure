package com.example.xpenditure

import android.os.Parcel
import android.os.Parcelable


data class Cell(
    var expenseId : String = "",
    val createdBy : String = "",
    val date: String = "",
    val merchantType: String = "",
    val totalAmount: Int = 0,
    val commentDetail: String = "",
    val receiptImage: String = ""
): Parcelable {
    constructor(source: Parcel) : this(
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readInt(),
        source.readString()!!,
        source.readString()!!
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(date)
        writeString(createdBy)
        writeString(merchantType)
        writeInt(totalAmount)
        writeString(commentDetail)
        writeString(receiptImage)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Cell> = object : Parcelable.Creator<Cell> {
            override fun createFromParcel(source: Parcel): Cell = Cell(source)
            override fun newArray(size: Int): Array<Cell?> = arrayOfNulls(size)
        }
    }
}
