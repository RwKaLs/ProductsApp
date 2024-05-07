package com.meganov.productsapp.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.meganov.productsapp.data.Product
import com.meganov.productsapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetails(product: Product, navController: NavController) {
    if (product != null) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column {
                TopAppBar(
                    title = { Text(product.title ?: "Loading Error", color = MaterialTheme.colorScheme.onBackground) },
                    navigationIcon = {
                        IconButton(onClick = {
                            navController.navigate("product_list")
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back Button")
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally).background(Color.Transparent)
                )
                LazyColumn(modifier = Modifier.padding(16.dp).background(Color.Transparent)) {
                    item {
                        ImageCarousel(images = product.images ?: emptyList<String>())
                        TextDetails(product = product)
                    }
                }
            }
        }
    }
}

@Composable
fun ImageCarousel(images: List<String>) {
    val scrollState = rememberScrollState()
    Row(Modifier.horizontalScroll(scrollState)) {
        images.forEach { url ->
            CarouselItem(
                url = url,
                modifier = Modifier
                    .padding(5.dp)
                    .size(400.dp)
            )
        }
    }
}

@Composable
fun CarouselItem(url: String, modifier: Modifier = Modifier) {
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current).data(data = url).apply(block = fun ImageRequest.Builder.() {
            memoryCachePolicy(CachePolicy.ENABLED)
            placeholder(R.color.transparent)
        }).build()
    )
    Image(
        painter = painter,
        contentDescription = "Product Image",
        modifier = modifier
    )
}

@Composable
fun TextDetails(product: Product) {
    Text(
        text = product.title ?: "Error loading title",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground,
        fontWeight = FontWeight.Bold,
        fontSize = 35.sp
    )
    Spacer(modifier = Modifier.height(20.dp))
    RatingBar(rating = product.rating ?: -1.0)
    Spacer(modifier = Modifier.height(20.dp))
    Row {
        Text(
            text = "${product.price ?: "???"} $",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp
        )
        Spacer(modifier = Modifier.width(50.dp))
        Text(
            text = "-${product.discountPercentage ?: 0}%",
            style = MaterialTheme.typography.titleMedium, color = Color.Green,
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp
        )
    }
    Spacer(modifier = Modifier.height(20.dp))
    Text(
        text = "Stock: ${product.stock ?: 0}",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = 20.sp
    )
    Spacer(modifier = Modifier.height(10.dp))
    Text(
        text = "Brand: ${product.brand ?: "No brand"}",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = 20.sp
    )
    Spacer(modifier = Modifier.height(10.dp))
    Text(
        text = product.description ?: "No description",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = 20.sp,
        lineHeight = 24.sp
    )
}