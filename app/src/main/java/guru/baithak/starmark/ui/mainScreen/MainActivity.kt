package guru.baithak.starmark.ui.mainScreen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import guru.baithak.starmark.Helpers.sharedPref
import guru.baithak.starmark.Helpers.userName
import guru.baithak.starmark.Helpers.userNameSharedPref
import guru.baithak.starmark.R
import guru.baithak.starmark.ui.mainScreen.Calls.Calls
import guru.baithak.starmark.ui.mainScreen.Groups.Groups
import guru.baithak.starmark.ui.mainScreen.Plans.Plans
import guru.baithak.starmark.ui.register.Login
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        nav_view.getHeaderView(0).findViewById<TextView>(R.id.greetingHeader).text = "Welcome "+getSharedPreferences(sharedPref, Context.MODE_PRIVATE).getString(userNameSharedPref,"User")


        viewSetter()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.logoutActionBar -> {
                logout()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    fun logout(){
        FirebaseAuth.getInstance().signOut()
        getSharedPreferences(sharedPref, Context.MODE_PRIVATE).edit().clear().apply()
        val i = Intent(this,Login::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(i)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.logout->{
               logout()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    fun viewSetter(){
        fragmentTabs.setupWithViewPager(fragmentsViewPager)
        val fragments : ArrayList<Fragment> = ArrayList()
        val names : ArrayList<String> = ArrayList()

        fragments.add(Groups())
        fragments.add(Plans())
        fragments.add(Calls())

        names.add("Groups")
        names.add("Plans")
        names.add("Calls")


        val adapter = FragmentPager(this, supportFragmentManager, fragments, names)
        fragmentsViewPager.adapter = adapter



    }

}
