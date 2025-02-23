package com.example.compair;
//
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import androidx.appcompat.app.AppCompatActivity;
//
//public class CompareActivity extends AppCompatActivity {
//    private EditText inputUrl1, inputUrl2;
//    private Button btnCompare;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_compare);
//
//        inputUrl1 = findViewById(R.id.inputUrl1);
//        inputUrl2 = findViewById(R.id.inputUrl2);
//        btnCompare = findViewById(R.id.btnCompare);
//
//        btnCompare.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String url1 = inputUrl1.getText().toString();
//                String url2 = inputUrl2.getText().toString();
//                compareProducts(url1, url2);
//            }
//        });
//    }
//
//    private void compareProducts(String url1, String url2) {
//        // Fetch product details from Firebase using URLs
//        Product product1 = fetchProductDetails(url1);
//        Product product2 = fetchProductDetails(url2);
//
//        if (product1 == null || product2 == null) {
//            return; // Handle error (optional)
//        }
//
//        Product bestProduct = getBestProduct(product1, product2);
//        openBestProductLink(bestProduct.getUrl());
//    }
//
//    private Product fetchProductDetails(String url) {
//        // TODO: Implement Firebase data fetching based on the URL
//        return new Product("Sample Product", 100.0, 4.5, 200, url);
//    }
//
//    private Product getBestProduct(Product p1, Product p2) {
//        if (p1.getPrice() < p2.getPrice()) return p1;
//        if (p2.getPrice() < p1.getPrice()) return p2;
//
//        if (p1.getRating() > p2.getRating()) return p1;
//        if (p2.getRating() > p1.getRating()) return p2;
//
//        return (p1.getReviews() > p2.getReviews()) ? p1 : p2;
//    }
//
//    private void openBestProductLink(String url) {
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//        startActivity(intent);
//    }
//}
