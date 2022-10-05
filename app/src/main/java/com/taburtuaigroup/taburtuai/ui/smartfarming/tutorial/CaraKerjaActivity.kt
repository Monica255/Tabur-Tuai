package com.taburtuaigroup.taburtuai.ui.smartfarming.tutorial

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.taburtuaigroup.taburtuai.R
import com.taburtuaigroup.taburtuai.databinding.ActivityCaraKerjaBinding

class CaraKerjaActivity : AppCompatActivity() {
    private lateinit var binding:ActivityCaraKerjaBinding
    var isLoading= MutableLiveData<Boolean>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityCaraKerjaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setActionBar()

        isLoading.value=true
        val webSettings = binding.webview.settings
        webSettings.javaScriptEnabled = true

        binding.webview.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                isLoading.value=false
                Toast.makeText(this@CaraKerjaActivity, getString(R.string.load_web_success), Toast.LENGTH_LONG).show()
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