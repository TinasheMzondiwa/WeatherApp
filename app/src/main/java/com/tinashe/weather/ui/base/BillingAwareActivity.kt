package com.tinashe.weather.ui.base

import android.os.Bundle
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.SkuType
import com.tinashe.weather.BuildConfig
import timber.log.Timber

abstract class BillingAwareActivity : BaseActivity(), PurchasesUpdatedListener, BillingClientStateListener {

    private lateinit var billingClient: BillingClient

    private var skuDetails: SkuDetails? = null

    private var purchasesResult: Purchase.PurchasesResult? = null

    abstract fun premiumUnlocked()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        billingClient = BillingClient.newBuilder(this).setListener(this).build()
        billingClient.startConnection(this)
    }

    override fun onPurchasesUpdated(responseCode: Int, purchases: MutableList<Purchase>?) {
        Timber.d("onPurchasesUpdated: RC $responseCode, $purchases")

        if (responseCode == BillingClient.BillingResponse.OK && purchases != null) {
            purchasesResult = billingClient.queryPurchases(SkuType.INAPP)
            premiumUnlocked()

        } else if (responseCode == BillingClient.BillingResponse.ITEM_ALREADY_OWNED) {
            premiumUnlocked()
        }
    }

    override fun onBillingServiceDisconnected() {
    }

    override fun onBillingSetupFinished(responseCode: Int) {

        if (responseCode == BillingClient.BillingResponse.OK) {
            // The billing client is ready. You can query purchases here.

            val params = SkuDetailsParams.newBuilder()
            params.setSkusList(arrayListOf(PREMIUM_SKU_ID)).setType(SkuType.INAPP)
            billingClient.querySkuDetailsAsync(params.build()) { code, skuDetailsList ->
                // Process the result.
                if (code == BillingClient.BillingResponse.OK && skuDetailsList.isNotEmpty()) {
                    skuDetails = skuDetailsList.find { it.sku == PREMIUM_SKU_ID }
                }
            }
        }

    }

    protected fun promotePremium() {
        skuDetails?.let {
            val fragment = UnlockPremiumFragment.newInstance(it) { purchasePremium() }
            fragment.show(supportFragmentManager, fragment.tag)
        }
    }

    private fun purchasePremium() {

        val flowParams = BillingFlowParams.newBuilder()
                .setSku(skuDetails?.sku)
                .setType(SkuType.INAPP)
                .build()

        val responseCode = billingClient.launchBillingFlow(this, flowParams)
        Timber.d("Response: $responseCode")
    }

    override fun onStart() {
        super.onStart()
        purchasesResult = billingClient.queryPurchases(SkuType.INAPP)

        if (purchasesResult?.purchasesList != null && purchasesResult?.purchasesList?.isNotEmpty() == true) {
            premiumUnlocked()
        }
    }

    companion object {
        private val PREMIUM_SKU_ID = if (BuildConfig.DEBUG) "android.test.purchased" else "premium_upgrade"
    }

}