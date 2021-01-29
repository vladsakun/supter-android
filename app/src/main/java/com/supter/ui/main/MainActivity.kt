package com.supter.ui.main

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.navigation.NavigationView
import com.supter.R
import com.supter.data.response.ResultWrapper
import com.supter.data.response.purchase.CreatePurchaseResponse
import com.supter.databinding.ActivityMainBinding
import com.supter.ui.main.purchase.create.AddPurchaseViewModel
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var navView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val viewModel: AddPurchaseViewModel by viewModels()
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    private val currentNavigationFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
            ?.childFragmentManager
            ?.fragments
            ?.first()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)
        val toolbar: Toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)

        drawerLayout = binding.drawerLayout
        navView = binding.navView
        navController = this.findNavController(R.id.nav_host_fragment)

        appBarConfiguration = AppBarConfiguration(navController.graph) //configure nav controller

        setupActionBar(navController)
        setupNavigationMenu(navController)

        bindAddPurchaseViews()

        bottomSheetBehavior = BottomSheetBehavior.from(binding.myAppBarMain.addPurchase.root)

        binding.myAppBarMain.fab.apply {
            setOnClickListener {
                navigateToAddPurchase()
            }
        }
    }

    private fun navigateToAddPurchase() {

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
//                binding.myAppBarMain.fab.isVisible = newState != BottomSheetBehavior.STATE_EXPANDED
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {

                } else {
                    clearAddPurchaseFocus()
                    binding.myAppBarMain.fab.isVisible = true
                }
            }

        })
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        binding.myAppBarMain.fab.isVisible = false
    }

    private fun bindAddPurchaseViews() {
        viewModel.createPurchaseResponse.observe(this) { result ->
            when (result) {
                is ResultWrapper.Success -> handleSuccessResult(result)
                is ResultWrapper.NetworkError -> showErrorToast(getString(R.string.no_internet_connection))
                is ResultWrapper.GenericError -> showErrorToast(
                    result.error?.message
                        ?: getString(R.string.no_internet_connection)
                )
            }
        }

        binding.myAppBarMain.addPurchase.save.setOnClickListener {
            binding.myAppBarMain.addPurchase.run {
                if (purchaseTitle.editText?.text.toString().isNotBlank()
                    && purchasePrice.editText?.text.toString().isNotBlank()
                ) {

                    val title = purchaseTitle.editText?.text.toString()
                    val price = purchasePrice.editText?.text.toString().toDouble()
                    val usability = purchaseUsability.editText?.text.toString()

                    val questionsMap = mapOf(
                        getString(R.string.how_would_the_purchase_be_useful)
                                to usability
                    )

                    viewModel.upsertPurchase(
                        title,
                        price,
                    )
                } else {
                    showErrorToast(getString(R.string.some_fields_are_empty))
                }
            }
        }

        binding.myAppBarMain.addPurchase.collapseSheet.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun showErrorToast(message: String) {
        Toasty.error(this, message).show()
    }

    private fun handleSuccessResult(result: ResultWrapper.Success<CreatePurchaseResponse>) {
        Toasty.success(this, "Successfully created ${result.value.data.title}").show()
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun clearAddPurchaseFocus() {
        with(binding.myAppBarMain.addPurchase) {
            purchaseTitle.editText?.clearFocus()
            purchasePrice.editText?.clearFocus()
            purchaseUsability.editText?.clearFocus()
        }
    }

    private fun setupNavigationMenu(navController: NavController) {
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setupWithNavController(navController)
    }

    private fun setupActionBar(navController: NavController) {
        // This allows NavigationUI to decide what label to show in the action bar
        // By using appBarConfig, it will also determine whether to
        // show the up arrow or drawer menu icon
        setupActionBarWithNavController(this, navController, drawerLayout)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.nav_host_fragment)
        return navigateUp(navController, drawerLayout)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return (item.onNavDestinationSelected(navController)
                || super.onOptionsItemSelected(item))
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun showAddBtn() {
        binding.myAppBarMain.fab.visibility = View.VISIBLE
    }

    fun hideAddBtn() {
        binding.myAppBarMain.fab.visibility = View.INVISIBLE
    }

}