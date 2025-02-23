/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.settings.quicksettings.protections.cookiebanners

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.core.net.toUri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mozilla.components.lib.publicsuffixlist.PublicSuffixList
import mozilla.components.support.ktx.kotlin.toShortUrl
import mozilla.telemetry.glean.private.NoExtras
import org.mozilla.fenix.GleanMetrics.CookieBanners
import org.mozilla.fenix.R
import org.mozilla.fenix.databinding.ComponentCookieBannerDetailsPanelBinding
import org.mozilla.fenix.trackingprotection.CookieBannerUIMode
import org.mozilla.fenix.trackingprotection.ProtectionsState

/**
 * MVI View that knows how to display cookie banner handling details for a site.
 *
 * @param container [ViewGroup] in which this View will inflate itself.
 * @param context An Android [Context].
 * @param ioScope [CoroutineScope] with an IO dispatcher used for structured concurrency.
 * @param publicSuffixList To show short url.
 * @param interactor [CookieBannerDetailsInteractor] which will have delegated to all user interactions.
 * @param onDismiss Lambda invoked to dismiss the cookie banner.
 */
class CookieBannerHandlingDetailsView(
    container: ViewGroup,
    private val context: Context,
    private val ioScope: CoroutineScope,
    private val publicSuffixList: PublicSuffixList,
    private val interactor: CookieBannerDetailsInteractor,
    private val onDismiss: () -> Unit,
) {
    val binding = ComponentCookieBannerDetailsPanelBinding.inflate(
        LayoutInflater.from(container.context),
        container,
        true,
    )

    /**
     * Allows changing what this View displays.
     */
    fun update(state: ProtectionsState) {
        setUiForCookieBannerMode(state)
        bindTitle(state.url, state.cookieBannerUIMode)
        bindBackButtonListener()
        bindDescription(state.cookieBannerUIMode)
    }

    private fun setUiForCookieBannerMode(state: ProtectionsState) {
        when (state.cookieBannerUIMode) {
            CookieBannerUIMode.ENABLE -> setUiForExceptionMode(state)
            CookieBannerUIMode.DISABLE -> setUiForExceptionMode(state)
            CookieBannerUIMode.SITE_NOT_SUPPORTED -> setUiForReportSiteMode()
            else -> {}
        }
    }

    private fun setUiForExceptionMode(state: ProtectionsState) {
        binding.cookieBannerSwitch.visibility = View.VISIBLE
        bindSwitch(state.cookieBannerUIMode)
    }

    private fun setUiForReportSiteMode() {
        binding.cancelButton.visibility = View.VISIBLE
        binding.requestSupport.visibility = View.VISIBLE
        binding.cookieBannerSwitch.visibility = View.GONE
        binding.requestSupport.setOnClickListener {
            interactor.handleRequestSiteSupportPressed()
            onDismiss.invoke()
        }
        binding.cancelButton.setOnClickListener {
            CookieBanners.reportSiteCancelButton.record(NoExtras())
            interactor.onBackPressed()
        }
    }

    @VisibleForTesting
    internal fun bindTitle(url: String, state: CookieBannerUIMode) {
        ioScope.launch {
            val host = url.toUri().host.orEmpty()
            val domain = publicSuffixList.getPublicSuffixPlusOne(host).await()

            launch(Dispatchers.Main) {
                val data = domain ?: url
                val shortUrl = data.toShortUrl(publicSuffixList)
                val title = when (state) {
                    CookieBannerUIMode.ENABLE -> context.getString(
                        R.string.reduce_cookie_banner_details_panel_title_off_for_site_1,
                        shortUrl,
                    )
                    CookieBannerUIMode.DISABLE -> context.getString(
                        R.string.reduce_cookie_banner_details_panel_title_on_for_site_1,
                        shortUrl,
                    )
                    CookieBannerUIMode.SITE_NOT_SUPPORTED -> {
                        val appName = context.getString(R.string.app_name)
                        context.getString(
                            R.string.cookie_banner_handling_details_site_is_not_supported_title_3,
                            appName,
                        )
                    }
                    else -> ""
                }
                binding.title.text = title
            }
        }
    }

    @VisibleForTesting
    internal fun bindDescription(state: CookieBannerUIMode) {
        val appName = context.getString(R.string.app_name)
        val description = when (state) {
            CookieBannerUIMode.ENABLE -> context.getString(
                R.string.reduce_cookie_banner_details_panel_description_off_for_site_1,
                appName,
            )
            CookieBannerUIMode.DISABLE -> context.getString(
                R.string.reduce_cookie_banner_details_panel_description_on_for_site_3,
                appName,
            )
            CookieBannerUIMode.SITE_NOT_SUPPORTED -> context.getString(
                R.string.reduce_cookie_banner_details_panel_title_unsupported_site_request_3,
            )
            else -> ""
        }

        binding.details.text = description
    }

    @VisibleForTesting
    internal fun bindBackButtonListener() {
        binding.navigateBack.setOnClickListener {
            interactor.onBackPressed()
        }
    }

    @VisibleForTesting
    internal fun bindSwitch(state: CookieBannerUIMode) {
        val isCookieBannerHandlingEnabled = when (state) {
            CookieBannerUIMode.ENABLE -> true
            CookieBannerUIMode.DISABLE -> false
            else -> false
        }
        binding.cookieBannerSwitch.isChecked = isCookieBannerHandlingEnabled
        binding.cookieBannerSwitch.setOnCheckedChangeListener { _, isChecked ->
            interactor.onTogglePressed(isChecked)
        }
    }
}
