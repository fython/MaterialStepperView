package moe.feng.common.stepperview.demo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

import moe.feng.alipay.zerosdk.AlipayZeroSdk;
import moe.feng.common.stepperview.demo.fragment.VerticalStepperAdapterDemoFragment;
import moe.feng.common.stepperview.demo.fragment.VerticalStepperDemoFragment;

public class MainActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener {

	private DrawerLayout mDrawerLayout;
	private NavigationView mNavigationView;

	private Fragment mVerticalStepperDemoFragment = new VerticalStepperDemoFragment();
	private Fragment mVerticalStepperAdapterDemoFragment = new VerticalStepperAdapterDemoFragment();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		ActionBar actionBar = getSupportActionBar();
		assert actionBar != null;
		actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
		actionBar.setDisplayHomeAsUpEnabled(true);

		mDrawerLayout = findViewById(R.id.drawer_layout);

		mNavigationView = findViewById(R.id.navigation_view);
		mNavigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            replaceFragment(mVerticalStepperDemoFragment);
        }
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				if (mDrawerLayout.isDrawerOpen(mNavigationView)) {
					mDrawerLayout.closeDrawer(mNavigationView);
				} else {
					mDrawerLayout.openDrawer(mNavigationView);
				}
				return true;
			default:
				return false;
		}
	}

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		mDrawerLayout.closeDrawer(mNavigationView);
		switch (item.getItemId()) {
			case R.id.item_vertical_stepper:
				replaceFragment(mVerticalStepperDemoFragment);
				return true;
			case R.id.item_vertical_stepper_adapter:
				replaceFragment(mVerticalStepperAdapterDemoFragment);
				return true;
			case R.id.action_alipay_donate:
				if (AlipayZeroSdk.hasInstalledAlipayClient(this)) {
					AlipayZeroSdk.startAlipayClient(this, "aehvyvf4taua18zo6e");
				} else {
					new AlertDialog.Builder(this)
							.setTitle(R.string.donate_dialog_title)
							.setMessage(R.string.donate_dialog_message)
							.setPositiveButton(android.R.string.ok, null)
							.setNeutralButton(R.string.doante_dialog_paypal_button, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialogInterface, int i) {
									openWebsite("https://paypal.me/fython");
								}
							})
							.show();
				}
				return true;
			case R.id.action_fork_on_github:
				openWebsite("https://github.com/fython/MaterialStepperView");
				return true;
			default:
				return false;
		}
	}

	private void replaceFragment(Fragment fragment) {
		getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
	}

	private void openWebsite(String url) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		try {
			startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
