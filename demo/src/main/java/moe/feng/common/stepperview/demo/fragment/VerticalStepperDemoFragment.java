package moe.feng.common.stepperview.demo.fragment;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import moe.feng.common.stepperview.VerticalStepperItemView;
import moe.feng.common.stepperview.demo.R;

public class VerticalStepperDemoFragment extends Fragment {

	private VerticalStepperItemView mSteppers[] = new VerticalStepperItemView[3];
	private Button mNextBtn0, mNextBtn1, mPrevBtn1, mNextBtn2, mPrevBtn2;

	private int mActivatedColorRes = R.color.material_blue_500;
	private int mDoneIconRes = R.drawable.ic_done_white_16dp;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_vertical_stepper, parent, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		mSteppers[0] = view.findViewById(R.id.stepper_0);
		mSteppers[1] = view.findViewById(R.id.stepper_1);
		mSteppers[2] = view.findViewById(R.id.stepper_2);

		VerticalStepperItemView.bindSteppers(mSteppers);

		mNextBtn0 = view.findViewById(R.id.button_next_0);
		mNextBtn0.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mSteppers[0].nextStep();
			}
		});

		view.findViewById(R.id.button_test_error).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mSteppers[0].getErrorText() != null) {
					mSteppers[0].setErrorText(null);
				} else {
					mSteppers[0].setErrorText("Test error!");
				}
			}
		});

		mPrevBtn1 = view.findViewById(R.id.button_prev_1);
		mPrevBtn1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mSteppers[1].prevStep();
			}
		});

		mNextBtn1 = view.findViewById(R.id.button_next_1);
		mNextBtn1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mSteppers[1].nextStep();
			}
		});

		mPrevBtn2 = view.findViewById(R.id.button_prev_2);
		mPrevBtn2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mSteppers[2].prevStep();
			}
		});

		mNextBtn2 = view.findViewById(R.id.button_next_2);
		mNextBtn2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Snackbar.make(view, "Finish!", Snackbar.LENGTH_LONG).show();
			}
		});

		view.findViewById(R.id.btn_change_point_color).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mActivatedColorRes == R.color.material_blue_500) {
					mActivatedColorRes = R.color.material_deep_purple_500;
				} else {
					mActivatedColorRes = R.color.material_blue_500;
				}
				for (VerticalStepperItemView stepper : mSteppers) {
					stepper.setActivatedColorResource(mActivatedColorRes);
				}
			}
		});
		view.findViewById(R.id.btn_change_done_icon).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mDoneIconRes == R.drawable.ic_done_white_16dp) {
					mDoneIconRes = R.drawable.ic_save_white_16dp;
				} else {
					mDoneIconRes = R.drawable.ic_done_white_16dp;
				}
				for (VerticalStepperItemView stepper : mSteppers) {
					stepper.setDoneIconResource(mDoneIconRes);
				}
			}
		});
	}

}
