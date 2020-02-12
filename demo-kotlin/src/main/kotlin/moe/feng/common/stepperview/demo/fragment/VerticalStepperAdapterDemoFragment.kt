package moe.feng.common.stepperview.demo.fragment

import android.content.Context
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

import moe.feng.common.stepperview.IStepperAdapter
import moe.feng.common.stepperview.VerticalStepperItemView
import moe.feng.common.stepperview.VerticalStepperView
import moe.feng.common.stepperview.demo.R

class VerticalStepperAdapterDemoFragment : Fragment(), IStepperAdapter {

    private lateinit var mVerticalStepperView: VerticalStepperView

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_vertical_stepper_adapter, parent, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mVerticalStepperView = view.findViewById(R.id.vertical_stepper_view)
        mVerticalStepperView.stepperAdapter = this
    }

    override fun getTitle(index: Int) = "Step $index"

    override fun getSummary(index: Int): CharSequence? = when (index) {
        0 -> Html.fromHtml("Summarized if needed" + if (mVerticalStepperView.currentStep > index) "; <b>isDone!</b>" else "")
        2 -> Html.fromHtml("Last step" + if (mVerticalStepperView.currentStep > index) "; <b>isDone!</b>" else "")
        else -> null
    }

    override fun size() = 3

    override fun onCreateCustomView(index: Int, context: Context, parent: VerticalStepperItemView): View {
        val inflateView = LayoutInflater.from(context).inflate(R.layout.vertical_stepper_sample_item, parent, false)
        val contentView = inflateView.findViewById<TextView>(R.id.item_content)
        contentView.setText(
                when (index) {
                    0 -> R.string.content_step_0
                    1 -> R.string.content_step_1
                    else -> R.string.content_step_2
                }
        )
        val nextButton = inflateView.findViewById<Button>(R.id.button_next)
        nextButton.text = if (index == size() - 1) "Set error text" else getString(android.R.string.ok)
        nextButton.setOnClickListener {
            if (!mVerticalStepperView.nextStep()) {
                mVerticalStepperView.setErrorText(0, if (mVerticalStepperView.getErrorText(0) == null) "Test error" else null)
                Snackbar.make(mVerticalStepperView, "Set!", Snackbar.LENGTH_LONG).show()
            }
        }
        val prevButton = inflateView.findViewById<Button>(R.id.button_prev)
        prevButton.setText(if (index == 0) R.string.toggle_animation_button else android.R.string.cancel)
        inflateView.findViewById<View>(R.id.button_prev).setOnClickListener {
            if (index != 0) {
                mVerticalStepperView.prevStep()
            } else {
                mVerticalStepperView.isAnimationEnabled = !mVerticalStepperView.isAnimationEnabled
            }
        }
        return inflateView
    }

    override fun onShow(index: Int) {

    }

    override fun onHide(index: Int) {

    }

}
