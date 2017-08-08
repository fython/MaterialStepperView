package moe.feng.common.stepperview;

import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;

interface IStepperView {

	IStepperViewAdapter getViewAdapter();
	int getCurrentStep();

	@ColorInt int getNormalColor();
	@ColorInt int getActivatedColor();
	int getAnimationDuration();
	Drawable getDoneIcon();

}
