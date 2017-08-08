package moe.feng.common.stepperview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import moe.feng.common.stepperview.internal.VerticalSpaceItemDecoration;

public class VerticalStepperView extends FrameLayout implements IStepperView {

	private RecyclerView mListView;
	private ItemAdapter mAdapter;

	private IStepperViewAdapter mViewAdapter;
	private int mCurrentStep = 0;

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

			if (a.hasValue(R.styleable.VerticalStepperView_step_done_icon)) {
				mDoneIcon = a.getDrawable(R.styleable.VerticalStepperView_step_done_icon);
			}

			a.recycle();
		}
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

		addView(mListView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
	}

	public void setViewAdapter(IStepperViewAdapter viewAdapter) {
		mViewAdapter = viewAdapter;
		mAdapter.notifyDataSetChanged();
	}

	public int getStepCount() {
		return mViewAdapter != null ? mViewAdapter.size() : 0;
	}

	public boolean canNext() {
		return mViewAdapter != null && mCurrentStep < mViewAdapter.size() - 1;
	}

	public boolean canPrev() {
		return mViewAdapter != null && mCurrentStep > 0;
	}

	public boolean nextStep() {
		if (canNext()) {
			mCurrentStep++;
			mAdapter.notifyDataSetChanged();
			return true;
		}
		return false;
	}

	public boolean prevStep() {
		if (canPrev()) {
			mCurrentStep--;
			mAdapter.notifyDataSetChanged();
			return true;
		}
		return false;
	}

	@Override
	public IStepperViewAdapter getViewAdapter() {
		return mViewAdapter;
	}

	@Override
	public int getCurrentStep() {
		return mCurrentStep;
	}

	@Override
	public int getNormalColor() {
		return mNormalColor;
	}

	@Override
	public int getActivatedColor() {
		return mActivatedColor;
	}

	@Override
	public int getAnimationDuration() {
		return mAnimationDuration;
	}

	@Override
	public Drawable getDoneIcon() {
		return mDoneIcon;
	}

	public void setCurrentStep(int currentStep) {
		mCurrentStep = currentStep;
		mAdapter.notifyDataSetChanged();
	}

	class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemHolder> {

		@Override
		public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			return new ItemHolder(new VerticalStepperItemView(parent.getContext()));
		}

		@Override
		public void onBindViewHolder(ItemHolder holder, int position) {
			holder.mItemView.setIndex(position);
			holder.mItemView.setIsLastStep(position == getItemCount() - 1);
			holder.mItemView.setTitle(getViewAdapter().getTitle(position));
			holder.mItemView.setSummary(getViewAdapter().getSummary(position));
			holder.mItemView.setNormalColor(mNormalColor);
			holder.mItemView.setActivatedColor(mActivatedColor);
			holder.mItemView.setAnimationDuration(mAnimationDuration);
			holder.mItemView.setDoneIcon(mDoneIcon);
			if (getCurrentStep() > position) {
				holder.mItemView.setState(VerticalStepperItemView.STATE_DONE);
			} else if (getCurrentStep() < position) {
				holder.mItemView.setState(VerticalStepperItemView.STATE_NORMAL);
			} else {
				holder.mItemView.setState(VerticalStepperItemView.STATE_SELECTED);
			}
			holder.mItemView.removeCustomView();
			holder.mItemView.addView(getViewAdapter().onCreateCustomView(position, getContext(), holder.mItemView));
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
