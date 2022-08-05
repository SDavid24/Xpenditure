package com.example.xpenditure.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.fragment.app.Fragment
import com.example.xpenditure.Cell

object Constants {

    const val EMPLOYEELIST = "EmployeeList"
    const val EXPENSELIST = "expenseList"
    const val FULLNAME: String = "fullName"
    const val EMAIL: String = "email"
    const val IMAGE: String = "image"
    const val LOCATION : String = "location"
    const val JOBDESCRIPTION: String = "jobDescription"
    const val DEPARTMENT: String = "department"

    const val ALLEXPENSES = "AllExpenses"
    const val DATE : String = "date"
    const val CREATEDBY : String = "createdBy"
    const val MERCHANTTYPE: String = "merchantType"
    const val TOTALAMOUNT: String = "totalAmount"
    const val COMMENTDETAIL: String = "commentDetail"
    const val EXPENSEDETAIL: String = "expenseDetail"
    const val RECEIPTIMAGE: String = "receiptImage"
    const val EXPENSEID: String = "expenseId"


    //Create a variable for GALLERY Selection which will be later used in the onActivityResult method
    const val READ_STORAGE_PERMISSION_CODE = 1
    // A unique code of image selection from Phone Storage.
    const val PICK_IMAGE_REQUEST_CODE = 2

    const val CREATE_BOARD_REQUEST_CODE = 10

    /**A function for user profile image selection from phone storage*/
    fun showImageChooser(fragment: Fragment) {
        // An intent for launching the image selection of phone storage.
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        // Launches the image selection of phone storage using the constant code.
        fragment.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    /**A function to get the extension of selected image.*/
    fun getFileExtension(activity: Activity, uri: Uri) : String?{
        return  MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri))
    }
}