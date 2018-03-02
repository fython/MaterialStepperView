package moe.feng.common.stepperview.demo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import moe.feng.alipay.zerosdk.AlipayZeroSdk;
import moe.feng.common.stepperview.demo.fragment.VerticalStepperAdapterDemoFragment;
import moe.feng.common.stepperview.demo.fragment.VerticalStepperDemoFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

	private DrawerLayout mDrawerLayout;
	private NavigationView mNavigationView;

	private Fragment mVerticalStepperDemoFragment = new VerticalStepperDemoFragment(),
			mVerticalStepperAdapterDemoFragment = new VerticalStepperAdapterDemoFragment();

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
		CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
		builder.setToolbarColor(getResources().getColor(R.color.colorPrimary));
		builder.build().launchUrl(this, Uri.parse(url));
	}

}
