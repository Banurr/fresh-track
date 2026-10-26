package com.example.freshtrack.ui.navigation

import com.example.freshtrack.ui.components.MainTab

object Routes {
    const val ARG_PRODUCT_ID = "productId"

    const val HOME = "home"
    const val PRODUCTS = "products"
    const val STATS = "stats"
    const val ADD = "product/add"
    const val EDIT = "product/edit/{$ARG_PRODUCT_ID}"

    fun edit(productId: String) = "product/edit/$productId"

    fun forTab(tab: MainTab): String = when (tab) {
        MainTab.HOME -> HOME
        MainTab.PRODUCTS -> PRODUCTS
        MainTab.STATS -> STATS
    }
}
