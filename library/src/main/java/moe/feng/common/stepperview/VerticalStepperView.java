package moe.feng.common.stepperview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import moe.feng.common.stepperview.internal.VerticalSpaceItemDecoration;

public class VerticalStepperView extends FrameLayout implements IStepperView {

	/**
	 * Internal view / adapter
	 */
	private RecyclerView mListView;
	private ItemAdapter mAdapter;

	/**
	 * View State
	 */
	private IStepperAdapter mViewAdapter;
	private int mCurrentStep = 0;

	/**
	 * View attributes
	 */
	private boolean mAnimationEnabled;
	private int mAnimationDuration;
	private int mNormalColor, mActivatedColor;
	private Drawable mDoneIcon;

	public VerticalStepperView(Context context) {
		this(context, null);
	}

	public VerticalStepperView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public VerticalStepperView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		mNormalColor = getResources().getColor(R.color.material_grey_500);
		mActivatedColor = ViewUtils.getColorFromAttr(context, R.attr.colorPrimary);
		mDoneIcon = getResources().getDrawable(R.drawable.ic_done_white_16dp);
		mAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

		prepareListView(context);

		if (attrs != null) {
			TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.VerticalStepperView, defStyleAttr, 0);

			mNormalColor = a.getColor(R.styleable.VerticalStepperView_step_normal_color, mNormalColor);
			mActivatedColor = a.getColor(R.styleable.VerticalStepperView_step_activated_color, mActivatedColor);
			mAnimationDuration = a.getInt(R.styleable.VerticalStepperView_step_animation_duration, mAnimationDuration);
			mAnimationEnabled = a.getBoolean(R.styleable.VerticalStepperView_step_enable_animation, true);

			if (a.hasValue(R.styleable.VerticalStepperView_step_done_icon)) {
				mDoneIcon = a.getDrawable(R.styleable.VerticalStepperView_step_done_icon);
			}

			a.recycle();
		}

		setAnimationEnabled(mAnimationEnabled);
	}

	private void prepareListView(Context context) {
		mListView = new RecyclerView(context);
		mAdapter = new ItemAdapter();

		mListView.setClipToPadding(false);
		mListView.setPadding(0, getResources().getDimensionPixelSize(R.dimen.stepper_margin_top), 0, 0);

		mListView.addItemDecoration(new VerticalSpaceItemDecoration(
				getResources().getDimensionPixelSize(R.dimen.vertical_stepper_item_space_height)));
		mListView.setLayoutManager(new LinearLayoutManager(context));
		mListView.setAdapter(mAdapter);

		addView(mListView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
	}

	/**
	 * Set up the stepper adapter
	 *
	 * @param stepperAdapter Stepper Adapter
	 */
	public void setStepperAdapter(IStepperAdapter stepperAdapter) {
		mViewAdapter = stepperAdapter;
		mAdapter.notifyDataSetChanged();
	}

	/**
	 * Return the count of steps
	 *
	 * @return The count of steps
	 */
	public int getStepCount() {
		return mViewAdapter != null ? mViewAdapter.size() : 0;
	}

	/**
	 * Return if stepper can go next
	 *
	 * @return If stepper can go next
	 */
	public boolean canNext() {
		return mViewAdapter != null && mCurrentStep < mViewAdapter.size() - 1;
	}

	/**
	 * Return if stepper can go previous
	 *
	 * @return If stepper can go previous
	 */
	public boolean canPrev() {
		return mViewAdapter != null && mCurrentStep > 0;
	}

	/**
	 * Go next step
	 *
	 * @return If success
	 */
	public boolean nextStep() {
		if (canNext()) {
			mViewAdapter.onHide(mCurrentStep);
			mCurrentStep++;
			mViewAdapter.onShow(mCurrentStep);
			if (mAnimationEnabled) {
				mAdapter.notifyItemRangeChanged(mCurrentStep - 1, 2);
			} else {
				mAdapter.notifyDataSetChanged();
			}
			return true;
		}
		return false;
	}

	/**
	 * Go previous step
	 *
	 * @return If success
	 */
	public boolean prevStep() {
		if (canPrev()) {
			mViewAdapter.onHide(mCurrentStep);
			mCurrentStep--;
			mViewAdapter.onShow(mCurrentStep);
			if (mAnimationEnabled) {
				mAdapter.notifyItemRangeChanged(mCurrentStep, 2);
			} else {
				mAdapter.notifyDataSetChanged();
			}
			return true;
		}
		return false;
	}

	/**
	 * Get Stepper Adapter
	 *
	 * @return Stepper Adapter
	 */
	@Override
	public IStepperAdapter getStepperAdapter() {
		return mViewAdapter;
	}

	/**
	 * Get the index of current step
	 *
	 * @return The index of current step
	 */
	@Override
	public int getCurrentStep() {
		return mCurrentStep;
	}

	/**
	 * Set normal point color
	 *
	 * @param color Normal Point Color
	 */
	public void setNormalColor(@ColorInt int color) {
		mNormalColor = color;
		mAdapter.notifyDataSetChanged();
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
	@Override
	public int getNormalColor() {
		return mNormalColor;
	}

	/**
	 * Set activated point color
	 *
	 * @param color Activated Point Color
	 */
	public void setActivatedColor(@ColorInt int color) {
		mActivatedColor = color;
		mAdapter.notifyDataSetChanged();
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
	 * Get activated point color
	 *
	 * @return Activated Point Color
	 */
	@Override
	public int getActivatedColor() {
		return mActivatedColor;
	}

	/**
	 * Get animation duration
	 *
	 * @return Animation duration
	 */
	@Override
	public int getAnimationDuration() {
		return mAnimationDuration;
	}

	/**
	 * Get done icon drawable
	 *
	 * @return Done Icon Drawable
	 */
	@Override
	public Drawable getDoneIcon() {
		return mDoneIcon;
	}

	/**
	 * Set if animation should be enabled
	 *
	 * @param enabled If animation should be enabled
	 */
	public void setAnimationEnabled(boolean enabled) {
		mAnimationEnabled = enabled;
	}

	/**
	 * Return if animation is enabled
	 *
	 * @return If animation is enabled
	 */
	public boolean isAnimationEnabled() {
		return mAnimationEnabled;
	}

	/**
	 * Set the current step by index
	 *
	 * @param currentStep The index of current step
	 */
	public void setCurrentStep(int currentStep) {
		int minIndex = Math.min(currentStep, mCurrentStep);
		int count = Math.abs(mCurrentStep - currentStep) + 1;

		mCurrentStep = currentStep;
		if (mAnimationEnabled) {
			mAdapter.notifyItemRangeChanged(minIndex, count);
		} else {
			mAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * Internal RecyclerView Adapter to show item views
	 */
	class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemHolder> {

		@Override
		public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			return new ItemHolder(new VerticalStepperItemView(parent.getContext()));
		}

		@Override
		public void onBindViewHolder(ItemHolder holder, int position) {
			holder.mItemView.setIndex(position + 1);
			holder.mItemView.setIsLastStep(position == getItemCount() - 1);
			holder.mItemView.setTitle(getStepperAdapter().getTitle(position));
			holder.mItemView.setSummary(getStepperAdapter().getSummary(position));
			holder.mItemView.setNormalColor(mNormalColor);
			holder.mItemView.setActivatedColor(mActivatedColor);
			holder.mItemView.setAnimationDuration(mAnimationDuration);
			holder.mItemView.setDoneIcon(mDoneIcon);
			holder.mItemView.setAnimationEnabled(mAnimationEnabled);
			if (getCurrentStep() > position) {
				holder.mItemView.setState(VerticalStepperItemView.STATE_DONE);
			} else if (getCurrentStep() < position) {
				holder.mItemView.setState(VerticalStepperItemView.STATE_NORMAL);
			} else {
				holder.mItemView.setState(VerticalStepperItemView.STATE_SELECTED);
			}
			holder.mItemView.removeCustomView();
			View customView = getStepperAdapter().onCreateCustomView(position, getContext(), holder.mItemView);
			if (customView != null) {
				holder.mItemView.addView(customView);
			}
		}

		@Override
		public int getItemCount() {
			return getStepCount();
		}

		class ItemHolder extends RecyclerView.ViewHolder {

			VerticalStepperItemView mItemView;

			ItemHolder(VerticalStepperItemView itemView) {
				super(itemView);
				mItemView = itemView;

				ViewGroup.LayoutParams lp = new LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				mItemView.setLayoutParams(lp);
			}

		}

	}

}
