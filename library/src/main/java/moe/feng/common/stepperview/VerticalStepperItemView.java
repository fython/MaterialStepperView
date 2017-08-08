package moe.feng.common.stepperview;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.*;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class VerticalStepperItemView extends FrameLayout {

	private View mPointBackground, mLineView;
	private TextView mPointNumber;
	private TextView mTitleText, mSummaryText;
	private FrameLayout mCustomView, mPointFrame;
	private LinearLayout mRightContainer;
	private ImageView mDoneIconView;
	private View mMarginBottomView;

	private String mTitle, mSummary;
	private int mIndex = 1;
	private boolean isLastStep = false;
	private int mState = STATE_NORMAL;

	private int mAnimationDuration;
	private int mNormalColor, mActivatedColor;
	private Drawable mDoneIcon;

	private @Nullable VerticalStepperItemView mPrevItemView, mNextItemView;

	public static final int STATE_NORMAL = 0, STATE_SELECTED = 1, STATE_DONE = 2;

	private final int DP;

	public VerticalStepperItemView(Context context) {
		this(context, null);
	}

	public VerticalStepperItemView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public VerticalStepperItemView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		prepareViews(context);

		mNormalColor = getResources().getColor(R.color.material_grey_500);
		mActivatedColor = ViewUtils.getColorFromAttr(context, R.attr.colorPrimary);
		mDoneIcon = getResources().getDrawable(R.drawable.ic_done_white_16dp);
		mAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
		DP = getResources().getDimensionPixelSize(R.dimen.dp1);

		if (attrs != null) {
			TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.VerticalStepperItemView, defStyleAttr, 0);

			mTitle = a.getString(R.styleable.VerticalStepperItemView_step_title);
			mSummary = a.getString(R.styleable.VerticalStepperItemView_step_summary);
			mIndex = a.getInt(R.styleable.VerticalStepperItemView_step_index, 1);
			mState = a.getInt(R.styleable.VerticalStepperItemView_step_state, STATE_NORMAL);
			isLastStep = a.getBoolean(R.styleable.VerticalStepperItemView_step_is_last, false);
			mNormalColor = a.getColor(R.styleable.VerticalStepperItemView_step_normal_color, mNormalColor);
			mActivatedColor = a.getColor(R.styleable.VerticalStepperItemView_step_activated_color, mActivatedColor);
			mAnimationDuration = a.getInt(R.styleable.VerticalStepperItemView_step_animation_duration, mAnimationDuration);

			if (a.hasValue(R.styleable.VerticalStepperItemView_step_done_icon)) {
				mDoneIcon = a.getDrawable(R.styleable.VerticalStepperItemView_step_done_icon);
			}

			a.recycle();
		}

		setTitle(mTitle);
		setSummary(mSummary);
		setIndex(mIndex);
		setState(mState);
		setIsLastStep(isLastStep);
		setDoneIcon(mDoneIcon);
	}

	@Override
	public void addView(View child, int index, ViewGroup.LayoutParams layoutParams) {
		if (child.getId() == R.id.vertical_stepper_item_view_layout) {
			super.addView(child, index, layoutParams);
		} else {
			mCustomView.addView(child, index, layoutParams);
		}
	}

	public void removeCustomView() {
		mCustomView.removeAllViews();
	}

	private void prepareViews(Context context) {
		// Inflate and find views
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View inflateView = inflater.inflate(R.layout.vertical_stepper_item_view_layout, null);
		mPointBackground = inflateView.findViewById(R.id.stepper_point_background);
		mLineView = inflateView.findViewById(R.id.stepper_line);
		mPointNumber = inflateView.findViewById(R.id.stepper_number);
		mTitleText = inflateView.findViewById(R.id.stepper_title);
		mSummaryText = inflateView.findViewById(R.id.stepper_summary);
		mCustomView = inflateView.findViewById(R.id.stepper_custom_view);
		mPointFrame = inflateView.findViewById(R.id.stepper_point_frame);
		mRightContainer = inflateView.findViewById(R.id.stepper_right_layout);
		mDoneIconView = inflateView.findViewById(R.id.stepper_done_icon);
		mMarginBottomView = inflateView.findViewById(R.id.stepper_margin_bottom);

		// Add view
		LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		addView(inflateView, lp);

		// Set title top margin
		mTitleText.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				int singleLineHeight = mTitleText.getMeasuredHeight();
				ViewGroup.MarginLayoutParams mlp = (MarginLayoutParams) mTitleText.getLayoutParams();
				mlp.topMargin = (mPointFrame.getMeasuredHeight() - singleLineHeight) / 2;
			}
		});
	}

	private void updateMarginBottom() {
		mMarginBottomView.getLayoutParams().height = (!isLastStep ? (mState != STATE_SELECTED ? 28 : 36) : 0) * DP;
	}

	public void setState(@State int state) {
		// Change point background
		if (state != STATE_NORMAL && mState == STATE_NORMAL) {
			ValueAnimator animator = ObjectAnimator
					.ofArgb(mPointBackground, "backgroundColor", mNormalColor, mActivatedColor);
			animator.setDuration(mAnimationDuration);
			animator.start();
		} else if (state == STATE_NORMAL && mState != STATE_NORMAL) {
			ValueAnimator animator = ObjectAnimator
					.ofArgb(mPointBackground, "backgroundColor", mActivatedColor, mNormalColor);
			animator.setDuration(mAnimationDuration);
			animator.start();
		} else {
			mPointBackground.setBackgroundColor(state == STATE_NORMAL ? mNormalColor : mActivatedColor);
		}

		// Change point state
		if (state == STATE_DONE && mState != STATE_DONE) {
			mDoneIconView.animate().alpha(1f).setDuration(mAnimationDuration).start();
			mPointNumber.animate().alpha(0f).setDuration(mAnimationDuration).start();
		} else if (state != STATE_DONE && mState == STATE_DONE) {
			mDoneIconView.animate().alpha(0f).setDuration(mAnimationDuration).start();
			mPointNumber.animate().alpha(1f).setDuration(mAnimationDuration).start();
		} else {
			mDoneIconView.setAlpha(state == STATE_DONE ? 1f : 0f);
			mPointNumber.setAlpha(state == STATE_DONE ? 0f : 1f);
		}

		// Set the visibility of views
		mSummaryText.setVisibility(state != STATE_SELECTED && !TextUtils.isEmpty(mSummary) ? View.VISIBLE : View.GONE);
		mCustomView.setVisibility(state == STATE_SELECTED ? View.VISIBLE : View.GONE);

		mState = state;

		updateMarginBottom();
	}

	public @State int getState() {
		return mState;
	}

	public void setTitle(String title) {
		mTitle = title;
		mTitleText.setText(title);
	}

	public void setTitle(@StringRes int titleRes) {
		setTitle(getResources().getString(titleRes));
	}

	public String getTitle() {
		return mTitle;
	}

	public void setSummary(String summary) {
		mSummary = summary;
		mSummaryText.setText(summary);
		mSummaryText.setVisibility(mState != STATE_SELECTED && !TextUtils.isEmpty(mSummary) ? View.VISIBLE : View.GONE);
	}

	public void setSummary(@StringRes int summaryRes) {
		setSummary(getResources().getString(summaryRes));
	}

	public String getSummary() {
		return mSummary;
	}

	public void setIndex(int index) {
		mIndex = index;
		mPointNumber.setText(String.valueOf(index));
	}

	public int getIndex() {
		return mIndex;
	}

	public void setIsLastStep(boolean isLastStep) {
		this.isLastStep = isLastStep;
		mLineView.setVisibility(isLastStep ? View.INVISIBLE : View.VISIBLE);
		updateMarginBottom();
	}

	public boolean isLastStep() {
		return isLastStep;
	}

	public void setShouldAnimateWhenCustomViewShowHide(boolean shouldAnimate) {
		if (shouldAnimate) {
			mRightContainer.setLayoutTransition(new LayoutTransition());
		} else {
			mRightContainer.setLayoutTransition(null);
		}
	}

	public void setDoneIcon(Drawable drawable) {
		mDoneIcon = drawable;
		mDoneIconView.setImageDrawable(drawable);
	}

	public void setDoneIconResource(@DrawableRes int drawableRes) {
		setDoneIcon(getResources().getDrawable(drawableRes));
	}

	public Drawable getDoneIcon() {
		return mDoneIcon;
	}

	public void setAnimationDuration(int duration) {
		mAnimationDuration = duration;
	}

	public int getAnimationDuration() {
		return mAnimationDuration;
	}

	public void bindSteppers(@Nullable VerticalStepperItemView prevItem, @Nullable VerticalStepperItemView nextItem) {
		if (prevItem != null) {
			mPrevItemView = prevItem;
			if (mPrevItemView.mNextItemView != this) {
				mPrevItemView.bindSteppers(null, this);
			}
		}
		if (nextItem != null) {
			mNextItemView = nextItem;
			if (mNextItemView.mPrevItemView != this) {
				mNextItemView.bindSteppers(this, null);
			}
		}
	}

	public static void bindSteppers(@NonNull VerticalStepperItemView... items) {
		for (int i = 0; i < items.length - 1; i++) {
			if (i != 0) {
				items[i].bindSteppers(items[i - 1], null);
			}
			items[i].bindSteppers(null, items[i + 1]);
		}
	}

	public boolean canPrevStep() {
		return mPrevItemView != null;
	}

	public boolean prevStep() {
		if (canPrevStep()) {
			setState(STATE_NORMAL);
			mPrevItemView.setState(STATE_SELECTED);
			return true;
		}
		return false;
	}

	public boolean canNextStep() {
		return mNextItemView != null;
	}

	public boolean nextStep() {
		if (canNextStep()) {
			setState(STATE_DONE);
			mNextItemView.setState(STATE_SELECTED);
			return true;
		}
		return false;
	}

	public void setNormalColor(@ColorInt int color) {
		mNormalColor = color;
		if (mState == STATE_NORMAL) {
			mPointBackground.setBackgroundColor(color);
		}
	}

	public void setNormalColorResource(@ColorRes int colorRes) {
		setNormalColor(getResources().getColor(colorRes));
	}

	public @ColorInt int getNormalColor() {
		return mNormalColor;
	}

	public void setActivatedColor(@ColorInt int color) {
		mActivatedColor = color;
		if (mState != STATE_NORMAL) {
			mPointBackground.setBackgroundColor(color);
		}
	}

	public void setActivatedColorResource(@ColorRes int colorRes) {
		setActivatedColor(getResources().getColor(colorRes));
	}

	public @ColorInt int getActivatedColor() {
		return mActivatedColor;
	}

	@IntDef({STATE_NORMAL, STATE_SELECTED, STATE_DONE})
	@Retention(RetentionPolicy.SOURCE)
	public @interface State {}

	// Save/Restore View Instance State
	@Override
	public Parcelable onSaveInstanceState() {
		Bundle bundle = new Bundle();
		ItemViewState state = new ItemViewState(super.onSaveInstanceState());
		state.title = mTitle;
		state.summary = mSummary;
		state.index = mIndex;
		state.isLastStep = isLastStep;
		state.state = mState;
		state.animationDuration = mAnimationDuration;
		state.normalColor = mNormalColor;
		state.activatedColor = mActivatedColor;
		state.doneIcon = mDoneIcon;
		bundle.putParcelable(ItemViewState.STATE, state);
		return bundle;
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		if (state instanceof Bundle) {
			Bundle bundle = (Bundle) state;
			ItemViewState viewState = bundle.getParcelable(ItemViewState.STATE);
			super.onRestoreInstanceState(viewState.getSuperState());
			setTitle(viewState.title);
			setSummary(viewState.summary);
			setIndex(viewState.index);
			setIsLastStep(viewState.isLastStep);
			setState(viewState.state);
			setAnimationDuration(viewState.animationDuration);
			setNormalColor(viewState.normalColor);
			setActivatedColor(viewState.activatedColor);
			setDoneIcon(viewState.doneIcon);
			return;
		}
		super.onRestoreInstanceState(BaseSavedState.EMPTY_STATE);
	}

	protected static class ItemViewState extends BaseSavedState {

		private static final String STATE = VerticalStepperItemView.class.getSimpleName() + ".STATE";

		String title, summary;
		int index = 1;
		boolean isLastStep = false;
		int state = STATE_NORMAL;

		int animationDuration;
		int normalColor, activatedColor;
		Drawable doneIcon;

		ItemViewState(Parcelable superState) {
			super(superState);
		}

	}

}