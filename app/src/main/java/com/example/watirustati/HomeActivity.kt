package com.example.watirustati

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.watirustati.ui.screen.DaftarProductScreen
import com.example.watirustati.ui.screen.DetailProductScreen
import com.example.watirustati.ui.theme.JualanTheme
import com.example.watirustati.ui.viewmodel.ProductViewModel

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JualanTheme {
                val navController = rememberNavController()

                val productViewModel: ProductViewModel = viewModel()

                NavHost(
                    navController = navController,
                    startDestination = "daftar_produk"
                ) {
                    // Halaman Daftar Produk
                    composable(route = "daftar_produk") {
                        DaftarProductScreen(
                            navController = navController,
                            viewModel = productViewModel
                        )
                    }

                    // Halaman Detail Produk
                    composable(
                        route = "detail/{productId}",
                        arguments = listOf(navArgument(name = "productId") {
                            type = NavType.IntType
                        })
                    ) { backStackEntry ->
                        val productId = backStackEntry.arguments?.getInt("productId") ?: 0
                        DetailProductScreen(
                            productId = productId,
                            navController = navController,
                            viewModel = productViewModel
                        )
                    }

                    // Halaman Hubungi Kami (placeholder)
                    composable(route = "hubungi_kami") {
                        Text("Halaman Hubungi Kami")
                    }
                }
            }
        }
    }
}