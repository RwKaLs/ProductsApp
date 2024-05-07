package com.meganov.productsapp.ui.composables

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.meganov.productsapp.ui.ProductListVM

@Composable
fun App(context: Context, viewModel: ProductListVM) {
    val products by viewModel.products.observeAsState(emptyList())
    val categories by viewModel.categories.observeAsState(emptyList())
    // check that some products are loading
    val isLoading by viewModel.isLoading.observeAsState(false)
    /*
    2 screens, navigation by navController product_list -> product_details(id)
     */
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "product_list") {
        composable("product_list") {
            ProductList(
                products = products,
                categories = categories,
                isLoading = isLoading,
                internetState = { isNetworkAvailable(context) },
                navController = navController,
                onSearch = viewModel::searchProducts,
                onSelectCategory = viewModel::getProductsByCategory,
                emptyProducts = viewModel::emptyProducts,
                onLoadMore = viewModel::loadProducts
            )
        }
        composable("product_details/{product_id}") { navBackStackEntry ->
            // try to get the product_id and pass to details screen
            val id: String? = navBackStackEntry.arguments?.getString("product_id")
            if (id == null) {
                Toast.makeText(context, "The product does not exist!", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.getProductById(id.toInt())
                    ?.let { ProductDetails(product = it, navController = navController) }
            }
        }
    }
}

/**
 * Check the network connection.
 */
@SuppressLint("ServiceCast")
fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connectivityManager.activeNetworkInfo
    return networkInfo != null && networkInfo.isConnected
}