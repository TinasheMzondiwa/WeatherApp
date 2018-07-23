package com.tinashe.weather.ui.base

import android.os.Bundle
import com.android.billingclient.api.*
import timber.log.Timber

abstract class BillingAwareActivity : BaseThemedActivity(), PurchasesUpdatedListener, BillingClientStateListener {

    private lateinit var billingClient: BillingClient

    private var billingResponseCode: Int = BillingClient.BillingResponse.BILLING_UNAVAILABLE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        billingClient = BillingClient.newBuilder(this).setListener(this).build()
        billingClient.startConnection(this)
    }

    override fun onPurchasesUpdated(responseCode: Int, purchases: MutableList<Purchase>?) {
        Timber.d("onPurchasesUpdated: RC $responseCode, $purchases")
    }

    override fun onBillingServiceDisconnected() {
        Timber.d("onBillingServiceDisconnected")
    }

    override fun onBillingSetupFinished(responseCode: Int) {
        Timber.d("onBillingSetupFinished: RC $responseCode")
        this.billingResponseCode = responseCode

        if (billingResponseCode == BillingClient.BillingResponse.OK) {
            // The billing client is ready. You can query purchases here.

            val params = SkuDetailsParams.newBuilder()
            params.setSkusList(arrayListOf(PREMIUM_SKU_ID)).setType(BillingClient.SkuType.INAPP)
            billingClient.querySkuDetailsAsync(params.build()) { code, skuDetailsList ->
                // Process the result.
                Timber.d("RESULT: $code, $skuDetailsList")
            }
        }

    }

    companion object {
        private const val PREMIUM_SKU_ID = "premium_upgrade"
    }

}