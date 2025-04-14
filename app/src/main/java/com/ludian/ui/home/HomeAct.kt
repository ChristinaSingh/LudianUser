package com.ludian.ui.home

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ludian.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeAct : AppCompatActivity() {

    var navView: BottomNavigationView? = null
    var navController: NavController? = null
    var ivLocation: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        navView = findViewById<BottomNavigationView>(R.id.nav_view)
        navView!!.itemIconTintList = null;



        val appBarConfiguration: AppBarConfiguration = AppBarConfiguration.Builder(
            R.id.navigation_home,
            R.id.navigation_explore,
            R.id.navigation_bookmark,
            R.id.navigation_booking,

            R.id.navigation_profile).build()


      //  val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
      //  val navController = navHostFragment.navController



        navController = Navigation.findNavController(this, R.id.nav_host_fragment)

        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        NavigationUI.setupWithNavController(navView!!, navController!!)


    }

}