package com.meganov.productsapp.ui

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.meganov.productsapp.data.Product
import com.meganov.productsapp.data.ProductsRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class ProductListVM(private val repository: ProductsRepository) : ViewModel() {

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> get() = _products

    private val _categories = MutableLiveData<MutableList<String>>()
    val categories: LiveData<MutableList<String>> get() = _categories

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private var page = 0

    private val loadingErrTAG = "Page Loading Error"
    private val compositeDisposable by lazy { CompositeDisposable() }

    init {
        getCategories()
        loadProducts()
    }

    /**
     * Completely remove all products (to keep order after categorization when needed).
     */
    fun emptyProducts() {
        _products.value = emptyList()
        page = 0
    }

    @SuppressLint("CheckResult")
    fun loadProducts() {
        _isLoading.value = true
        compositeDisposable.add(
            repository.getProducts(page * 20, 20)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retry()
                .doFinally {
                    _isLoading.value = false
                }
                .subscribe({ newProducts ->
                    _products.value = _products.value.orEmpty() + newProducts.products
                    page++
                }, { error ->
                    Log.d(loadingErrTAG, "loadProducts: ${error.message}")
                })
        )
    }

    @SuppressLint("CheckResult")
    fun searchProducts(query: String) {
        _products.value = emptyList()
        _isLoading.value = true
        repository.search(query)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .retry()
            .doFinally { _isLoading.value = false }
            .subscribe({ searchResults ->
                _products.value = searchResults.products
            }, { error ->
                Log.d(loadingErrTAG, "searchProducts: ${error.message}")
            })
    }

    @SuppressLint("CheckResult")
    fun getProductsByCategory(category: String) {
        _products.value = emptyList()
        _isLoading.value = true
        page = 0
        repository.getProductsByCategory(category)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .retry()
            .doFinally { _isLoading.value = false }
            .subscribe({ categoryProducts ->
                _products.value = categoryProducts.products
            }, { error ->
                Log.d(loadingErrTAG, "getProductsByCategory: ${error.message}")
            })
    }

    fun getProductById(id: Int): Product? {
        if (id - 1 in _products.value!!.indices) {
            val productByIndex = _products.value!![id - 1]
            if (productByIndex.id == id - 1) {
                return productByIndex
            }
        }
        return _products.value!!.find { it.id == id }
    }

    @SuppressLint("CheckResult")
    fun getCategories() {
        _isLoading.value = true
        repository.getCategories()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .retry()
            .doFinally { _isLoading.value = false }
            .subscribe({ categories ->
                _categories.value = categories.toMutableList()
                if (_categories.value != null) _categories.value!!.add(0, "all")
            }, { error ->
                Log.d(loadingErrTAG, "getCategories: ${error.message}")
            })
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}