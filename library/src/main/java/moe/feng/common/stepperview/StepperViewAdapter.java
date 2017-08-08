package moe.feng.common.stepperview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public abstract class StepperViewAdapter implements IStepperViewAdapter {

	private View mViews[];

	public StepperViewAdapter() {

	}

	public StepperViewAdapter(View[] views) {
		mViews = views;
	}

	public void setViews(View[] views) {
		mViews = views;
	}

	public View getView(int index) {
		return mViews[index];
	}

	@Override
	public String getTitle(int index) {
		return "Step " + index;
	}

	@Override
	public int size() {
		return mViews.length;
	}

	@Override
	public void onCreateView(int index, Context context, ViewGroup parent) {
		parent.addView(mViews[index]);
	}

}
