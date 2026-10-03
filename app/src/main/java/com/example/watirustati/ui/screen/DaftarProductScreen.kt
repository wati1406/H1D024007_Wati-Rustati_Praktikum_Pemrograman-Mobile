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
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.collectAsState
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
import com.example.watirustati.data.model.Category
import com.example.watirustati.data.model.Product
import com.example.watirustati.ui.theme.JualanTheme
import com.example.watirustati.ui.viewmodel.ProductUiState
import com.example.watirustati.ui.viewmodel.ProductViewModel

private val GreenBar = Color(0xFF4CAF50)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DaftarProductScreen(
    navController: NavController? = null,
    viewModel: ProductViewModel
) {
    var selectedCategoryId by rememberSaveable { mutableStateOf<Int?>(null) }
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by rememberSaveable { mutableStateOf("") }

    when (val state = uiState) {
        is ProductUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = GreenBar)
            }
        }

        is ProductUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
            }
        }

        is ProductUiState.Success -> {
            if (selectedCategoryId == null && state.categories.isNotEmpty()) {
                selectedCategoryId = state.categories.first().id
            }

            val filteredByCategory = if (selectedCategoryId != null) {
                state.products.filter { it.category_id == selectedCategoryId }
            } else {
                state.products
            }

            val filteredProducts = if (searchQuery.isBlank()) {
                filteredByCategory
            } else {
                filteredByCategory.filter {
                    it.name.contains(other = searchQuery, ignoreCase = true)
                }
            }

            StatelessDaftarProduct(
                categories = state.categories,
                selectedCategoryId = selectedCategoryId,
                onCategorySelected = { selectedCategoryId = it },
                searchQuery = searchQuery,
                onSearchQueryChange = { /* handled di state hoisting */ },
                isLoading = false,
                products = filteredProducts,
                onProductClick = { product ->
                    navController?.navigate("detail/${product.id}")
                },
                onContactUsClick = {
                    navController?.navigate("hubungi_kami")
                }
            )
        }
    }
}

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
    var expanded by remember { mutableStateOf(false) }

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
                                contentDescription = "Menu"
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
                    focusedBorderColor = MaterialTheme.colorScheme.outline,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    cursorColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Text(
                text = "Kategori Produk",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
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
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = GreenBar)
                }
            } else if (products.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Produk tidak ditemukan.")
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(products) { product ->
                        ProductItemCard(
                            product = product,
                            onClick = { onProductClick(product) }
                        )
                    }
                }
            }
        }
    }
}

// Sample data kategori
private val sampleCategories = listOf(
    Category(id = 1, name = "Makanan", description = "Aneka makanan ringan", products_count = 4),
    Category(id = 2, name = "Minuman", description = "Aneka minuman segar", products_count = 2),
    Category(id = 3, name = "Kerajinan", description = "Aneka kerajinan tangan", products_count = 1)
)

// Sample data produk
private val sampleProducts = listOf(
    Product(
        id = 1,
        name = "Kripik Singkong",
        price = 15000.0,
        category_id = 1,
        img = "dummy_product",
        description = "Kripik gurih renyah",
        stock = 50,
        category = sampleCategories[0]
    ),
    Product(
        id = 2,
        name = "Mendoan",
        price = 10000.0,
        category_id = 1,
        img = "dummy_product",
        description = "Mendoan hangat",
        stock = 30,
        category = sampleCategories[0]
    ),
    Product(
        id = 3,
        name = "Sale Pisang",
        price = 20000.0,
        category_id = 1,
        img = "dummy_product",
        description = "Sale pisang manis",
        stock = 20,
        category = sampleCategories[0]
    ),
    Product(
        id = 4,
        name = "Getuk Goreng",
        price = 10000.0,
        category_id = 1,
        img = "dummy_product",
        description = "Getuk goreng gurih",
        stock = 15,
        category = sampleCategories[0]
    ),
    Product(
        id = 5,
        name = "Es Teh Manis",
        price = 5000.0,
        category_id = 2,
        img = "dummy_product",
        description = "Es teh segar",
        stock = 100,
        category = sampleCategories[1]
    ),
    Product(
        id = 6,
        name = "Es Jeruk",
        price = 7000.0,
        category_id = 2,
        img = "dummy_product",
        description = "Es jeruk peras",
        stock = 80,
        category = sampleCategories[1]
    ),
    Product(
        id = 7,
        name = "Anyaman Bambu",
        price = 35000.0,
        category_id = 3,
        img = "dummy_product",
        description = "Anyaman bambu halus",
        stock = 5,
        category = sampleCategories[2]
    )
)

// Preview 1: menampilkan semua produk (semua kategori)
@Preview(showBackground = true, name = "Daftar Produk - Semua", showSystemUi = true)
@Composable
fun PreviewDaftarProductAll() {
    JualanTheme(darkTheme = false) {
        StatelessDaftarProduct(
            categories = sampleCategories,
            selectedCategoryId = 1,
            onCategorySelected = {},
            searchQuery = "",
            onSearchQueryChange = {},
            isLoading = false,
            products = sampleProducts,
            onProductClick = {},
            onContactUsClick = {}
        )
    }
}

// Preview 2: filter kategori "Makanan" saja
@Preview(showBackground = true, name = "Kategori Makanan", showSystemUi = true)
@Composable
fun PreviewDaftarProductMakanan() {
    JualanTheme(darkTheme = false) {
        StatelessDaftarProduct(
            categories = sampleCategories,
            selectedCategoryId = 1,
            onCategorySelected = {},
            searchQuery = "",
            onSearchQueryChange = {},
            isLoading = false,
            products = sampleProducts.filter { it.category_id == 1 },
            onProductClick = {},
            onContactUsClick = {}
        )
    }
}

// Preview 3: sedang loading
@Preview(showBackground = true, name = "Loading State", showSystemUi = true)
@Composable
fun PreviewDaftarProductLoading() {
    JualanTheme(darkTheme = false) {
        StatelessDaftarProduct(
            categories = emptyList(),
            selectedCategoryId = null,
            onCategorySelected = {},
            searchQuery = "",
            onSearchQueryChange = {},
            isLoading = true,
            products = emptyList(),
            onProductClick = {},
            onContactUsClick = {}
        )
    }
}
@Preview(showBackground = true, name = "Empty State", showSystemUi = true)
@Composable
fun PreviewDaftarProductEmpty() {
    JualanTheme(darkTheme = false) {
        StatelessDaftarProduct(
            categories = sampleCategories,
            selectedCategoryId = 1,
            onCategorySelected = {},
            searchQuery = "tidakada",
            onSearchQueryChange = {},
            isLoading = false,
            products = emptyList(),
            onProductClick = {},
            onContactUsClick = {}
        )
    }
}