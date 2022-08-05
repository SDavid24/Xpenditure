package com.example.xpenditure.fragments.operations

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.xpenditure.Cell
import com.example.xpenditure.R
import com.example.xpenditure.activity.MainActivity
import com.example.xpenditure.databinding.FragmentAddExpenseBinding
import com.example.xpenditure.firebase.FirestoreClass
import com.example.xpenditure.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.fragment_add_expense.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

class AddExpenseFragment : BaseFragment() {
    //private var mSelectedDueDateMilliSeconds: Long = 0
    lateinit var addExpenseBinding: FragmentAddExpenseBinding
    private var saveImageToInternalStorageUri : Uri? = null
    private var mExpenseImageFileUri: String = ""

    private var expenseModel: Cell? = null

    var expenseIdString : String? = null
    private lateinit var fullName : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
      /*  val contextThemeWrapper: Context = ContextThemeWrapper(
            activity, R.style.XpenditureFullscreenContainer
        )

        val localInflater = inflater.cloneInContext(contextThemeWrapper)

        return localInflater.inflate(R.layout.fragment_add_expense, container, false)*/

        addExpenseBinding = FragmentAddExpenseBinding.inflate(inflater, container, false)
        return addExpenseBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupActionBar()
        onClick()
    }

        private fun setupActionBar() {
        (activity as AppCompatActivity?)!!.setSupportActionBar(addExpenseBinding.toolbarAddExpense)

        val bundle = this.arguments
        val actionBar = (activity as AppCompatActivity?)!!.supportActionBar
        if (actionBar != null) {

            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white)

            if(bundle != null){
                expenseModel = bundle.getParcelable(Constants.EXPENSEDETAIL)!!
                actionBar.title = "Edit expense"

                addExpenseBinding.etAddDate.setText(expenseModel!!.date)
                addExpenseBinding.etMerchant.setText(expenseModel!!.merchantType)
                addExpenseBinding.etTotalAmount.setText(expenseModel!!.totalAmount.toString())
                addExpenseBinding.etComment.setText(expenseModel!!.commentDetail)
                addExpenseBinding.tvAddImage.text = "Change Receipt"

                expenseIdString  = expenseModel!!.expenseId

                Glide
                    .with(this)
                    .load(expenseModel!!.receiptImage)
                    .centerCrop()
                    .into(addExpenseBinding.ivReceiptImage)

                btn_add_expense.visibility = View.GONE
                btn_update_expense.visibility = View.VISIBLE
            }else{
                actionBar.title = resources.getString(R.string.add_expense)

                btn_add_expense.visibility = View.VISIBLE
                btn_update_expense.visibility = View.GONE
            }

        }

        addExpenseBinding.toolbarAddExpense.setNavigationOnClickListener {
            val homeFragment = HomeFragment()
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.flFragment, homeFragment)
                commit()
            }
        }
    }

    fun expenseID () : String{
        return expenseIdString!!
    }

    private fun onClick(){
        addExpenseBinding.etAddDate.setOnClickListener {
            showDatePicker(this)
        }

        addExpenseBinding.tvAddImage.setOnClickListener {
            val pictureDialog = AlertDialog.Builder(requireContext())
            pictureDialog.setTitle("Select Action")
            val pictureDialogItems =
                arrayOf("Select photo from gallery", "Capture photo from camera")
            pictureDialog.setItems(pictureDialogItems) { dialog, which ->
                when (which) {
                    // Here we have create the methods for image selection from GALLERY or CAMERA
                    0 -> chooseFromPhotoGallery()
                    1-> takePhotoFromCamera()
                }
            }
            pictureDialog.show()
        }

        addExpenseBinding.btnAddExpense.setOnClickListener {
            uploadExpense()
        }

        addExpenseBinding.btnUpdateExpense.setOnClickListener {
            updateExpense()
        }
    }

    /**A function to update the current expense into the database.*/
    private fun updateExpense(){
        val expenseHashMap = HashMap<String, Any>()
        var anyChangesMade = false

        if(saveImageToInternalStorageUri != null
            && mExpenseImageFileUri != expenseModel!!.receiptImage){
            expenseHashMap[Constants.RECEIPTIMAGE] = mExpenseImageFileUri
            anyChangesMade = true
        }

        if(addExpenseBinding.etMerchant.text.toString() != expenseModel!!.merchantType){
            expenseHashMap[Constants.MERCHANTTYPE] = addExpenseBinding.etMerchant.text.toString()
            anyChangesMade = true
        }

        if(addExpenseBinding.etTotalAmount.text.toString() != expenseModel!!.totalAmount.toString()){
            expenseHashMap[Constants.TOTALAMOUNT] = addExpenseBinding.etTotalAmount
                .text.toString().toInt()
            anyChangesMade = true
        }

        if(addExpenseBinding.etAddDate.text.toString() != expenseModel!!.date){
            expenseHashMap[Constants.DATE] = addExpenseBinding.etAddDate.text.toString()
            anyChangesMade = true
        }

        if(addExpenseBinding.etComment.text.toString() != expenseModel!!.commentDetail){
            expenseHashMap[Constants.COMMENTDETAIL] = addExpenseBinding.etComment.text.toString()
            anyChangesMade = true
        }

        // Update the data in the database.
        if (anyChangesMade) {
            showProgressDialog("Updating...")
            FirestoreClass().updateCurrentExpense(this, expenseHashMap)

        } else{
            Log.e("AnyChangesMade", "No change was made")
            Toast.makeText(requireContext(), "No change was made", Toast.LENGTH_SHORT).show()
        }
    }

    /**Method in charge of the uploading the xpense to firebase. It checks the validity of the expense form, gets the receipt image from gallery or camera and calls firebase createExpense function*/
    private fun uploadExpense() {

        when {
            addExpenseBinding.etAddDate.text.isNullOrEmpty() -> {
                Toast.makeText(
                    requireContext(), "Please enter transaction date",
                    Toast.LENGTH_LONG
                ).show()
            }

            addExpenseBinding.etMerchant.text.isNullOrEmpty() -> {
                Toast.makeText(
                    requireContext(), "Please specify a merchant",
                    Toast.LENGTH_LONG
                ).show()
            }

            addExpenseBinding.etTotalAmount.text.isNullOrEmpty() -> {
                Toast.makeText(
                    requireContext(), "Please enter transaction amount",
                    Toast.LENGTH_LONG
                ).show()
            }

            addExpenseBinding.etComment.text.isNullOrEmpty() -> {
                Toast.makeText(
                    requireContext(), "Please enter a comment on the transaction",
                    Toast.LENGTH_LONG
                ).show()
            }

            saveImageToInternalStorageUri == null -> {
                Toast.makeText(
                    requireContext(), "Please select an image",
                    Toast.LENGTH_LONG
                ).show()
            }

            else -> {

                showProgressDialog("Uploading expense...")

                val sRef: StorageReference = FirebaseStorage.getInstance()
                    .reference.child(
                        "EXPENSE_RECEIPT_IMAGE"
                                + System.currentTimeMillis() + "."
                                + Constants.getFileExtension(
                            requireActivity(),
                            saveImageToInternalStorageUri!!
                        )
                    )

                //adding the file to reference
                sRef.putFile(saveImageToInternalStorageUri!!).addOnSuccessListener {
                    // The image upload is success
                        taskSnapshot ->
                    Log.i(
                        "Firebase Image URL", taskSnapshot.metadata!!
                            .reference!!.downloadUrl.toString()
                    )

                    // Get the downloadable url from the task snapshot
                    taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                        Log.i("Downloadable Image URL", uri.toString())

                        // assign the image url to the variable.
                        mExpenseImageFileUri = uri.toString()

                        // Call a function to update user details in the database.
                        val dateString = addExpenseBinding.etAddDate.text.toString()

                        val merchant = addExpenseBinding.etMerchant.text.toString()

                        val amount = addExpenseBinding.etTotalAmount.text.toString()

                        val comment = addExpenseBinding.etComment.text.toString()

                        val receiptImage = mExpenseImageFileUri

                        val expenseId = ""
                        val createdBy = FirestoreClass().getCurrentUserId()
                        val expense = Cell(expenseId,
                            createdBy, dateString, merchant,
                            amount.toInt(), comment, receiptImage
                        )

                        FirestoreClass().createNewExpense(this, expense) //calling Firebase

                        hideProgressDialog()
                    }
                }.addOnFailureListener { exception ->
                    Toast.makeText(
                        requireContext(),
                        exception.message, Toast.LENGTH_LONG
                    ).show()

                    hideProgressDialog()
                }
            }
        }
    }


    /** A method is used for image selection from GALLERY / PHOTOS of phone storage.*/

    private fun chooseFromPhotoGallery() {
        // Asking the permissions of Storage using DEXTER Library which we have added in gradle file.
        Dexter.withContext(requireContext())
            .withPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    // Here after all the permission are granted launch the gallery to select and image.
                    if (report!!.areAllPermissionsGranted()){
                        val galleryIntent = Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        startActivityForResult(galleryIntent, GALLERY)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest?>?,
                    token: PermissionToken?
                ) {
                    showRationalDialogForPermissions()
                }
            }).onSameThread().check()
    }


    // This function is created to show the alert dialog when the permissions are denied and need to allow it from settings app info.)

    private fun showRationalDialogForPermissions(){
        AlertDialog.Builder(requireContext()).setMessage("It looks like you have turned off " +
                "permissions required for this feature. " +
                "It can be enabled under the Application settings")
            .setPositiveButton("GO TO SETTINGS") {
                    _,_ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", (activity as MainActivity).packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }.setNegativeButton("Cancel"){ dialog, _ ->
                dialog.dismiss()
            }.show()
    }


    /**A method used for asking the permission for camera and storage and image capturing and selection from Camera. */

    private fun takePhotoFromCamera() {
        // Asking the permissions of Storage using DEXTER Library which we have added in gradle file.
        Dexter.withContext(requireContext())
            .withPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    // Here after all the permission are granted launch the gallery to select and image.
                    if (report!!.areAllPermissionsGranted()){
                        val galleryIntent = Intent(
                            MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityForResult(galleryIntent, CAMERA)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest?>?,
                    token: PermissionToken?
                ) {
                    showRationalDialogForPermissions()
                }
            }).onSameThread().check()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if (requestCode == GALLERY){
                if (data != null){
                    val contentURI = data.data

                    try {
                        // Here this is used to get a bitmap from URI
                        @Suppress("DEPRECATION")

                        val selectedImageBitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, contentURI)

                        saveImageToInternalStorageUri = saveImageToInternalStorage(selectedImageBitmap)
                        Log.e("Saved image: ", "Path :: $saveImageToInternalStorageUri")

                        addExpenseBinding.ivReceiptImage.setImageBitmap(selectedImageBitmap)// Set the selected image from GALLERY to imageView.
                    }catch (e: IOException){
                        e.printStackTrace()
                        Toast.makeText(requireContext(),
                            "Failed to load the image from gallery", Toast.LENGTH_SHORT).show()
                    }
                }

            }else if (requestCode == CAMERA){
                val thumbNail : Bitmap = data!!.extras!!.get("data") as Bitmap

                saveImageToInternalStorageUri = saveImageToInternalStorage(thumbNail)

                Log.e("Saved image: ", "Path :: $saveImageToInternalStorageUri")

                addExpenseBinding.ivReceiptImage!!.setImageBitmap(thumbNail)
                //iv_place_image!!.setImageURI(imageUri)
            }
        }
    }



    /**
     * A function to save a copy of an image to internal storage for HappyPlaceApp to use.
     */

    private fun saveImageToInternalStorage(bitmap: Bitmap) : Uri{
        // Get the context wrapper instance
        val wrapper = ContextWrapper(activity as MainActivity )


        // Initializing a new file
        // The bellow line return a directory in internal storage

/**
         * The Mode Private here is
         * File creation mode: the default mode, where the created file can only
         * be accessed by the calling application (or all applications sharing the
         * same user ID).
         */
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)

        // Create a file to save the image
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            val stream : OutputStream = FileOutputStream(file) //Inorder to output an image to our phone
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)  // Compress bitmap

            stream.flush()   // Flush the stream

            stream.close()   // Close stream

        }catch (e: IOException){
            e.printStackTrace()
        }

        //return Uri.parse(file.absolutePath)
        return Uri.fromFile(File(file.absolutePath))
    }



    //Create a variable for GALLERY Selection which will be later used in the onActivityResult method
    companion object{
        private const val GALLERY = 1
        private const val CAMERA = 2
        private const val IMAGE_DIRECTORY = "HappyPlacesImages"
    }
}
