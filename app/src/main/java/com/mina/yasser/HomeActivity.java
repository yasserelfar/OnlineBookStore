package com.mina.yasser;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
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

import android.view.View;
import android.widget.AdapterView;
import com.mina.yasser.DataBase.Category;
import com.mina.yasser.DataBase.CategoryDao;
import android.widget.Spinner;
import android.widget.ArrayAdapter;


public class HomeActivity extends AppCompatActivity {
    private static final int VOICE_SEARCH_REQUEST_CODE = 1001;
    private static final int BARCODE_SCANNER_REQUEST_CODE = 1002;
    private Spinner spinnerCategory;
    List<Category> categoryList = new ArrayList<>();
    private ProductDao productDao;
    private ProductAdapter productAdapter;
    private Button btnViewCart;
    private Spinner spinnerSort;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnViewCart =findViewById(R.id.btnViewCart);
        EditText edtSearch = findViewById(R.id.edtSearch);
        ImageButton btnVoiceSearch = findViewById(R.id.btnVoiceSearch);
        ImageButton btnBarcodeSearch = findViewById(R.id.btnBarcodeSearch);
        ImageButton btnAccount = findViewById(R.id.btnAccount);
        RecyclerView productList = findViewById(R.id.productList);
        productList.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new ProductAdapter(this, new ArrayList<>(), false, productDao,btnViewCart);
        productList.setAdapter(productAdapter);
        spinnerSort = findViewById(R.id.spinnerSort); // Add this for the sorting spinner
        setupSortSpinner(); // Call this to initialize sorting spinner
//        productAdapter = new ProductAdapter(this, new ArrayList<>(), false, productDao, btnViewCart);//cart view btn
        // view cart button navigate to CartActivity
        btnViewCart.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CartActivity.class);
            startActivity(intent);
        });
        // Update the cart count whenever the activity starts


        AppDatabase database = AppDatabase.getInstance(this);
        productDao = database.productDao();
        btnAccount.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, AccountManagementActivity.class);
            startActivity(intent);
        });
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
        loadCategories();
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Assuming the category ID is at the same position in the category list
                position--;
                if(position!=-1){
                    int selectedCategoryId = categoryList.get(position).getId();
                    Log.d("categoryid","category id is : "+categoryList.get(position).getId());
                    filterByCategory(selectedCategoryId);}
                else{
                    allProducts.observe(HomeActivity.this, products -> productAdapter.setProductList(products));
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                loadCategories();
            }
        });

    }
    private void setupSortSpinner() {
        List<String> sortOptions = new ArrayList<>();
        sortOptions.add("Sort by");
        sortOptions.add("Popularity");
        sortOptions.add("Price");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sortOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(adapter);

        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    // Sort by popularity
                    productAdapter.sortProducts("popularity", true);
                } else if (position == 2) {
                    // Sort by price
                    productAdapter.sortProducts("price", true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optional: Handle the case when nothing is selected
            }
        });

    }

    // Sort products by popularity
//    private void sortProductsByPopularity() {
//        LiveData<List<Product>> allProducts = productDao.getAllProducts();
//        allProducts.observe(this, products -> {
//            if (products != null) {
//                Collections.sort(products, (p1, p2) -> Integer.compare(p2.getPopularity(), p1.getPopularity()));
//                productAdapter.setProductList(products);
//            }
//        });
//    }
//
//    // Sort products by price
//    private void sortProductsByPrice() {
//        LiveData<List<Product>> allProducts = productDao.getAllProducts();
//        allProducts.observe(this, products -> {
//            if (products != null) {
//                Collections.sort(products, (p1, p2) -> Double.compare(p1.getPrice(), p2.getPrice())); // Assuming Product has a 'price' field
//                productAdapter.setProductList(products);
//            }
//        });
//    }
    private void loadCategories() {
        CategoryDao categoryDao = AppDatabase.getInstance(this).categoryDao();
        categoryDao.getAllCategories().observe(this, categories -> {
            categoryList=categories;
            List<String> categoryNames = new ArrayList<>();
            categoryNames.add("All Categories");
            for (Category category : categories) {
                categoryNames.add(category.getName());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCategory.setAdapter(adapter);
        });
    }

    // Assuming the filterByCategory method expects an integer representing the category ID
    private void filterByCategory(int categoryId) {
        LiveData<List<Product>> filteredProducts = productDao.getProductsByCategory(categoryId);
        filteredProducts.observe(this, products -> {
            if (products != null) {
                productAdapter.setProductList(products);
            }
        });
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
            String barcode = data.getStringExtra("SCAN_RESULT");
            if (barcode != null) {
                Toast.makeText(this, "Barcode is"+barcode, Toast.LENGTH_SHORT).show();
                searchByBarcode(barcode); // Process the barcode
            } else {
                Toast.makeText(this, "Barcode not found in the result data.", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Barcode is"+ barcode, Toast.LENGTH_SHORT).show();
                Log.e("BarcodeScanner", "Barcode not found in the result data.");
            }
        }
    }
    private void searchByBarcode(String barcode) {
        Toast.makeText(this, "Barcode is: " + barcode, Toast.LENGTH_SHORT).show();

        // Check if barcode is valid before proceeding
        if (barcode != null && !barcode.trim().isEmpty()) {
            // Get the product query (LiveData)
            LiveData<Product> products = productDao.getProductByBarcode(barcode);

            // Observe the LiveData on the main thread
            products.observe(this, product -> {
                if (product != null) {
                    // If product is found, update the UI
                    List<Product> singleResult = Collections.singletonList(product);
                    if (productAdapter != null) {
                        productAdapter.setProductList(singleResult);
                    }
                } else {
                    // If no product is found, show a toast
                    Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // If barcode is invalid, show a toast
            Toast.makeText(this, "Invalid barcode input", Toast.LENGTH_SHORT).show();
        }
    }


//    private void searchByBarcode(String barcode) {
//        Toast.makeText(this, "Barcode is"+ barcode, Toast.LENGTH_SHORT).show();
//        new Thread(() -> {
//            Product product = productDao.getProductByBarcode(barcode).getValue();
//            if (product != null) {
//                List<Product> singleResult = Collections.singletonList(product);
//                runOnUiThread(() -> productAdapter.setProductList(singleResult)); // Update adapter with the single product
//            } else {
//                runOnUiThread(() -> Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show());
//            }
//        }).start();
//    }



}
