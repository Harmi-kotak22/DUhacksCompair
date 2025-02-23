package com.example.compair;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

public class ProductInfoActivity extends AppCompatActivity {

    private WebView productWebView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info);

        productWebView = findViewById(R.id.productWebView);

        // Enable JavaScript (if needed)
        WebSettings webSettings = productWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Make links open within the WebView
        productWebView.setWebViewClient(new WebViewClient());

        // Get the URL from the Intent
        String url = getIntent().getStringExtra("PRODUCT_URL");
        String productName = getIntent().getStringExtra("PRODUCT_NAME");
        if (url != null && !url.isEmpty()) {
            setTitle(productName); // Set activity title as product name
            productWebView.loadUrl(url);
        } else {
            Toast.makeText(this, "Product URL not available", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
