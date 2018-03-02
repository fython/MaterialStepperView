package moe.feng.common.stepperview.demo

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem

import moe.feng.alipay.zerosdk.AlipayZeroSdk
import moe.feng.common.stepperview.demo.fragment.VerticalStepperAdapterDemoFragment
import moe.feng.common.stepperview.demo.fragment.VerticalStepperDemoFragment

class MainActivity : AppCompatActivity() {

    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mNavigationView: NavigationView

    private val mVerticalStepperDemoFragment = VerticalStepperDemoFragment()
    private val mVerticalStepperAdapterDemoFragment = VerticalStepperAdapterDemoFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mDrawerLayout = findViewById(R.id.drawer_layout)

        mNavigationView = findViewById(R.id.navigation_view)
        mNavigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected)

        if (savedInstanceState == null) {
            replaceFragment(mVerticalStepperDemoFragment)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            if (mDrawerLayout.isDrawerOpen(mNavigationView)) {
                mDrawerLayout.closeDrawer(mNavigationView)
            } else {
                mDrawerLayout.openDrawer(mNavigationView)
            }
            true
        }
        else -> false
    }

    private fun onNavigationItemSelected(item: MenuItem): Boolean {
        mDrawerLayout.closeDrawer(mNavigationView)
        when (item.itemId) {
            R.id.item_vertical_stepper -> {
                replaceFragment(mVerticalStepperDemoFragment)
                return true
            }
            R.id.item_vertical_stepper_adapter -> {
                replaceFragment(mVerticalStepperAdapterDemoFragment)
                return true
            }
            R.id.action_alipay_donate -> {
                if (AlipayZeroSdk.hasInstalledAlipayClient(this)) {
                    AlipayZeroSdk.startAlipayClient(this, "aehvyvf4taua18zo6e")
                } else {
                    AlertDialog.Builder(this)
                            .setTitle(R.string.donate_dialog_title)
                            .setMessage(R.string.donate_dialog_message)
                            .setPositiveButton(android.R.string.ok, null)
                            .setNeutralButton(R.string.doante_dialog_paypal_button) { _, _ ->
                                openWebsite("https://paypal.me/fython")
                            }
                            .show()
                }
                return true
            }
            R.id.action_fork_on_github -> {
                openWebsite("https://github.com/fython/MaterialStepperView")
                return true
            }
            else -> return false
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
    }

    private fun openWebsite(url: String) {
        val builder = CustomTabsIntent.Builder()
        builder.setToolbarColor(resources.getColor(R.color.colorPrimary))
        builder.build().launchUrl(this, Uri.parse(url))
    }

}
