package moe.feng.common.stepperview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ListView;

public class VerticalStepperView extends FrameLayout implements IStepperView {

	private ListView mListView;
	private VerticalStepperAdapter mAdapter;

	private IStepperViewAdapter mViewAdapter;

	public VerticalStepperView(Context context) {
		this(context, null);
	}

	public VerticalStepperView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public VerticalStepperView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		mListView = new ListView(context);
		mAdapter = new VerticalStepperAdapter(this);

		mListView.setSelector(null);
		mListView.setAdapter(mAdapter);
	}

	public void setViewAdapter(IStepperViewAdapter viewAdapter) {
		mViewAdapter = viewAdapter;
	}

	@Override
	public IStepperViewAdapter getViewAdapter() {
		return mViewAdapter;
	}

}
