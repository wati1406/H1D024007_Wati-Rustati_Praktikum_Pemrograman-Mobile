package com.example.watirustati.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items as lazyRowItems
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.watirustati.data.dummy.DummyData
import com.example.watirustati.data.model.Category
import com.example.watirustati.data.model.Product
import com.example.watirustati.ui.theme.JualanTheme
import kotlinx.coroutines.delay

// Hijau untuk TopAppBar, border, dan loading (sesuai contoh)
private val GreenBar = Color(0xFF4CAF50)

// Langkah 2, 10: function state (menyimpan data) yang memanggil StatelessDaftarProduct
@Composable
fun DaftarProductScreen(navController: NavController? = null) {
    var selectedCategoryId by rememberSaveable {
        mutableStateOf(value = DummyData.categories.firstOrNull()?.id)
    }
    var searchQuery by rememberSaveable { mutableStateOf(value = "") }
    var isLoading by remember { mutableStateOf(value = false) }
    var filteredProducts by remember { mutableStateOf(value = emptyList<Product>()) }

    LaunchedEffect(key1 = selectedCategoryId, key2 = searchQuery) {
        isLoading = true

        delay(timeMillis = 1000)

        val filteredByCategory = if (selectedCategoryId != null) {
            DummyData.products.filter { it.category_id == selectedCategoryId }
        } else DummyData.products

        filteredProducts = if (searchQuery.isBlank()) {
            filteredByCategory
        } else {
            filteredByCategory.filter { it.name.contains(other = searchQuery, ignoreCase = true) }
        }

        isLoading = false
    }

    StatelessDaftarProduct(
        categories = DummyData.categories,
        selectedCategoryId = selectedCategoryId,
        onCategorySelected = { selectedCategoryId = it },
        searchQuery = searchQuery,
        onSearchQueryChange = { searchQuery = it },
        isLoading = isLoading,
        products = filteredProducts,
        onProductClick = { product ->
            navController?.navigate("detail/${product.id}")
        },
        onContactUsClick = {
            navController?.navigate("hubungi_kami")
        }
    )
}

// Langkah 3-9: function stateless (hanya menampilkan UI)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatelessDaftarProduct(
    categories: List<Category>,
    selectedCategoryId: Int?,
    onCategorySelected: (Int) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    isLoading: Boolean,
    products: List<Product>,
    onProductClick: (Product) -> Unit,
    onContactUsClick: () -> Unit
) {
    // Status buka/tutup menu titik tiga
    var expanded by remember { mutableStateOf(value = false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Daftar Produk UMKM") },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Keranjang")
                    }

                    Box {
                        IconButton(onClick = { expanded = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Menu",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Hubungi Kami") },
                                onClick = {
                                    expanded = false
                                    onContactUsClick()
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Email,
                                        contentDescription = "Email"
                                    )
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GreenBar,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                label = { Text("Cari produk...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GreenBar,
                    unfocusedBorderColor = GreenBar,
                    focusedLabelColor = GreenBar,
                    unfocusedLabelColor = GreenBar,
                    cursorColor = GreenBar,
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                )
            )

            Text(
                text = "Kategori Produk",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(all = 16.dp)
            )

            // Langkah 6: items memakai parameter categories
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                lazyRowItems(categories) { category ->
                    CategoryItem(
                        category = category,
                        isSelected = category.id == selectedCategoryId,
                        onClick = { onCategorySelected(category.id) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Daftar Produk",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Langkah 7: blok pengkondisian tampilan
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = GreenBar)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Mencari data...",
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            } else {
                if (products.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Produk tidak ditemukan.",
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                } else {
                    // Langkah 8 & 9: LazyVerticalGrid di dalam blok else
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(count = 2),
                        contentPadding = PaddingValues(all = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(products) { product ->
                            ProductItemCard(product = product) {
                                onProductClick(product)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDaftarProductScreen() {
    JualanTheme(darkTheme = false) {
        StatelessDaftarProduct(
            categories = DummyData.categories,
            selectedCategoryId = DummyData.categories.firstOrNull()?.id,
            onCategorySelected = {},
            searchQuery = "",
            onSearchQueryChange = {},
            isLoading = false,
            products = DummyData.products.filter {
                it.category_id == DummyData.categories.firstOrNull()?.id
            },
            onProductClick = {},
            onContactUsClick = {}
        )
    }
}

// Preview gelap: ini yang disamakan dengan contoh
@Preview(showBackground = true)
@Composable
fun PreviewDaftarProductScreenDark() {
    JualanTheme(darkTheme = true) {
        StatelessDaftarProduct(
            categories = DummyData.categories,
            selectedCategoryId = DummyData.categories.firstOrNull()?.id,
            onCategorySelected = {},
            searchQuery = "",
            onSearchQueryChange = {},
            isLoading = false,
            products = DummyData.products.filter {
                it.category_id == DummyData.categories.firstOrNull()?.id
            },
            onProductClick = {},
            onContactUsClick = {}
        )
    }
}