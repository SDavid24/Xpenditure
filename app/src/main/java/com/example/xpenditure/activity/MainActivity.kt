package com.example.xpenditure.activity

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.xpenditure.R
import com.example.xpenditure.databinding.ActivityMainBinding
import com.example.xpenditure.firebase.FirestoreClass
import com.example.xpenditure.fragments.SplashFragment
import com.example.xpenditure.fragments.intro.LoginFragment
import com.example.xpenditure.fragments.intro.SignupFragment
import com.example.xpenditure.fragments.operations.AddExpenseFragment
import com.example.xpenditure.fragments.operations.HomeFragment
import com.example.xpenditure.fragments.operations.ProfileFragment
import kotlinx.android.synthetic.main.fragment_splash.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val homeFragment = HomeFragment()

    private val profileFragment = ProfileFragment()
    private val loginFragment = LoginFragment()
    private val splashFragment = SplashFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val profileFragment = ProfileFragment()
        val signupFragment = SignupFragment()
        val addExpenseFragment = AddExpenseFragment()



        chooseFragment()
    }


    private fun chooseFragment(){
        supportFragmentManager.beginTransaction().apply {
           replace(R.id.flFragment, splashFragment)
           commit()


       }

        val currentUserId = FirestoreClass().getCurrentUserId()

        Handler().postDelayed({

            //Checks if there is a logged in user already. If yes, it goes straight
            // to the home page and if not it goes to te login page
            if (currentUserId.isNotEmpty()) {
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.flFragment, homeFragment)
                    commit()
                }
            } else {
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.flFragment, loginFragment)
                    commit()

                }
            }
        }, 2500)
    }
}