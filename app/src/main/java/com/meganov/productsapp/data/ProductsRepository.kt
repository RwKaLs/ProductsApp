package com.meganov.productsapp.data

import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class ProductsRepository @Inject constructor(private val api: ProductsService) {

    fun getProducts(start: Int, count: Int): Single<ProductsResponse> {
        return api.getProducts(start, count)
    }

    fun search(query: String): Single<ProductsResponse> {
        return api.search(query)
    }

    fun getProductsByCategory(category: String): Single<ProductsResponse> {
        return api.getProductsByCategory(category)
    }

    fun getCategories(): Single<List<String>> {
        return api.getCategories()
    }
}
