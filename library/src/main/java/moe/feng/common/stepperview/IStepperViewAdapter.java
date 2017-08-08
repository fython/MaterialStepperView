package moe.feng.common.stepperview;

import android.content.Context;
import android.view.ViewGroup;

interface IStepperViewAdapter {

	String getTitle(int index);

	int size();

	void onCreateView(int index, Context context, ViewGroup parent);

	void onShow(int index);

	void onHide(int index);

}
