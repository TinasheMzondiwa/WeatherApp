package com.tinashe.weather.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.billingclient.api.SkuDetails
import com.tinashe.weather.R
import kotlinx.android.synthetic.main.fragment_premium_features.view.*

class UnlockPremiumFragment : RoundedBottomSheetDialogFragment() {

    private lateinit var skuDetails: SkuDetails

    private lateinit var callback: () -> Unit

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_premium_features, container, false)
    }

    override fun onViewCreated(contentView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(contentView, savedInstanceState)

        contentView.btnPurchase.text = skuDetails.price
        contentView.btnPurchase.setOnClickListener {
            callback.invoke()
            dismiss()
        }
    }

    companion object {

        fun newInstance(skuDetails: SkuDetails, callback: () -> Unit): UnlockPremiumFragment {

            val fragment = UnlockPremiumFragment()
            fragment.skuDetails = skuDetails
            fragment.callback = callback

            return fragment
        }
    }
}