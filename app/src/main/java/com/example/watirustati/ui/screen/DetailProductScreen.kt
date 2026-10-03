package com.example.watirustati.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.watirustati.R
import com.example.watirustati.data.model.Category
import com.example.watirustati.data.model.Product
import com.example.watirustati.ui.theme.JualanTheme
import com.example.watirustati.ui.viewmodel.ProductUiState
import com.example.watirustati.ui.viewmodel.ProductViewModel
import com.example.watirustati.util.JualanConstants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailProductScreen(
    productId: Int,
    navController: NavController?,
    viewModel: ProductViewModel = viewModel()
) {
    val context = LocalContext.current
    var quantity by rememberSaveable { mutableStateOf(1) }
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is ProductUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is ProductUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
            }
        }

        is ProductUiState.Success -> {
            val product = state.products.find { it.id == productId }

            if (product == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Produk tidak ditemukan.")
                }
            } else {
                StatelessDetailProduct(
                    product = product,
                    quantity = quantity,
                    onQuantityChange = { quantity = it },
                    onBackClick = { navController?.popBackStack() },
                    onAddToCartClick = {
                        Toast.makeText(context, "Membeli sebanyak $quantity", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatelessDetailProduct(
    product: Product?,
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    onBackClick: () -> Unit,
    onAddToCartClick: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Detail Produk") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        if (product != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(28.dp))

                val imageModel: Any = if (product.img == "dummy_product") {
                    R.drawable.dummy_product
                } else {
                    "${JualanConstants.BASE_URL}img/${product.img}"
                }

                Box(modifier = Modifier.fillMaxWidth()) {
                    AsyncImage(
                        model = imageModel,
                        contentDescription = product.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Column(modifier = Modifier.padding(horizontal = 16.dp)) {

                    // Badge kategori
                    if (product.category != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = product.category.name,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Text(
                        product.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        "Rp ${product.price}",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Deskripsi",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        product.description ?: "",
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        "Stok Tersedia: ${product.stock}",
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Jumlah Beli", color = MaterialTheme.colorScheme.onBackground)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FilledTonalIconButton(
                                onClick = { if (quantity > 1) onQuantityChange(quantity - 1) },
                                enabled = quantity > 1
                            ) { Text("-") }

                            Text(
                                quantity.toString(),
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            FilledTonalIconButton(
                                onClick = { if (quantity < product.stock) onQuantityChange(quantity + 1) },
                                enabled = quantity < product.stock
                            ) { Text("+") }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onAddToCartClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = product.stock > 0 && quantity > 0
                    ) {
                        Text("Tambah ke Keranjang")
                    }
                }
            }
        }
    }
}


// Sample product lengkap (semua field wajib diisi)
private val sampleProduct = Product(
    id = 1,
    name = "Batik Purbalingga",
    price = 150000.0,
    category_id = 3,
    img = "dummy_product",
    description = "Batik khas Purbalingga dengan motif tradisional.",
    stock = 10,
    category = Category(
        id = 3,
        name = "Kerajinan",
        description = "Aneka kerajinan tangan",
        products_count = 5
    )
)

// Preview 1: kondisi normal — produk ada, stok banyak
@Preview(showBackground = true, name = "Detail Produk - Normal", showSystemUi = true)
@Composable
fun PreviewDetailProductNormal() {
    JualanTheme(darkTheme = false) {
        StatelessDetailProduct(
            product = sampleProduct,
            quantity = 1,
            onQuantityChange = {},
            onBackClick = {},
            onAddToCartClick = {}
        )
    }
}

// Preview 1b: kondisi normal dark mode (disamakan dengan contoh)
@Preview(showBackground = true, name = "Detail Produk - Normal Dark", showSystemUi = true)
@Composable
fun PreviewDetailProductNormalDark() {
    JualanTheme(darkTheme = true) {
        StatelessDetailProduct(
            product = sampleProduct,
            quantity = 1,
            onQuantityChange = {},
            onBackClick = {},
            onAddToCartClick = {}
        )
    }
}

// Preview 2: produk dengan stok habis (tombol disabled)
@Preview(showBackground = true, name = "Detail Produk - Stok Habis", showSystemUi = true)
@Composable
fun PreviewDetailProductOutOfStock() {
    JualanTheme(darkTheme = false) {
        StatelessDetailProduct(
            product = sampleProduct.copy(stock = 0),
            quantity = 1,
            onQuantityChange = {},
            onBackClick = {},
            onAddToCartClick = {}
        )
    }
}

// Preview 3: quantity sudah diubah jadi 5
@Preview(showBackground = true, name = "Detail Produk - Quantity 5", showSystemUi = true)
@Composable
fun PreviewDetailProductQuantity5() {
    JualanTheme(darkTheme = false) {
        StatelessDetailProduct(
            product = sampleProduct,
            quantity = 5,
            onQuantityChange = {},
            onBackClick = {},
            onAddToCartClick = {}
        )
    }
}

// Preview 4: produk null (tidak ditemukan)
@Preview(showBackground = true, name = "Detail Produk - Null", showSystemUi = true)
@Composable
fun PreviewDetailProductNull() {
    JualanTheme(darkTheme = false) {
        StatelessDetailProduct(
            product = null,
            quantity = 1,
            onQuantityChange = {},
            onBackClick = {},
            onAddToCartClick = {}
        )
    }
}