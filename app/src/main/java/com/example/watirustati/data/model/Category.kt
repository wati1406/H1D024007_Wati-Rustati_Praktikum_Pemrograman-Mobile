package com.example.watirustati.data.model

data class Category(
    val id: Int,
    val name: String,
    val description:String?,
    val products_count: Int?
)

data class Product(
    val id: Int,
    val category_id: Int = 0,
    val category: Category? = null,
    val name: String,
    val description: String? = null,
    val price: Double,
    val stock: Int = 0,
    val img: String = "dummy_product"
)
