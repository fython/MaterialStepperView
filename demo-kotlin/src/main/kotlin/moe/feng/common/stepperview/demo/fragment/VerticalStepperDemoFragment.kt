package moe.feng.common.stepperview.demo.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

import moe.feng.common.stepperview.VerticalStepperItemView
import moe.feng.common.stepperview.demo.R

class VerticalStepperDemoFragment : Fragment() {

    private val mSteppers = arrayOfNulls<VerticalStepperItemView>(3)
    private lateinit var mNextBtn0: Button
    private lateinit var mNextBtn1: Button
    private lateinit var mPrevBtn1: Button
    private lateinit var mNextBtn2: Button
    private lateinit var mPrevBtn2: Button

    private var mActivatedColorRes = R.color.material_blue_500
    private var mDoneIconRes = R.drawable.ic_done_white_16dp

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_vertical_stepper, parent, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mSteppers[0] = view.findViewById(R.id.stepper_0)
        mSteppers[1] = view.findViewById(R.id.stepper_1)
        mSteppers[2] = view.findViewById(R.id.stepper_2)

        VerticalStepperItemView.bindSteppers(*mSteppers)

        mNextBtn0 = view.findViewById(R.id.button_next_0)
        mNextBtn0.setOnClickListener { mSteppers[0]?.nextStep() }

        view.findViewById<View>(R.id.button_test_error).setOnClickListener {
            if (mSteppers[0]?.errorText != null) {
                mSteppers[0]?.errorText = null
            } else {
                mSteppers[0]?.errorText = "Test error!"
            }
        }

        mPrevBtn1 = view.findViewById(R.id.button_prev_1)
        mPrevBtn1.setOnClickListener { mSteppers[1]?.prevStep() }

        mNextBtn1 = view.findViewById(R.id.button_next_1)
        mNextBtn1.setOnClickListener { mSteppers[1]?.nextStep() }

        mPrevBtn2 = view.findViewById(R.id.button_prev_2)
        mPrevBtn2.setOnClickListener { mSteppers[2]?.prevStep() }

        mNextBtn2 = view.findViewById(R.id.button_next_2)
        mNextBtn2.setOnClickListener {
            Snackbar.make(view, "Finish!", Snackbar.LENGTH_LONG).show()
        }

        view.findViewById<View>(R.id.btn_change_point_color).setOnClickListener {
            mActivatedColorRes = if (mActivatedColorRes == R.color.material_blue_500) {
                R.color.material_deep_purple_500
            } else {
                R.color.material_blue_500
            }
            for (stepper in mSteppers) {
                stepper?.setActivatedColorResource(mActivatedColorRes)
            }
        }
        view.findViewById<View>(R.id.btn_change_done_icon).setOnClickListener {
            mDoneIconRes = if (mDoneIconRes == R.drawable.ic_done_white_16dp) {
                R.drawable.ic_save_white_16dp
            } else {
                R.drawable.ic_done_white_16dp
            }
            for (stepper in mSteppers) {
                stepper?.setDoneIconResource(mDoneIconRes)
            }
        }
    }

}
