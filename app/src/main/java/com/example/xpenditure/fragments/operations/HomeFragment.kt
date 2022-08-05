package com.example.xpenditure.fragments.operations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.evrencoskun.tableview.TableView
import com.example.xpenditure.*
import com.example.xpenditure.databinding.FragmentHomeBinding
import com.example.xpenditure.firebase.FirestoreClass
import com.example.xpenditure.fragments.intro.LoginFragment
import com.example.xpenditure.model.EmployeeModel
import com.example.xpenditure.utils.Constants
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.fragment_add_expense.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.nav_header_main.*

class HomeFragment : BaseFragment(), NavigationView.OnNavigationItemSelectedListener{
    lateinit var homeBinding: FragmentHomeBinding
    private lateinit var fullName : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return homeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //loading the user's data into the apps UI from firebase
        FirestoreClass().loadUserDataFragment(this, true)

        nav_view.setNavigationItemSelectedListener(this)
        setUpActionBar()

        fab_create_board.setOnClickListener {
            val addExpenseFragment = AddExpenseFragment()
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.flFragment, addExpenseFragment)
                addToBackStack(null)
                commit()
            }
        }
    }

    /**Method to configure toolbar and also insert Hamburger icon*/
    private fun setUpActionBar(){

        (activity as AppCompatActivity).setSupportActionBar(toolbar_main_activity)
        toolbar_main_activity.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        toolbar_main_activity.setNavigationOnClickListener {
            toggleDrawer()
        }
        radioGroupConfig()
        searchTable()
    }

    private fun radioGroupConfig(){
        et_date_search.setOnClickListener {
            showDatePicker(this)
        }
        tv_filter.setOnClickListener {
            if (radioGroup.isVisible || ll_searchMerchant.isVisible
                || ll_searchAmount.isVisible || ll_searchAmount.isVisible
            ) {
                radioGroup.visibility = GONE
                ll_searchMerchant.visibility = GONE
                ll_searchAmount.visibility = GONE
                ll_searchAmount.visibility = GONE
            } else {
                radioGroup.visibility = VISIBLE
            }
        }

        radioGroup.setOnCheckedChangeListener { radioGroup, checkedId ->
            if (checkedId == R.id.merchant_radio){
                if (!ll_searchMerchant.isVisible){
                    ll_searchMerchant.visibility = VISIBLE
                    ll_searchAmount.visibility = GONE
                    ll_search_Date.visibility = GONE
                }
            }

            if (checkedId == R.id.amount_radio){
                if (!ll_searchAmount.isVisible){
                    ll_searchMerchant.visibility = GONE
                    ll_searchAmount.visibility = VISIBLE
                    ll_search_Date.visibility = GONE

                }
            }
            if (checkedId == R.id.date_radio){
                if (!ll_search_Date.isVisible){
                    ll_searchMerchant.visibility = GONE
                    ll_searchAmount.visibility = GONE
                    ll_search_Date.visibility = VISIBLE
                }
            }
        }
    }

    private fun searchTable(){
        btn_search_merchant.setOnClickListener {
            val merchantName = et_merchant_search.text
            if(merchantName.isNotEmpty()) {
                showProgressDialog("Loading...")
                FirestoreClass().searchMerchantWord(this, merchantName.toString())
            }else{
                Toast.makeText(requireContext(), "Enter Merchant name",
                    Toast.LENGTH_SHORT).show()
            }
        }

        btn_search_amount.setOnClickListener {
            val amountFrom  = et_amountMin.text.toString()
            val  amountTo = et_amountMax.text.toString()
            if(amountTo.isNotEmpty() && amountFrom.isNotEmpty()) {

                showProgressDialog("Loading...")
                FirestoreClass().searchAmountRange(this,
                    amountFrom.toInt(), amountTo.toInt())

            } else{
                Toast.makeText(requireContext(), "Enter all amount fields",
                    Toast.LENGTH_SHORT).show()
            }
        }

        btn_search_date.setOnClickListener {
            val date = et_date_search.text.toString()
            if(date.isNotEmpty() ) {
                showProgressDialog("Loading...")
                FirestoreClass().searchByDate(this,date)

            }else{
                Toast.makeText(requireContext(), "Enter a Date", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**A function to populate the result of BOARDS list in the UI i.e sets up the recyclerView.*/
    fun populateBoardsListToUI(expenseList: ArrayList<Cell>){
        hideProgressDialog()

        if(expenseList.size > 0){
            table_recycler_view.visibility = VISIBLE
            no_result_found.visibility = GONE

            table_recycler_view.layoutManager = LinearLayoutManager(requireContext())
            table_recycler_view.setHasFixedSize(true)

            val adapter = DataCellAdapter(requireContext(), expenseList)
            table_recycler_view.adapter = adapter

            adapter.setOnClickListener(object : DataCellAdapter.OnClickListener{
                override fun onClick(position: Int, model: Cell) {

                    val addExpenseFragment = AddExpenseFragment()

                    val bundle = Bundle()
                    bundle.putParcelable(Constants.EXPENSEDETAIL, model)
                    addExpenseFragment.arguments = bundle
                    parentFragmentManager.beginTransaction().apply {
                        replace(R.id.flFragment, addExpenseFragment)
                        addToBackStack("Home_TAG")
                        commit()
                    }
                }

            })

        }else{

            table_recycler_view.visibility = GONE
            no_result_found.visibility = VISIBLE
        }
    }

    /**A function to get the current user details from firebase.*/
    fun updateNavigationUserDetails(employeeModel: EmployeeModel, readExpenseList : Boolean){

        //hideProgressDialog()

        fullName = employeeModel.fullName

        // Load the user image in the ImageView.
        if(activity != null) {
            Glide
                .with(this)
                .load(employeeModel.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(iv_user_image)
            //tv_username.text = user.name
            tv_username.text = employeeModel.fullName

        }

        //conditional that checks and only loads the expense list if readBoardsList is true
        if(readExpenseList){
            showProgressDialog("please_wait")
            FirestoreClass().getBoardsList(this)
        }
    }

    /**A function for opening and closing the Navigation Drawer.*/
    private fun toggleDrawer(){
        if (drawer_layout.isDrawerOpen(GravityCompat.START)){
            drawer_layout.closeDrawer(GravityCompat.START)
        }else{
            drawer_layout.openDrawer(GravityCompat.START)
        }
    }

   /**Method that closes the navigation drawer instead of the app when it is open*/
    /*
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)){
            drawer_layout.closeDrawer(GravityCompat.START)
        }else{
            doubleBackToExit()  //This calls the double back press function from the base activity
        }
    }*/

    /**Sets the item click listener for the nav drawer items and the operations they should carry out when clicked*/
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){

            //Using startActivityForResult so it can get the user data into the profile activity
            R.id.nav_my_profile -> {
                val profileFragment = ProfileFragment()
                parentFragmentManager.beginTransaction().apply {
                    replace(R.id.flFragment, profileFragment)
                    commit()
                }
               // startActivityForResult(intent, MY_PROFILE_REQUEST_CODE)
            }

            R.id.nav_sign_out -> {
                FirebaseAuth.getInstance().signOut()

                val loginFragment = LoginFragment()
                parentFragmentManager.beginTransaction().apply {
                    replace(R.id.flFragment, loginFragment)
                    commit()
                }

                //val intent = Intent(this, IntroActivity::class.java)

                //Read about this
                //intent.addFlags((Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK))
                //startActivity(intent)
            }

        }
        drawer_layout.closeDrawer(GravityCompat.START)

        return true
    }
}