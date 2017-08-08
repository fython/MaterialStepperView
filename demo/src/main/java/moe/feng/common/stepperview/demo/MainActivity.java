package moe.feng.common.stepperview.demo;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
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

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, mVerticalStepperDemoFragment).commit();
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
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.container, mVerticalStepperDemoFragment).commit();
				return true;
			case R.id.item_vertical_stepper_adapter:
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.container, mVerticalStepperAdapterDemoFragment).commit();
				return true;
			default:
				return false;
		}
	}

}
