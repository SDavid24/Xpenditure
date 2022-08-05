package com.example.xpenditure.firebase

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.xpenditure.Cell
import com.example.xpenditure.fragments.intro.LoginFragment
import com.example.xpenditure.fragments.intro.SignupFragment
import com.example.xpenditure.fragments.operations.AddExpenseFragment
import com.example.xpenditure.fragments.operations.HomeFragment
import com.example.xpenditure.fragments.operations.ProfileFragment
import com.example.xpenditure.model.EmployeeModel
import com.example.xpenditure.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {

    private val mFirestoreClass = FirebaseFirestore.getInstance()

    /**Method that handles adding user info into the database during signup using the generated UID*/
    fun registerUser(context: Context, fragment: Fragment, employeeInfo: EmployeeModel){
        mFirestoreClass.collection(Constants.EMPLOYEELIST)
            .document(getCurrentUserId())
            .set(employeeInfo, SetOptions.merge())
            .addOnSuccessListener{
                when(fragment){
                    is SignupFragment ->
                        fragment.userRegisteredSuccess(context)
                }
            }
    }

    fun loadUserDataFragment(fragment: Fragment, readExpenseList: Boolean = false){
        mFirestoreClass.collection(Constants.EMPLOYEELIST)
            .document(getCurrentUserId()).get()
            .addOnSuccessListener { document->

                val loggedInUser = document.toObject(EmployeeModel::class.java)!!
                when (fragment){
                    is LoginFragment ->{
                        fragment.signInSuccess()
                    }

                    is HomeFragment ->{
                        fragment.updateNavigationUserDetails(loggedInUser, readExpenseList)
                    }

                    is ProfileFragment -> {
                        fragment.setUserDataUI(loggedInUser)
                    }
                }
            }
    }


    /**Function to to get the user current  Id*/
    fun getCurrentUserId() : String{
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserId = ""

        if (currentUser != null){
            currentUserId = currentUser.uid
        }

        return currentUserId
    }

    /**A function to update the user profile data into the database.*/
    fun updateUserProfileData(fragment: Fragment, employeeHashMap: HashMap<String, Any>){
        mFirestoreClass.collection(Constants.EMPLOYEELIST)
            .document(getCurrentUserId())
            .update(employeeHashMap)
            .addOnSuccessListener {  //This runs a code of our wish if the registration is successful
                Log.i(fragment.javaClass.simpleName, "Profile Data update")

                when (fragment){
                    is ProfileFragment-> {
                        fragment.profileUpdateSuccess()
                    }
                    is AddExpenseFragment-> {
                        fragment.addUpdateExpenseListSuccess()

                        Log.i(fragment.javaClass.simpleName, "Expense added successfully")
                    }

                }

            }.addOnFailureListener{
                    e->
                when (fragment){

                    is ProfileFragment->{
                        fragment.hideProgressDialog()
                    }
                }

                Log.i(fragment.javaClass.simpleName, "Error while creating a user", e)
                // Toast.makeText(, "Error while updating the profile!",
                // Toast.LENGTH_LONG).show()
            }
    }

    /**A function to update the user profile data into the database.*/
    fun updateCurrentExpense(fragment: AddExpenseFragment, expenseHashMap: HashMap<String, Any>){
        mFirestoreClass.collection(Constants.ALLEXPENSES)
            .document(fragment.expenseID())
            .update(expenseHashMap)
            .addOnSuccessListener {  //This runs a code of our wish if the registration is successful
                Log.e(fragment.javaClass.simpleName, "Profile Data update")

                fragment.hideProgressDialog()
                fragment.addUpdateExpenseListSuccess()
                Log.i(fragment.javaClass.simpleName, "Expense updated successfully")

            }.addOnFailureListener{
                    e->

                Log.e(fragment.javaClass.simpleName, "Error while creating a user", e)
                // Toast.makeText(, "Error while updating the profile!",
                // Toast.LENGTH_LONG).show()
            }
    }


    /** A function for creating a new expense and making an entry in the database.*/
    fun createNewExpense(fragment: AddExpenseFragment, cell: Cell){
        mFirestoreClass.collection(Constants.ALLEXPENSES)
            .document()   //Creating a database document for each boards that's created by using their UIDs which is gotten from the authentication side
            .set(cell, SetOptions.merge()) //This sets & merges all the user Info that's passed
            .addOnSuccessListener {   //This runs a code of our wish if the registration is successful

                fragment.addUpdateExpenseListSuccess()
                Log.e(fragment.javaClass.simpleName, "Expense created successfully")

            }.addOnFailureListener {
                    exception ->
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName,
                    "Error while creating an expense",
                    exception)

            }
    }

    /**A method to get the board list that is assigned to the current user from the database using the UUID*/
    fun getBoardsList(fragment: HomeFragment){
        mFirestoreClass.collection(Constants.ALLEXPENSES)
            // A where array query is used as we want the list of the board in which the user is assigned. So here you can pass the current user id.
            .whereEqualTo(Constants.CREATEDBY, getCurrentUserId())
            .get()
            .addOnSuccessListener {
                    document ->    //This document is a snapshot that contains only the boards that are assigned to us
                Log.i(fragment.javaClass.simpleName, document.documents.toString())

                // Here we have created a new instance for Boards ArrayList.
                val boardList: ArrayList<Cell> = ArrayList()

                // A for loop as per the list of documents to convert them into Boards ArrayList.
                for (i in document.documents){
                    val board = i.toObject(Cell::class.java)
                    board!!.expenseId = i.id  //getting the id of each board
                    boardList.add(board)
                }

                //Calling the method that sets up the recycler view using the data gotten from the database
                fragment.populateBoardsListToUI(boardList)

            }.addOnFailureListener { e ->
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName, "Error while  populating with expense", e)

            }
    }

    fun searchMerchantWord(fragment: HomeFragment, merchantName: String){

        mFirestoreClass.collection(Constants.ALLEXPENSES)
            .whereEqualTo(Constants.CREATEDBY, getCurrentUserId())
            .whereEqualTo(Constants.MERCHANTTYPE, merchantName)
            .get()
            .addOnSuccessListener {
                    document ->    //This document is a snapshot that contains only the boards that are assigned to us
                Log.e("searched successfully", document.documents.toString())

                // Here we have created a new instance for Boards ArrayList.
                val boardList: ArrayList<Cell> = ArrayList()

                // A for loop as per the list of documents to convert them into Boards ArrayList.
                for (i in document.documents){
                    val board = i.toObject(Cell::class.java)
                    board!!.expenseId = i.id  //getting the id of each board
                    boardList.add(board)
                }

                //Calling the method that sets up the recycler view using the data gotten from the database
                fragment.populateBoardsListToUI(boardList)
                fragment.hideProgressDialog()
            }.addOnFailureListener { e ->
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName, "Error while  populating with expense", e)
            }
    }

    fun searchByDate(fragment: HomeFragment, date: String){
        mFirestoreClass.collection(Constants.ALLEXPENSES)
            .whereEqualTo(Constants.CREATEDBY, getCurrentUserId())
            .whereEqualTo(Constants.DATE, date)
            .get()
            .addOnSuccessListener {
                    document ->    //This document is a snapshot that contains only the boards that are assigned to us
                Log.i(fragment.javaClass.simpleName, document.documents.toString())
                Log.e("searched successfully", document.documents.toString())

                // Here we have created a new instance for Boards ArrayList.
                val boardList: ArrayList<Cell> = ArrayList()

                // A for loop as per the list of documents to convert them into Boards ArrayList.
                for (i in document.documents){
                    val board = i.toObject(Cell::class.java)
                    board!!.expenseId = i.id  //getting the id of each board
                    boardList.add(board)
                }

                //Calling the method that sets up the recycler view using the data gotten from the database
                fragment.populateBoardsListToUI(boardList)
                fragment.hideProgressDialog()

            }.addOnFailureListener { e ->
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName, "Error while  populating with expense", e)
            }
    }

    fun searchAmountRange(fragment: HomeFragment, amountFrom: Int, amountTo:Int){
        mFirestoreClass.collection(Constants.ALLEXPENSES)
            .whereEqualTo(Constants.CREATEDBY, getCurrentUserId())
            .whereGreaterThanOrEqualTo(Constants.TOTALAMOUNT, amountFrom)
            .whereLessThanOrEqualTo(Constants.TOTALAMOUNT, amountTo)
            .get()
            .addOnSuccessListener {
                    document ->    //This document is a snapshot that contains only the boards that are assigned to us
                Log.i(fragment.javaClass.simpleName, document.documents.toString())
                Log.e("searched successfully", document.documents.toString())

                // Here we have created a new instance for Boards ArrayList.
                val boardList: ArrayList<Cell> = ArrayList()

                // A for loop as per the list of documents to convert them into Boards ArrayList.
                for (i in document.documents){
                    val board = i.toObject(Cell::class.java)
                    board!!.expenseId = i.id  //getting the id of each board
                    boardList.add(board)
                }

                //Calling the method that sets up the recycler view using the data gotten from the database
                fragment.populateBoardsListToUI(boardList)
                fragment.hideProgressDialog()

            }.addOnFailureListener { e ->
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName, "Error while  populating with expense", e)

            }
    }


}