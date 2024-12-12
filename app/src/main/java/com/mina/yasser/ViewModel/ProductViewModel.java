package com.mina.yasser.ViewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mina.yasser.DataBase.Product;
import com.mina.yasser.DataBase.ProductRepository;

import java.util.List;

public class ProductViewModel extends AndroidViewModel {
    private ProductRepository repository;

    public ProductViewModel(Application application) {
        super(application);
        repository = new ProductRepository(application);
    }

    // Get all products as LiveData
    public LiveData<List<Product>> getAllProducts() {
        return repository.getAllProducts();  // repository returns LiveData<List<Product>>
    }

    // Get products by category as LiveData
    public LiveData<List<Product>> getProductsByCategory(int category) {
        return repository.getProductsByCategory(category);  // repository returns LiveData<List<Product>>
    }
}
