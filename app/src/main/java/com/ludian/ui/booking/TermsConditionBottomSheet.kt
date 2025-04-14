package com.ludian.ui.booking

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

import androidx.core.widget.ContentLoadingProgressBar
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ludian.R


class TermsConditionBottomSheet : BottomSheetDialogFragment() {
    private lateinit var bottomSheetView: View
    private var url: String? = null

    companion object {
        fun newInstance(
            url: String,
            ): TermsConditionBottomSheet {
            val fragment = TermsConditionBottomSheet()
            fragment.url = url

            return fragment
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bottomSheetView = inflater.inflate(R.layout.activity_terms_web_view, container, false)

        return bottomSheetView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       // val ivBack = bottomSheetView.findViewById<ImageView>(R.id.ivClose)
        val webView = bottomSheetView.findViewById<WebView>(R.id.web_view)
        val progressBar = bottomSheetView.findViewById<ContentLoadingProgressBar>(R.id.progress_bar)

        webView.settings.javaScriptEnabled = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.useWideViewPort = true
        webView.settings.domStorageEnabled = true

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                // Handle URL loading here
                return false
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                progressBar.show()
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                progressBar.hide()
                super.onPageFinished(view, url)
                Log.e("terms url=====", url!!)


            }
        }

        progressBar.show()

        /* Enable Javascript in Webview */

        webView.loadUrl(url!!);


       /* ivBack.setOnClickListener {
            dialog!!.dismiss()
        }*/






    }


}