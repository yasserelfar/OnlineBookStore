package com.mina.yasser;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mina.yasser.DataBase.AppDatabase;
import com.mina.yasser.DataBase.Product;
import com.mina.yasser.DataBase.ProductDao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.mina.yasser.Adapter.ProductAdapter;

public class HomeActivity extends AppCompatActivity {
    private static final int VOICE_SEARCH_REQUEST_CODE = 1001;
    private static final int BARCODE_SCANNER_REQUEST_CODE = 1002;

    private ProductDao productDao;
    private ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);

        EditText edtSearch = findViewById(R.id.edtSearch);
        ImageButton btnVoiceSearch = findViewById(R.id.btnVoiceSearch);
        ImageButton btnBarcodeSearch = findViewById(R.id.btnBarcodeSearch);

        RecyclerView productList = findViewById(R.id.productList);
        productList.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new ProductAdapter(this, new ArrayList<>(), false, productDao);
        productList.setAdapter(productAdapter);

        AppDatabase database = AppDatabase.getInstance(this);
        productDao = database.productDao();

        // Observe products initially
        LiveData<List<Product>> allProducts = productDao.getAllProducts();
        allProducts.observe(this, products -> productAdapter.setProductList(products));

        // Text Search
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Voice Search
        btnVoiceSearch.setOnClickListener(v -> startVoiceSearch());

        // Barcode Search
        btnBarcodeSearch.setOnClickListener(v -> startBarcodeScanner());
    }

    private void searchProducts(String query) {
        // Observe the LiveData returned by the DAO method
        LiveData<List<Product>> filteredProducts = productDao.searchByTitleOrAuthor("%" + query + "%");

        // Use LiveData's observer to update the product list in the adapter when data changes
        filteredProducts.observe(this, products -> {
            if (products != null) {
                productAdapter.setProductList(products); // Update the adapter with the new product list
            }
        });
    }


    private void startVoiceSearch() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak the title or author name");

        try {
            startActivityForResult(intent, VOICE_SEARCH_REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Voice search not supported on this device", Toast.LENGTH_SHORT).show();
        }
    }

    private void startBarcodeScanner() {
        Intent intent = new Intent(this, BarcodeScannerActivity.class);
        startActivityForResult(intent, BARCODE_SCANNER_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VOICE_SEARCH_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (results != null && !results.isEmpty()) {
                String voiceQuery = results.get(0);
                searchProducts(voiceQuery);
            }
        } else if (requestCode == BARCODE_SCANNER_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String barcode = data.getStringExtra("barcode");
            searchByBarcode(barcode);
        }
    }

    private void searchByBarcode(String barcode) {
        new Thread(() -> {
            Product product = productDao.getProductByBarcode(barcode).getValue();
            if (product != null) {
                List<Product> singleResult = Collections.singletonList(product);
                runOnUiThread(() -> productAdapter.setProductList(singleResult));
            } else {
                runOnUiThread(() -> Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

}
