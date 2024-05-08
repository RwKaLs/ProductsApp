package com.meganov.productsapp.ui.composables

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.meganov.productsapp.data.Product
import com.meganov.productsapp.R

@Composable
fun ProductList(
    products: List<Product>,
    categories: List<String>,
    isLoading: Boolean,
    internetState: () -> Boolean,
    navController: NavController,
    onSearch: (String) -> Unit,
    onSelectCategory: (String) -> Unit,
    emptyProducts: () -> Unit,
    onLoadMore: () -> Unit
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) } // categories menu
    var categoryChosen by rememberSaveable { mutableStateOf(false) } // to keep track when to reload products
    val scrollState = rememberLazyListState()
    val closeToEnd =
        (scrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) >= products.size - 2
    // downloading by chunks (20 items)
    if (closeToEnd && !isLoading && searchQuery == "" && !categoryChosen) {
        onLoadMore()
    }
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {
        Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)) {
            // Search field
            TextField(
                value = searchQuery,
                onValueChange = { query ->
                    searchQuery = query
                    onSearch(query)
                },
                label = { Text("Search", color = MaterialTheme.colorScheme.onSurface) },
                colors = TextFieldDefaults.colors(unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent),
                modifier = Modifier
                    .weight(1f)
                    .padding(5.dp)
            )
            // Choose category
            Box(
                modifier = Modifier.padding(5.dp)
            ) {
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    offset = DpOffset(x = 0.dp, y = 35.dp),
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.surface,
                        )
                        .border(width = 1.dp, color = MaterialTheme.colorScheme.surface)
                        .padding(end = 5.dp)
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = category,
                                    style = TextStyle(color = Color.White, fontSize = 16.sp),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }, onClick = {
                                categoryChosen = true
                                if (category != "all") onSelectCategory(category) else {
                                    emptyProducts()
                                    categoryChosen = false
                                }
                                expanded = false
                            },
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Category", tint = MaterialTheme.colorScheme.onBackground)
                }
            }
        }
        if (products.isEmpty() && !internetState()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Network error")
            }
        } else if (isLoading && products.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.White)
            }
        } else {
            LazyColumn(
                state = scrollState,
                contentPadding = PaddingValues(3.dp),
                modifier = Modifier.background(MaterialTheme.colorScheme.background)
            ) {
                items(products) { product ->
                    if (product != null) ProductItem(product, navController)
                }
            }
        }
    }
}

@Composable
fun ProductItem(product: Product, navController: NavController) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(1.dp),
        colors = CardDefaults.cardColors(
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.onSurface
        ),
        onClick = {
            Log.d("PRODCHECK", "ProductItem: ${product.id} ${product.title}")
            navController.navigate("product_details/${product.id}")
        }
    ) {
        Row {
            NetworkImage(
                url = product.thumbnail ?: "",
                modifier = Modifier
                    .height(170.dp)
                    .width(180.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(5.dp))
            DetailsCard(product = product)
        }

    }
}

/**
 * Load image using coil.
 */
@Composable
fun NetworkImage(url: String, modifier: Modifier = Modifier) {
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current).data(data = url)
            .apply(block = fun ImageRequest.Builder.() {
                memoryCachePolicy(CachePolicy.ENABLED)
                placeholder(R.color.transparent)
            }).build()
    )
    Image(
        painter = painter,
        contentDescription = "Product Image",
        modifier = modifier,
        contentScale = ContentScale.Crop,
    )
}

/**
 * Product details on productlist screen.
 */
@Composable
fun DetailsCard(product: Product) {
    Column {
        Text(
            text = product.title ?: "Error loading title",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(5.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "${product.price ?: "???"} $",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.width(10.dp))
            if (product.discountPercentage != null && product.discountPercentage > 0) {
                Text(
                    text = "(-${product.discountPercentage}%)",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Green
                )
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        RatingBar(product.rating ?: -1.0)
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = product.description ?: "No description",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Star and rating.
 */
@Composable
fun RatingBar(rating: Double, modifier: Modifier = Modifier) {
    Row {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = "Star",
            tint = Color.Yellow,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        Text(
            text = String.format("%.1f", rating),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = modifier
                .padding(start = 4.dp)
                .align(Alignment.CenterVertically)
        )
    }
}