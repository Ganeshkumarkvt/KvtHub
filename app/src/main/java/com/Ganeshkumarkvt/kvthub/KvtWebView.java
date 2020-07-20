package com.Ganeshkumarkvt.kvthub;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class KvtWebView extends AppCompatActivity {

    private WebView KVT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kvt_web_view);

        KVT = findViewById(R.id.KvtWebView);
        KVT.setWebViewClient(new WebViewClient());
        KVT.loadUrl("https://sites.google.com/view/kondalvattamthidal");

        WebSettings webSettings = KVT.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }

    @Override
    public void onBackPressed() {
        if(KVT.canGoBack()) {
            KVT.goBack();
        }else {
            super.onBackPressed();
        }
    }
}