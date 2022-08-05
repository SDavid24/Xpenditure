package com.example.xpenditure.fragments.intro

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.xpenditure.R
import com.example.xpenditure.databinding.FragmentSignupBinding
import com.example.xpenditure.firebase.FirestoreClass
import com.example.xpenditure.fragments.operations.BaseFragment
import com.example.xpenditure.fragments.operations.HomeFragment
import com.example.xpenditure.model.EmployeeModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase

class SignupFragment : BaseFragment() {

    private val auth = FirebaseAuth.getInstance()
    lateinit var signUpBinding: FragmentSignupBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        signUpBinding = FragmentSignupBinding.inflate(inflater,container,false)

        //binding.viewmodel = vm//attach your viewModel to xml
        return signUpBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        signUpBinding.btnSignUp.setOnClickListener {
            signupUser()
        }
        setupActionBar()

    }


    //Method to configure the Action/tool bar
    private fun setupActionBar() {
        val loginFragment = LoginFragment()

        (activity as AppCompatActivity?)!!.setSupportActionBar(signUpBinding.toolbarSignUpActivity)

        val actionBar = (activity as AppCompatActivity?)!!.supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black)
        }

        signUpBinding.toolbarSignUpActivity.setNavigationOnClickListener {
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.flFragment, loginFragment)
                commit()
            }
        }
        signUpBinding.tvSignIn.setOnClickListener {
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.flFragment, loginFragment)
                commit()
            }
        }

    }

    /**Method that carries out the sign up process by first calling the form validity function and then calling the Firestore sign in function*/
    private fun signupUser(){
        //Initialising the text views
        val fullName = signUpBinding.etNameSignUp.text.toString().trim { it <= ' ' }
        val email = signUpBinding.etEmailSignUp.text.toString().trim { it <= ' ' }
        val password= signUpBinding.etPasswordSignUp.text.toString().trim { it <= ' ' }
        val confirmPassword = signUpBinding.etConfirmPasswordSignUp.text.toString().trim { it <= ' ' }

        //Checking the form validity
        if (checkForm(fullName, email, password, confirmPassword)){
            if(password == confirmPassword ) {
                showProgressDialog("Please wait")

                //Creating the user using the firebase auth inbuilt method
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser = task.result.user!!
                        val newEmail = firebaseUser.email!!
                        val employee = EmployeeModel(firebaseUser.uid, fullName,
                            newEmail
                        )

                        //Updating the new user's data to Firestore using the
                        // method from Firestore class
                        FirestoreClass().registerUser(requireContext(), this , employee)
                        hideProgressDialog()
                        goToHomeFragment()

                    } else {
                        Toast.makeText(
                            requireContext(), "Registration failed", Toast.LENGTH_SHORT
                        ).show()
                        hideProgressDialog()
                    }
                }

            }else{
                Toast.makeText(requireContext(), "Password is not consistent." +
                        "Confirm again! ", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**Method that first checks the validity of the form if it is ready to be processed*/
    private fun checkForm(firstName: String, email: String, password: String, confirmPassword: String) : Boolean{

        return when {
            TextUtils.isEmpty(firstName) -> {
                showErrorSnackBar("Please enter your full name")
                false
            }
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please enter an email")
                return false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please enter a password")
                return false
            }
            TextUtils.isEmpty(confirmPassword) -> {
                showErrorSnackBar("Please confirm password")
                return false
            }

            else -> {
                return true
            }
        }
    }

    /**Actions to be carried when the sign up process is on going*/
  /*  private fun signUploading(){
        signUpBinding.btnSignUpFragment.visibility = View.GONE
        signUpBinding.signUpProgressBarFragment.visibility = View.VISIBLE
    }*/

    /**Actions to be carried out if an error occurs and the sign up process stops*/
   /* fun signUploadingStopped(){
        signUpBinding.btnSignUpFragment.visibility = View.VISIBLE
        signUpBinding.signUpProgressBarFragment.visibility = View.GONE
    }*/

    private fun goToHomeFragment(){
        val homeFragment = HomeFragment()
        parentFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, homeFragment)
            commit()

        }
    }

}