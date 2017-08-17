package moe.feng.common.stepperview;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.view.View;

class ViewUtils {

	/**
	 * Get color attribute from current theme
	 *
	 * @param context Themed context
	 * @param attr The resource id of color attribute
	 * @return Result
	 */
	@ColorInt
	static int getColorFromAttr(Context context, @AttrRes int attr) {
		TypedArray array = context.getTheme().obtainStyledAttributes(new int[]{attr});
		int color = array.getColor(0, Color.TRANSPARENT);
		array.recycle();
		return color;
	}

	static ObjectAnimator createArgbAnimator(View view, String propertyName, int startColor, int endColor) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			return ObjectAnimator.ofObject(view, propertyName, new TypeEvaluator() {
				@Override
				public Object evaluate(float fraction, Object startValue, Object endValue) {
					int startInt = (Integer) startValue;
					int startA = (startInt >> 24) & 0xff;
					int startR = (startInt >> 16) & 0xff;
					int startG = (startInt >> 8) & 0xff;
					int startB = startInt & 0xff;

					int endInt = (Integer) endValue;
					int endA = (endInt >> 24) & 0xff;
					int endR = (endInt >> 16) & 0xff;
					int endG = (endInt >> 8) & 0xff;
					int endB = endInt & 0xff;

					return (startA + (int)(fraction * (endA - startA))) << 24 |
							(startR + (int)(fraction * (endR - startR))) << 16 |
							(startG + (int)(fraction * (endG - startG))) << 8 |
							(startB + (int)(fraction * (endB - startB)));
				}
			}, startColor, endColor);
		} else {
			return ObjectAnimator.ofArgb(view, propertyName, startColor, endColor);
		}
	}

}
