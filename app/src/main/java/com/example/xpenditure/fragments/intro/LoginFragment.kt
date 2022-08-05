package com.example.xpenditure.fragments.intro

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.xpenditure.R
import com.example.xpenditure.databinding.FragmentLoginBinding
import com.example.xpenditure.firebase.FirestoreClass
import com.example.xpenditure.fragments.operations.BaseFragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_login.*
import androidx.appcompat.app.AppCompatActivity
import com.example.xpenditure.fragments.operations.HomeFragment


class LoginFragment : BaseFragment() {
    private val auth = FirebaseAuth.getInstance()
    lateinit var loginBinding: FragmentLoginBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        loginBinding = FragmentLoginBinding.inflate(inflater,container,false)

        return loginBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val signupFragment = SignupFragment()

        //On click listener for the sign in button
        loginBinding.btnSignIn.setOnClickListener {
            signInUserWithDetails()
        }

        //On click listener for the highlighted sign up text
        /*loginBinding.textSignUpFragment.setOnClickListener{
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.flFragment, signupFragment)
                commit()
            }
        }*/
        setupActionBar()
    }

    //Method to configure the Action/tool bar
    private fun setupActionBar() {
        val signupFragment = SignupFragment()

        (activity as AppCompatActivity?)!!.setSupportActionBar(loginBinding.toolbarSignInActivity)

        val actionBar = (activity as AppCompatActivity?)!!.supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black)
        }

        loginBinding.toolbarSignInActivity.setNavigationOnClickListener {
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.flFragment, signupFragment)
                commit()
        }
    }

        loginBinding.tvJoinNow.setOnClickListener {
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.flFragment, signupFragment)
                commit()
            }

        }

    }
        /**Method that carries out the sign in process by first calling the form validity function and then calling the Firestore sign in function*/
    private fun signInUserWithDetails() {
        val email = loginBinding.etEmail.text.toString().trim { it <= ' ' }
        val password = loginBinding.etPassword.text.toString().trim { it <= ' ' }

        //Checking the form validity
        if (checkForm(email, password)){
            showProgressDialog("Logging in...")

            //Signing in the user using the firebase auth inbuilt method
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task->
                if (task.isSuccessful){

                    //Loading the signed in user's data from Firestore using the method from Firestore class
                    FirestoreClass().loadUserDataFragment(this)


                    hideProgressDialog()
                }else{
                    hideProgressDialog()
                    Toast.makeText(
                        requireContext(), "Sign in failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    /**Method that first checks the validity of the form if it is ready to be processed*/
    private fun checkForm(email: String, password: String) : Boolean{

        return when {
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please enter your email")
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please enter your password")
                false
            }

            else -> {
                true
            }
        }
    }
}