package com.vjezba.androidjetpackgithub.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.annotation.Nullable
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.vjezba.androidjetpackgithub.R
import kotlinx.android.synthetic.main.activity_languages.*
import kotlinx.android.synthetic.main.activity_languages_main.*


class BaseDrawerActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

//    var drawerLayout: DrawerLayout? = null
//    var toolbar: Toolbar? = null
//    var navigationView: NavigationView? = null

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_languages)
        //val toolbar: Toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        //drawerLayout = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout!!.setDrawerListener(toggle)
        toggle.syncState()
        //drawer_layout = findViewById<View>(R.id.nav_view) as NavigationView
        nav_view!!.setNavigationItemSelectedListener(this)
    }


    override fun onBackPressed() {
        if (drawer_layout?.isDrawerOpen(GravityCompat.START) ?: false) {
            drawer_layout?.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    /**
     * Called when an item in the navigation menu is selected.
     *
     * @param item The selected item
     * @return true to display the item as the selected item
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        //to prevent current item select over and over

        //to prevent current item select over and over
        if (item.isChecked) {
            drawer_layout?.closeDrawer(GravityCompat.START)
            return false
        }

        if (id == R.id.repository_fragment) {
            // Handle the camera action
            startActivity(Intent(applicationContext, RepositoriesActivity::class.java))
        }
        drawer_layout?.closeDrawer(GravityCompat.START)
        return true
    }
}