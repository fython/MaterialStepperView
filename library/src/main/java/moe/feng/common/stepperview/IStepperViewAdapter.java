package moe.feng.common.stepperview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public interface IStepperViewAdapter {

	String getTitle(int index);

	String getSummary(int index);

	int size();

	View onCreateCustomView(int index, Context context, VerticalStepperItemView parent);

	void onShow(int index);

	void onHide(int index);

}
