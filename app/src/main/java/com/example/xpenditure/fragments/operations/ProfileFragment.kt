package com.example.xpenditure.fragments.operations

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.xpenditure.R
import com.example.xpenditure.databinding.FragmentProfileBinding
import com.example.xpenditure.firebase.FirestoreClass
import com.example.xpenditure.model.EmployeeModel
import com.example.xpenditure.utils.Constants
import com.example.xpenditure.utils.Constants.READ_STORAGE_PERMISSION_CODE
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class ProfileFragment : BaseFragment() {

    private var mSelectedImageFileUri: Uri? = null
    private var mProfileImageFileUri: String = ""
    private val homeFragment = HomeFragment()
    private lateinit var mEmployeeModel: EmployeeModel
    lateinit var profileBinding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        profileBinding = FragmentProfileBinding.inflate(inflater, container, false)
        return profileBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupActionBar()
        //loading the user's data into the apps UI from firebase
        FirestoreClass().loadUserDataFragment(this)

        photoClickedOn()  //calling the photoClickedOn method
        updateProfile()
    }



    private fun setupActionBar() {
        (activity as AppCompatActivity?)!!.setSupportActionBar(profileBinding.toolbarMyProfileActivity)

        val actionBar = (activity as AppCompatActivity?)!!.supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white)
            actionBar.title = resources.getString(R.string.my_profile_title)
        }

        profileBinding.toolbarMyProfileActivity.setNavigationOnClickListener {
            val homeFragment = HomeFragment()
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.flFragment, homeFragment)
                commit()
            }

        }
    }

        /**A function to set the existing details in UI.*/
    fun setUserDataUI(employeeModel: EmployeeModel) {
        // Initialize the user details variable
        mEmployeeModel = employeeModel

        //Load the user image in the ImageView.
        Glide
            .with(this)
            .load(employeeModel.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(profileBinding.ivProfileUserImage)

        profileBinding.etFullName.setText(employeeModel.fullName)
        profileBinding.etJob.setText(employeeModel.jobDescription)
        profileBinding.etLocation.setText(employeeModel.location)
        profileBinding.etDepartment.setText(employeeModel.department)

    }


    /**A function to upload the selected user image to firebase cloud storage.*/
    fun uploadUserImage(){
        //showProgressDialog(resources.getString(R.string.please_wait))

        if(mSelectedImageFileUri != null){
            val sRef : StorageReference = FirebaseStorage.getInstance()
                .reference.child("USER_IMAGE"
                        + System.currentTimeMillis() + "."
                        + Constants.getFileExtension(requireActivity(), mSelectedImageFileUri!!)
                )

            //adding the file to reference
            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                // The image upload is success
                    taskSnapshot ->
                Log.i( "Firebase Image URL", taskSnapshot.metadata!!
                    .reference!!.downloadUrl.toString())

                // Get the downloadable url from the task snapshot
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                        uri->
                    Log.i("Downloadable Image URL", uri.toString())

                    // assign the image url to the variable.
                    mProfileImageFileUri = uri.toString()

                    //hideProgressDialog()

                    // Call a function to update user details in the database.
                    updateUserProfileDataInProfFrag()

                }
            }.addOnFailureListener{
                    exception -> Toast.makeText(requireContext(),
                exception.message, Toast.LENGTH_LONG).show()

                //hideProgressDialog()
            }
        }
    }

    /**Adds the click event for iv_profile_user_image*/
    fun photoClickedOn(){
        profileBinding.ivProfileUserImage.setOnClickListener {

            if(ContextCompat.checkSelfPermission(requireContext(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ){
                Constants.showImageChooser(this)
            }else{
                ActivityCompat.requestPermissions(requireActivity(),
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), READ_STORAGE_PERMISSION_CODE)
            }
        }
    }

    /** This function will identify the result of runtime permission after the user allows or deny
     * permission based on the unique code.*/
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions:Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_STORAGE_PERMISSION_CODE){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)  //Calling the image chooser function.)
            }else{
                Toast.makeText(
                    requireContext(),
                    "Oops you just denied the permission for storage. You can " +
                            "allow it in the settings", Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    /** This gets the result of the image selection based on the constant code.*/
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
            && requestCode == Constants.PICK_IMAGE_REQUEST_CODE
            && data!!.data != null){

            //The uri of selected image from phone storage.
            mSelectedImageFileUri = data.data

            try {
                Glide
                    .with(this)
                    .load(mSelectedImageFileUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(profileBinding.ivProfileUserImage)
            }catch (e: IOException){
                e.printStackTrace()
            }

        }
    }


    /**A function to update the user profile details into the database.*/
    private fun updateUserProfileDataInProfFrag(){
        val employeeHashMap = HashMap<String, Any>()
        var anyChangesMade = false

        if(mProfileImageFileUri.isNotEmpty()
            && mProfileImageFileUri != mEmployeeModel.image){
            employeeHashMap[Constants.IMAGE] = mProfileImageFileUri
            anyChangesMade = true
        }

        if(profileBinding.etFullName.text.toString() != mEmployeeModel.fullName){
            employeeHashMap[Constants.FULLNAME] = profileBinding.etFullName.text.toString()
            anyChangesMade = true
        }

        if(profileBinding.etJob.text.toString() != mEmployeeModel.jobDescription){
            employeeHashMap[Constants.JOBDESCRIPTION] = profileBinding.etJob.text.toString()
            anyChangesMade = true
        }

        if(profileBinding.etLocation.text.toString() != mEmployeeModel.location){
            employeeHashMap[Constants.LOCATION] = profileBinding.etLocation.text.toString()
            anyChangesMade = true
        }

        if(profileBinding.etDepartment.text.toString() != mEmployeeModel.department){
            employeeHashMap[Constants.DEPARTMENT] = profileBinding.etDepartment.text.toString()
            anyChangesMade = true
        }

        // Update the data in the database.
        if (anyChangesMade) {
            showProgressDialog("Updating...")
            FirestoreClass().updateUserProfileData(this, employeeHashMap)

        } else{
            Log.e("AnyChangesMade", "No change was made")
            Toast.makeText(requireContext(), "No change was made", Toast.LENGTH_SHORT).show()
        }
    }

    /**Method that handles the immediate afterwards of the Profile update process*/
    fun profileUpdateSuccess(){
        hideProgressDialog()
        Toast.makeText(requireContext(), "You have successfully updated your profile",
            Toast.LENGTH_LONG).show()

        //hideProgressDialog()
        //setResult(Activity.RESULT_OK)  //Setting the Result_Ok value when the profile updates

        parentFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, homeFragment)
            commit()

        }
    }


    /**Click listener for the update button*/
    private fun updateProfile(){
        profileBinding.btnUpdate.setOnClickListener {

            if(mSelectedImageFileUri != null){
                uploadUserImage()
            }else{
                //showProgressDialog(resources.getString(R.string.please_wait))

                updateUserProfileDataInProfFrag()
            }
        }
    }


}