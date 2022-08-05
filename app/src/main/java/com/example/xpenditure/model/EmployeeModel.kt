package com.example.xpenditure.model

import android.os.Parcel
import android.os.Parcelable
import com.example.xpenditure.Cell
import com.example.xpenditure.model.EmployeeModel.Companion.CREATOR

data class EmployeeModel(
    val employeeId : String = "",
    val fullName : String = "",
    val email: String = "",
    val image : String = "",
    val jobDescription : String = "",
    val location : String = "",
    val department : String = "",
    //val expenseList : ArrayList<Cell> = ArrayList()

) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        //source.createTypedArrayList(Cell.CREATOR)!!
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(employeeId)
        writeString(fullName)
        writeString(email)
        writeString(image)
        writeString(jobDescription)
        writeString(location)
        writeString(department)
       // writeTypedList(expenseList)


    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<EmployeeModel> = object : Parcelable.Creator<EmployeeModel> {
            override fun createFromParcel(source: Parcel): EmployeeModel = EmployeeModel(source)
            override fun newArray(size: Int): Array<EmployeeModel?> = arrayOfNulls(size)
        }
    }
}
