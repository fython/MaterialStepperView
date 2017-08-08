package moe.feng.common.stepperview;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

class VerticalStepperAdapter extends BaseAdapter {

	private IStepperView mStepperView;
	private int mCurrentStep = 0;

	public VerticalStepperAdapter(IStepperView stepperView) {
		mStepperView = stepperView;
	}

	public void setCurrentStep(int currentStep) {
		mCurrentStep = currentStep;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mStepperView.getViewAdapter() == null ? 0 : mStepperView.getViewAdapter().size();
	}

	@Override
	public Object getItem(int i) {
		return null;
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		if (view == null) {
			view = new VerticalStepperItemView(viewGroup.getContext());
		}

		return view;
	}

}
