package com.tinashe.weather.ui.base


import android.annotation.SuppressLint
import android.app.Dialog
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialogFragment
import android.support.design.widget.CoordinatorLayout
import android.view.View

abstract class BaseBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private lateinit var behavior: BottomSheetBehavior<*>

    private val mBottomSheetBehaviorCallback = object : BottomSheetBehavior.BottomSheetCallback() {

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss()
            }

        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    protected abstract fun layoutRes(): Int

    protected abstract fun initialize(contentView: View)


    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)

        val contentView = View.inflate(context, layoutRes(), null)

        dialog.setContentView(contentView)
        val layoutParams = (contentView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = layoutParams.behavior
        if (behavior != null && behavior is BottomSheetBehavior<*>) {

            behavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)
            this.behavior = behavior
        }

        initialize(contentView)
    }
}
