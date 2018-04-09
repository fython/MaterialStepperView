package moe.feng.common.stepperview;

import android.animation.LayoutTransition;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.*;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.*;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class VerticalStepperItemView extends FrameLayout {

	/**
	 * Internal Views
	 */
	private View mPointBackground, mLineView;
	private TextView mPointNumber;
	private TextView mTitleText, mSummaryText;
	private FrameLayout mCustomView, mPointFrame;
	private LinearLayout mRightContainer;
	private ImageView mDoneIconView, mErrorIconView;
	private View mMarginBottomView;

	private ValueAnimator mTitleColorAnimator, mSummaryColorAnimator, mPointColorAnimator;
	private ViewPropertyAnimator mPointAnimator, mErrorIconAnimator;

	/**
	 * Step state
	 */
	private CharSequence mTitle;
	private @Nullable CharSequence mSummary, mSummaryFinished = null;
	private int mIndex = 1;
	private boolean isLastStep = false;
	private int mState = STATE_NORMAL;
	private CharSequence mErrorText = null; // If null means no error

	/**
	 * View attributes
	 */
	private int mAnimationDuration;
	private int mNormalColor, mActivatedColor, mLineColor, mErrorColor;
	private Drawable mDoneIcon;
	private boolean mAnimationEnabled = true;
	private boolean mAlwaysShowSummary = false;

	/**
	 * The bind views
	 */
	private @Nullable VerticalStepperItemView mPrevItemView, mNextItemView;

	public static final int STATE_NORMAL = 0, STATE_SELECTED = 1, STATE_DONE = 2;

	private final int DP;

	public VerticalStepperItemView(@NonNull Context context) {
		this(context, null);
	}

	public VerticalStepperItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public VerticalStepperItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		prepareViews(context);

		DP = getResources().getDimensionPixelSize(R.dimen.dp1);

		if (attrs != null) {
			TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.VerticalStepperItemView,
					defStyleAttr, R.style.Widget_Stepper);

			mTitle = a.getString(R.styleable.VerticalStepperItemView_step_title);
			mSummary = a.getString(R.styleable.VerticalStepperItemView_step_summary);
			mSummaryFinished = a.getString(R.styleable.VerticalStepperItemView_step_summary_done);
			mIndex = a.getInt(R.styleable.VerticalStepperItemView_step_index, 1);
			mState = a.getInt(R.styleable.VerticalStepperItemView_step_state, STATE_NORMAL);
			isLastStep = a.getBoolean(R.styleable.VerticalStepperItemView_step_is_last, false);
			mNormalColor = a.getColor(R.styleable.VerticalStepperItemView_step_normal_color, mNormalColor);
			mActivatedColor = a.getColor(R.styleable.VerticalStepperItemView_step_activated_color, mActivatedColor);
			mAnimationDuration = a.getInt(R.styleable.VerticalStepperItemView_step_animation_duration, mAnimationDuration);
			mAnimationEnabled = a.getBoolean(R.styleable.VerticalStepperItemView_step_enable_animation, true);
			mLineColor = a.getColor(R.styleable.VerticalStepperItemView_step_line_color, mLineColor);
			mErrorColor = a.getColor(R.styleable.VerticalStepperItemView_step_error_highlight_color, mErrorColor);
			mAlwaysShowSummary = a.getBoolean(R.styleable.VerticalStepperItemView_step_show_summary_always, mAlwaysShowSummary);

			if (a.hasValue(R.styleable.VerticalStepperItemView_step_done_icon)) {
				mDoneIcon = a.getDrawable(R.styleable.VerticalStepperItemView_step_done_icon);
			}

			a.recycle();
		}

		setTitle(mTitle);
		updateSummaryView();
		setIndex(mIndex);
		setState(mState);
		setIsLastStep(isLastStep);
		setDoneIcon(mDoneIcon);
		setAnimationEnabled(mAnimationEnabled);
		setLineColor(mLineColor);
		setErrorColor(mErrorColor);
	}

	@Override
	public void addView(View child, int index, ViewGroup.LayoutParams layoutParams) {
		if (child.getId() == R.id.vertical_stepper_item_view_layout) {
			super.addView(child, index, layoutParams);
		} else {
			mCustomView.addView(child, index, layoutParams);
		}
	}

	FrameLayout getCustomView() {
		return mCustomView;
	}

	/**
	 * Remove custom view manually
	 */
	public void removeCustomView() {
		mCustomView.removeAllViews();
	}

	private void prepareViews(@NonNull Context context) {
		// Inflate and find views
		LayoutInflater inflater = LayoutInflater.from(context);
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
		mErrorIconView = inflateView.findViewById(R.id.stepper_error_icon);

		// Add view
		LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		addView(inflateView, lp);

		// Set title top margin
		mTitleText.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				int singleLineHeight = mTitleText.getMeasuredHeight();
				int topMargin = (mPointFrame.getMeasuredHeight() - singleLineHeight) / 2;
				// Only update top margin when it is positive, preventing titles being truncated.
				if (topMargin > 0) {
					ViewGroup.MarginLayoutParams mlp = (MarginLayoutParams) mTitleText.getLayoutParams();
					mlp.topMargin = topMargin;
				}
			}
		});
	}

	private void updateMarginBottom() {
		mMarginBottomView.getLayoutParams().height = (!isLastStep ? (mState != STATE_SELECTED ? 28 : 36) : 12) * DP;
	}

	/**
	 * Set up the state of this stepper item
	 *
	 * @param state The state of this stepper item
	 */
	public synchronized void setState(@State int state) {
		// Change point background
		if (mPointColorAnimator != null) mPointColorAnimator.cancel();
		if (state != STATE_NORMAL && mState == STATE_NORMAL) {
			mPointColorAnimator = ViewUtils.createArgbAnimator(
					mPointBackground, "backgroundColor", mNormalColor, mActivatedColor);
			mPointColorAnimator.setDuration(mAnimationDuration);
			mPointColorAnimator.start();
		} else if (state == STATE_NORMAL && mState != STATE_NORMAL) {
			mPointColorAnimator = ViewUtils.createArgbAnimator(
					mPointBackground, "backgroundColor", mActivatedColor, mNormalColor);
			mPointColorAnimator.setDuration(mAnimationDuration);
			mPointColorAnimator.start();
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

		// Set title style
		int lastTitleTextColor = mTitleText.getCurrentTextColor();
		if (mTitleColorAnimator != null) mTitleColorAnimator.cancel();
		mTitleText.setTextAppearance(getContext(), state == STATE_DONE ?
				R.style.TextAppearance_Widget_Stepper_Done : (
						state == STATE_NORMAL ?
								R.style.TextAppearance_Widget_Stepper_Normal :
								R.style.TextAppearance_Widget_Stepper_Selected
				));

		// Update error state
		if (mErrorText != null) {
			mTitleColorAnimator = ViewUtils.createArgbAnimator(
					mTitleText, "textColor", lastTitleTextColor, mErrorColor);
			mTitleColorAnimator.setDuration(mAnimationDuration);
			mTitleColorAnimator.start();
			if (mSummaryColorAnimator != null) mSummaryColorAnimator.cancel();
			mSummaryColorAnimator = ViewUtils.createArgbAnimator(
					mSummaryText, "textColor", mSummaryText.getCurrentTextColor(), mErrorColor);
			mSummaryColorAnimator.setDuration(mAnimationDuration);
			mSummaryColorAnimator.start();

			if (mErrorIconView.getAlpha() < 1F) {
				if (mPointAnimator != null) mPointAnimator.cancel();
				mPointAnimator = mPointFrame.animate().alpha(0F).setDuration(mAnimationDuration);
				mPointAnimator.start();
				mErrorIconView.setScaleX(0.6F);
				mErrorIconView.setScaleY(0.6F);
				if (mErrorIconAnimator != null) mErrorIconAnimator.cancel();
				mErrorIconAnimator = mErrorIconView.animate().scaleX(1F).scaleY(1F)
						.alpha(1F).setDuration(mAnimationDuration).setInterpolator(new OvershootInterpolator());
				mErrorIconAnimator.start();
			}
		} else {
			if (mSummaryColorAnimator != null) mSummaryColorAnimator.cancel();
			mSummaryColorAnimator = ViewUtils.createArgbAnimator(
					mSummaryText, "textColor", mSummaryText.getCurrentTextColor(), mLineColor);
			mSummaryColorAnimator.setDuration(mAnimationDuration);
			mSummaryColorAnimator.start();

			if (mPointFrame.getAlpha() < 1F) {
				mPointFrame.setScaleX(0.6F);
				mPointFrame.setScaleY(0.6F);
				if (mPointAnimator != null) mPointAnimator.cancel();
				mPointAnimator = mPointFrame.animate().scaleX(1F).scaleY(1F).alpha(1F).setDuration(mAnimationDuration);
				mPointAnimator.start();
				if (mErrorIconAnimator != null) mErrorIconAnimator.cancel();
				mErrorIconAnimator = mErrorIconView.animate().alpha(0F).setDuration(mAnimationDuration);
				mErrorIconAnimator.start();
			}
		}

		// Set the visibility of views
		mSummaryText.setVisibility(state != STATE_SELECTED && !TextUtils.isEmpty(mSummary) ? View.VISIBLE : View.GONE);
		mCustomView.setVisibility(state == STATE_SELECTED ? View.VISIBLE : View.GONE);

		mState = state;

		updateMarginBottom();
		updateSummaryView();
	}

	/**
	 * Get the state of this stepper item
	 *
	 * @return The state of this stepper item
	 */
	public @State int getState() {
		return mState;
	}

	/**
	 * Set title for this step
	 *
	 * @param title The title should be set
	 */
	public void setTitle(CharSequence title) {
		mTitle = title;
		mTitleText.setText(title);
	}

	/**
	 * Set title for this step
	 *
	 * @param titleRes The title resource should be set
	 */
	public void setTitle(@StringRes int titleRes) {
		setTitle(getResources().getString(titleRes));
	}

	/**
	 * Get the title of this step
	 *
	 * @return The title of this step
	 */
	public @NonNull CharSequence getTitle() {
		return mTitle;
	}

    /**
     * Set should show summary always.
     *
     * @param alwaysShowSummary new value
     */
    public void setAlwaysShowSummary(boolean alwaysShowSummary) {
        mAlwaysShowSummary = alwaysShowSummary;
        updateSummaryView();
    }

    /**
     * Should show summary always
     *
     * @return If should show summary always
     */
    public boolean isAlwaysShowSummary() {
        return mAlwaysShowSummary;
    }

    /**
	 * Set error text for this step. If you want to remove error text, the param should be null.
	 *
	 * @param errorText The error text should be set or zero for removing error text
	 */
	public void setErrorText(@Nullable CharSequence errorText) {
		mErrorText = errorText;
		mSummaryText.setText(mErrorText != null ? mErrorText : mSummary);
		setState(mState);
	}

	/**
	 * Set error text for this step. If you want to remove error text, the param should be zero value.
	 *
	 * @param errorTextRes The title resource should be set
	 */
	public void setErrorText(@StringRes int errorTextRes) {
		if (errorTextRes != 0) {
			setErrorText(getResources().getString(errorTextRes));
		} else {
			setErrorText(null);
		}
	}

	/**
	 * Get the title of this step
	 *
	 * @return The title of this step
	 */
	public @Nullable CharSequence getErrorText() {
		return mErrorText;
	}

	/**
	 * Set summary for this step.
	 * If you set a null value, it will hide the summary view.
	 *
	 * @param summary The summary should be set or null
	 */
	public void setSummary(@Nullable CharSequence summary) {
		mSummary = summary;
		updateSummaryView();
	}

	/**
	 * Set summary for this step.
	 *
	 * @param summaryRes The summary resource should be set
	 */
	public void setSummary(@StringRes int summaryRes) {
		setSummary(getResources().getString(summaryRes));
	}

	/**
	 * Get the summary of this step
	 *
	 * @return The summary of this step
	 */
	public @Nullable CharSequence getSummary() {
		return mSummary;
	}

	/**
	 * Set finished summary for this step.
	 * If you set a null value, it will hide the summary view or show default summary.
	 *
	 * @param summary The summary should be set or null
	 */
	public void setSummaryFinished(@Nullable CharSequence summary) {
		mSummaryFinished = summary;
		updateSummaryView();
	}

	/**
	 * Set summary for this step.
	 *
	 * @param summaryRes The summary resource should be set
	 */
	public void setSummaryFinished(@StringRes int summaryRes) {
		setSummaryFinished(getResources().getString(summaryRes));
	}

	/**
	 * Get the summary of this step
	 *
	 * @return The summary of this step
	 */
	public @Nullable CharSequence getSummaryFinished() {
		return mSummaryFinished;
	}

	/**
	 * Update summary view
	 */
	private void updateSummaryView() {
		mSummaryText.setText(
				mErrorText != null ? mErrorText
						: (mSummaryFinished != null && mState == STATE_DONE) ? mSummaryFinished : mSummary
		);
		mSummaryText.setVisibility(
		        (mState != STATE_SELECTED || mAlwaysShowSummary) && !TextUtils.isEmpty(mSummaryText.getText()) ?
                        View.VISIBLE : View.GONE
        );
	}

	/**
	 * Set index for this step
	 *
	 * @param index Index
	 */
	public void setIndex(int index) {
		mIndex = index;
		mPointNumber.setText(String.valueOf(index));
	}

	/**
	 * Get the index of this step
	 *
	 * @return The index of this step
	 */
	public int getIndex() {
		return mIndex;
	}

	/**
	 * Set if it is last step
	 *
	 * @param isLastStep If it is last step
	 */
	public void setIsLastStep(boolean isLastStep) {
		this.isLastStep = isLastStep;
		mLineView.setVisibility(isLastStep ? View.INVISIBLE : View.VISIBLE);
		updateMarginBottom();
	}

	/**
	 * Return if it is last step
	 *
	 * @return If it is last step
	 */
	public boolean isLastStep() {
		return isLastStep;
	}

	/**
	 * Set if animation should be enabled
	 *
	 * @param shouldAnimate If animation should be enabled
	 */
	public void setAnimationEnabled(boolean shouldAnimate) {
		mAnimationEnabled = shouldAnimate;
		if (shouldAnimate) {
			mRightContainer.setLayoutTransition(new LayoutTransition());
		} else {
			mRightContainer.setLayoutTransition(null);
		}
	}

	/**
	 * Get if animation is enabled
	 *
	 * @return If animation is enabled
	 */
	public boolean isAnimationEnabled() {
		return mAnimationEnabled;
	}

	/**
	 * Set done icon drawable
	 *
	 * @param drawable Done icon drawable
	 */
	public void setDoneIcon(@NonNull Drawable drawable) {
		mDoneIcon = drawable;
		mDoneIconView.setImageDrawable(drawable);
	}

	/**
	 * Set done icon drawable resource
	 *
	 * @param drawableRes Done icon drawable resource
	 */
	public void setDoneIconResource(@DrawableRes int drawableRes) {
		setDoneIcon(getResources().getDrawable(drawableRes));
	}

	/**
	 * Get done icon drawable
	 *
	 * @return Done icon drawable
	 */
	public @NonNull Drawable getDoneIcon() {
		return mDoneIcon;
	}

	/**
	 * Set animation duration
	 *
	 * @param duration Animation Duration
	 */
	public void setAnimationDuration(int duration) {
		mAnimationDuration = duration;
	}

	/**
	 * Get animation duration
	 *
	 * @return Animation Duration
	 */
	public int getAnimationDuration() {
		return mAnimationDuration;
	}

	/**
	 * Bind two stepper items for automatically setting state with nextStep() or prevStep()
	 *
	 * @param prevItem The previous item
	 * @param nextItem The next item
	 */
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

	/**
	 * Bind stepper items for automatically setting state with nextStep() or prevStep()
	 *
	 * @param items Stepper items
	 */
	public static void bindSteppers(@NonNull VerticalStepperItemView... items) {
		for (int i = 0; i < items.length - 1; i++) {
			if (i != 0) {
				items[i].bindSteppers(items[i - 1], null);
			}
			items[i].bindSteppers(null, items[i + 1]);
		}
	}

	/**
	 * Return if stepper can go previous
	 *
	 * @return If stepper can go previous
	 */
	public boolean canPrevStep() {
		return mPrevItemView != null;
	}

	/**
	 * Go previous step
	 *
	 * @return If success
	 */
	public boolean prevStep() {
		if (canPrevStep()) {
			setState(STATE_NORMAL);
            if (mPrevItemView != null) {
                mPrevItemView.setState(STATE_SELECTED);
            }
            return true;
		}
		return false;
	}

	/**
	 * Return if stepper can go next
	 *
	 * @return If stepper can go next
	 */
	public boolean canNextStep() {
		return mNextItemView != null;
	}

	/**
	 * Go next step
	 *
	 * @return If success
	 */
	public boolean nextStep() {
		if (canNextStep()) {
			setState(STATE_DONE);
            if (mNextItemView != null) {
                mNextItemView.setState(STATE_SELECTED);
            }
            return true;
		}
		return false;
	}

	/**
	 * Set normal point color
	 *
	 * @param color Normal Point Color
	 */
	public void setNormalColor(@ColorInt int color) {
		mNormalColor = color;
		if (mState == STATE_NORMAL) {
			mPointBackground.setBackgroundColor(color);
		}
	}

	/**
	 * Set normal point color
	 *
	 * @param colorRes Normal Point Color resource
	 */
	public void setNormalColorResource(@ColorRes int colorRes) {
		setNormalColor(getResources().getColor(colorRes));
	}

	/**
	 * Get normal point color
	 *
	 * @return Normal Point Color
	 */
	public @ColorInt int getNormalColor() {
		return mNormalColor;
	}

	/**
	 * Set activated point color
	 *
	 * @param color Activated Point Color
	 */
	public void setActivatedColor(@ColorInt int color) {
		mActivatedColor = color;
		if (mState != STATE_NORMAL) {
			mPointBackground.setBackgroundColor(color);
		}
	}

	/**
	 * Set activated point color
	 *
	 * @param colorRes Activated Point Color resource
	 */
	public void setActivatedColorResource(@ColorRes int colorRes) {
		setActivatedColor(getResources().getColor(colorRes));
	}

	/**
	 * Get line color
	 *
	 * @return Line Color
	 */
	public @ColorInt int getLineColor() {
		return mLineColor;
	}

	/**
	 * Set line color
	 *
	 * @param color Line Color
	 */
	public void setLineColor(@ColorInt int color) {
		mLineColor = color;
		mLineView.setBackgroundColor(color);
	}

	/**
	 * Set line color
	 *
	 * @param colorRes Line Color resource
	 */
	public void setLineColorResource(@ColorRes int colorRes) {
		setLineColor(getResources().getColor(colorRes));
	}

	/**
	 * Get error highlight color
	 *
	 * @return Error Highlight Color
	 */
	public @ColorInt int getErrorColor() {
		return mErrorColor;
	}

	/**
	 * Set error highlight color
	 *
	 * @param color Error Highlight Color
	 */
	public void setErrorColor(@ColorInt int color) {
		if (isPreLollipop()) {
			mErrorIconView.getDrawable().setColorFilter(color, PorterDuff.Mode.DST_IN);
		} else {
			mErrorIconView.getDrawable().setTint(color);
		}
		if (mErrorText != null && color != mErrorColor) {
			if (mTitleColorAnimator != null && mTitleColorAnimator.isRunning()) mTitleColorAnimator.cancel();
			if (mSummaryColorAnimator != null && mSummaryColorAnimator.isRunning()) mSummaryColorAnimator.cancel();
			mTitleText.setTextColor(color);
			mSummaryText.setTextColor(color);
		}
		mErrorColor = color;
	}

	/**
	 * Set error highlight color
	 *
	 * @param colorRes Error Highlight Color resource
	 */
	public void setErrorColorResource(@ColorRes int colorRes) {
		setErrorColor(getResources().getColor(colorRes));
	}

	/**
	 * Get activated point color
	 *
	 * @return Activated Point Color
	 */
	public @ColorInt int getActivatedColor() {
		return mActivatedColor;
	}

	@IntDef({STATE_NORMAL, STATE_SELECTED, STATE_DONE})
	@Retention(RetentionPolicy.SOURCE)
	public @interface State {}

	// Save/Restore View Instance State
	@Override
	public Parcelable onSaveInstanceState() {
		ItemViewState state = new ItemViewState(super.onSaveInstanceState());
		state.title = mTitle;
		state.summary = mSummary;
		state.summaryFinished = mSummaryFinished;
		state.index = mIndex;
		state.isLastStep = isLastStep;
		state.state = mState;
		state.errorText = mErrorText;
		return state;
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		if (state instanceof ItemViewState) {
			ItemViewState viewState = (ItemViewState) state;
			super.onRestoreInstanceState(viewState.getSuperState());
			setTitle(viewState.title);
			setSummary(viewState.summary);
			setSummaryFinished(viewState.summaryFinished);
			setIndex(viewState.index);
			setIsLastStep(viewState.isLastStep);
			setState(viewState.state);
			setErrorText(viewState.errorText);
			return;
		}
		super.onRestoreInstanceState(BaseSavedState.EMPTY_STATE);
	}

	protected static class ItemViewState extends BaseSavedState {

		CharSequence title, summary, summaryFinished;
		int index = 1;
		boolean isLastStep = false;
		int state = STATE_NORMAL;
		CharSequence errorText;

		ItemViewState(Parcelable superState) {
			super(superState);
		}

		private ItemViewState(Parcel in) {
		    super(in);
		    title = in.readString();
		    summary = in.readString();
		    summaryFinished = in.readString();
		    index = in.readInt();
		    isLastStep = in.readByte() != 0;
		    state = in.readInt();
		    errorText = in.readString();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
		    super.writeToParcel(out, flags);
		    out.writeString(title != null ? title.toString() : null);
            out.writeString(summary != null ? summary.toString() : null);
            out.writeString(summaryFinished != null ? summaryFinished.toString() : null);
            out.writeInt(index);
            out.writeByte(isLastStep ? (byte) 1 : (byte) 0);
            out.writeInt(state);
            out.writeString(errorText != null ? errorText.toString() : null);
        }

        public static final Parcelable.Creator<ItemViewState> CREATOR =
                new Parcelable.Creator<ItemViewState>() {
                    @Override
                    public ItemViewState createFromParcel(Parcel source) {
                        return new ItemViewState(source);
                    }

                    @Override
                    public ItemViewState[] newArray(int size) {
                        return new ItemViewState[size];
                    }
		        };

	}

	private static boolean isPreLollipop() {
		return Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP;
	}

}