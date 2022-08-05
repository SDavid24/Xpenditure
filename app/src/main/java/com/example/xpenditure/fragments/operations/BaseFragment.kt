package com.example.xpenditure.fragments.operations

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.xpenditure.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.dialog_progress.*
import kotlinx.android.synthetic.main.fragment_add_expense.*
import java.text.SimpleDateFormat
import java.util.*

open class BaseFragment : Fragment(R.layout.fragment_base) {
    private lateinit var mProgressDialog: Dialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    /**Method that handles the immediate afterwards of the sign up process*/
    fun userRegisteredSuccess(context: Context) {
        Toast.makeText(
            context, "You have successfully registered",
            Toast.LENGTH_LONG
        ).show()
    }


    fun signInSuccess() {
        val homeFragment = HomeFragment()

        parentFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, homeFragment)
            commit()

            //finish() //Finishes the activity
        }
    }

    /**Method to show the circling progress dialog when something is loading*/
    fun showProgressDialog(text: String) {

        /*Set the screen content from a layout resource
    The resource will be inflated, adding all top-level views to the screen
     */
        mProgressDialog = Dialog(requireContext())
        mProgressDialog.setContentView(R.layout.dialog_progress)  //Setting the circling progress icon
        mProgressDialog.tv_progress_text.text = text   //Setting the text

        //Starts the dialog and displays is on the screen
        mProgressDialog.show()
    }


    /**Method to dismiss dialog*/
    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }

    /**Method for the snack bar that'll display throughout the app*/
    fun showErrorSnackBar(message: String) {
        val snackBar = Snackbar.make(
            // binding!!.root.findViewById(
            requireActivity().findViewById(android.R.id.content),
            message, Snackbar.LENGTH_LONG
        )
        val snackBarView = snackBar.view

        snackBarView.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.snackbar_error_color
            )
        )  //To set background color of snack bar

        snackBar.show()
        //    <color name="snackBar_error_color">#F72400</color>
    }

    fun addUpdateExpenseListSuccess() {
        val homeFragment = HomeFragment()

        //hideProgressDialog()
        Toast.makeText(
            context, "You have successfully uploaded an expense",
            Toast.LENGTH_LONG
        ).show()
        parentFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, homeFragment)
            commit()
        }
    }


    /**
     * The function to show the DatePicker Dialog and select the due date.
     */
    fun showDatePicker(fragment: Fragment) {
        var mSelectedDueDateMilliSeconds: Long = 0

        /**
         * This Gets a calendar using the default time zone and locale.
         * The calender returned is based on the current time
         * in the default time zone with the default.
         */
        val c = Calendar.getInstance()
        val year =
            c.get(Calendar.YEAR) // Returns the value of the given calendar field. This indicates YEAR
        val month = c.get(Calendar.MONTH) // This indicates the Month
        val day = c.get(Calendar.DAY_OF_MONTH) // This indicates the Day

        /**
         * Creates a new date picker dialog for the specified date using the parent
         * context's default date picker dialog theme.
         */
        val dpd = DatePickerDialog(requireContext(),
            { _, year, monthOfYear, dayOfMonth ->

                /*
                 Here the selected date is set into format i.e : day/Month/Year
                  And the month is counted in java is 0 to 11 so we need to add +1
                  so it can be as selected.*/

                // Here we have appended 0 if the selected day/month is smaller than 10 to make
                // it double digit value.
                val sDayOfMonth = if (dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"

                val sMonthOfYear =
                    if ((monthOfYear + 1) < 10) "0${monthOfYear + 1}" else "${monthOfYear + 1}"

                val selectedDate = "$sDayOfMonth/$sMonthOfYear/$year"

                // Selected date it set to the TextView to make it visible to user.
                if (fragment is AddExpenseFragment){
                    fragment.addExpenseBinding.etAddDate.setText(selectedDate)
                }

                if (fragment is HomeFragment){
                    fragment.et_date_search.setText(selectedDate)
                }

                /**
                 * Here we have taken an instance of Date Formatter as it will format our
                 * selected date in the format which we pass it as an parameter and Locale.
                 * Here I have passed the format as dd/MM/yyyy.
                 */
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)

                // The formatter will parse the selected date in to Date object
                // so we can simply get date in to milliseconds.
                val theDate = sdf.parse(selectedDate)


                /** Here we get the time in milliSeconds from Date object*/

                mSelectedDueDateMilliSeconds = theDate!!.time
            },
            year,
            month,
            day
        )

        dpd.show() // It is used to show the datePicker Dialog.
    }


}