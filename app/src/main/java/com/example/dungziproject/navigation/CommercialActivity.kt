package com.example.dungziproject

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.dungziproject.databinding.ActivityCommercialBinding

class CommercialActivity : AppCompatActivity() {
    lateinit var binding: ActivityCommercialBinding
    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommercialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.webView.webViewClient = WebViewClient() // 웹 페이지 로딩을 위한 WebViewClient 설정
        webView = binding.webView
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true // 자바스크립트 사용을 허용
        webView.loadUrl("https://korean.visitkorea.or.kr/kfes/detail/fstvlDetail.do;jsessionid=7A544E70B8F7298A7F6F793210E14530.instance1?cmsCntntsId=1230074") // 로드할 웹 페이지의 URL을 설정
    }
}