package com.meganov.productsapp.data

import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductsService {
    @GET("/products")
    fun getProducts(@Query("skip") skip: Int, @Query("limit") limit: Int): Single<ProductsResponse>

    @GET("/products/search")
    fun search(@Query("q") query: String): Single<ProductsResponse>

    @GET("/products/category/{category}")
    fun getProductsByCategory(@Path("category") category: String): Single<ProductsResponse>

    @GET("/products/categories")
    fun getCategories(): Single<List<String>>
}