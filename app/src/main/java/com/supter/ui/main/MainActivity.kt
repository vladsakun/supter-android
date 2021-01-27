package com.supter.ui.main

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.navigation.NavigationView
import com.google.android.material.transition.MaterialElevationScale
import com.supter.R
import com.supter.databinding.ActivityMainBinding
import com.supter.ui.main.purchase.create.AddPurchaseFragmentDirections
import com.supter.utils.SystemUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var navView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration

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

        binding.myAppBarMain.fab.apply {
            setOnClickListener {
                navigateToAddPurchase()
            }
        }
    }

    private fun navigateToAddPurchase() {

//        currentNavigationFragment?.apply {
//            exitTransition = MaterialElevationScale(false).apply {
//                duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
//            }
//            reenterTransition = MaterialElevationScale(true).apply {
//                duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
//            }
//        }
//
//        val direction = AddPurchaseFragmentDirections.actionGlobalAddPurchase()
//        findNavController(R.id.nav_host_fragment).navigate(direction)

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.myAppBarMain.addPurchase.root)

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                binding.myAppBarMain.fab.isVisible = newState != BottomSheetBehavior.STATE_EXPANDED
            }

        })
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
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