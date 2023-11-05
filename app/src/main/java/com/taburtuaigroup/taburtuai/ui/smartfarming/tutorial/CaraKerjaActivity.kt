package com.taburtuaigroup.taburtuai.ui.smartfarming.tutorial

import android.annotation.SuppressLint
import android.net.http.SslError
import android.os.Bundle
import android.view.View
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.taburtuaigroup.taburtuai.R
import com.taburtuaigroup.taburtuai.databinding.ActivityCaraKerjaBinding

class CaraKerjaActivity : AppCompatActivity() {
    private lateinit var binding:ActivityCaraKerjaBinding
    var isLoading= MutableLiveData<Boolean>()
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityCaraKerjaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setActionBar()

        isLoading.value=true
        val webSettings = binding.webview.settings
        webSettings.javaScriptEnabled = true
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true
        webSettings.domStorageEnabled=true

        binding.webview.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                isLoading.value=false
                Toast.makeText(this@CaraKerjaActivity, getString(R.string.load_web_success), Toast.LENGTH_LONG).show()
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
                view?.loadUrl(url)
                return true
            }

            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler, error: SslError) {
                val builder = AlertDialog.Builder(this@CaraKerjaActivity)
                var message = "SSL Certificate error."

                when (error.primaryError) {
                    SslError.SSL_UNTRUSTED -> message = "The certificate authority is not trusted."
                    SslError.SSL_EXPIRED -> message = "The certificate has expired."
                    SslError.SSL_IDMISMATCH -> message = "The certificate Hostname mismatch."
                    SslError.SSL_NOTYETVALID -> message = "The certificate is not yet valid."
                }

                message += " Do you want to continue anyway?"

                builder.setTitle("SSL Certificate Error")
                builder.setMessage(message)

                builder.setPositiveButton("continue") { dialog, which ->
                    handler.proceed()
                }

                builder.setNegativeButton("cancel") { dialog, which ->
                    handler.cancel()
                }

                val alertDialog = builder.create()
                alertDialog.show()
            }

        }

        binding.webview.loadUrl(getString(R.string.web_url_cara_kerja_smartfarming))

        isLoading.observe(this){
            showLoading(it)
        }
    }
    private fun showLoading(show:Boolean){

        if(show){
            binding.tvLoadingWeb.visibility= View.VISIBLE
            binding.ltJavrvis.visibility= View.VISIBLE
            binding.webview.visibility= View.GONE
        }else{
            binding.tvLoadingWeb.visibility=View.GONE
            binding.ltJavrvis.visibility= View.GONE
            binding.webview.visibility= View.VISIBLE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setActionBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
    }

}