package com.example.freshtrack.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.freshtrack.ui.ProductsViewModel
import com.example.freshtrack.ui.components.MainTab
import com.example.freshtrack.ui.screens.HomeScreen
import com.example.freshtrack.ui.screens.ProductFormScreen
import com.example.freshtrack.ui.screens.ProductNotFoundScreen
import com.example.freshtrack.ui.screens.ProductsScreen
import com.example.freshtrack.ui.screens.StatsScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val viewModel: ProductsViewModel = viewModel(factory = ProductsViewModel.Factory)

    val onTabSelected: (MainTab) -> Unit = { tab ->
        navController.navigate(Routes.forTab(tab)) {
            popUpTo(Routes.HOME)
            launchSingleTop = true
        }
    }

    NavHost(navController = navController, startDestination = Routes.HOME) {

        composable(Routes.HOME) {
            val summary by viewModel.homeState.collectAsStateWithLifecycle()
            val lastAction by viewModel.lastAction.collectAsStateWithLifecycle()

            HomeScreen(
                summary = summary,
                lastAction = lastAction,
                onTabSelected = onTabSelected,
                onAddProduct = { navController.navigate(Routes.ADD) },
                onEditProduct = { navController.navigate(Routes.edit(it)) },
                onMarkUsed = viewModel::markUsed,
                onMarkDiscarded = viewModel::markDiscarded,
                onUndo = viewModel::undoLastAction,
                onUndoDismissed = viewModel::dismissLastAction,
                onLoadSamples = viewModel::loadSamples
            )
        }

        composable(Routes.PRODUCTS) {
            val state by viewModel.listState.collectAsStateWithLifecycle()
            val query by viewModel.query.collectAsStateWithLifecycle()
            val lastAction by viewModel.lastAction.collectAsStateWithLifecycle()

            ProductsScreen(
                state = state,
                query = query,
                lastAction = lastAction,
                onQueryChange = viewModel::setQuery,
                onFilterChange = viewModel::setFilter,
                onSortChange = viewModel::setSort,
                onClearFilters = viewModel::clearFilters,
                onTabSelected = onTabSelected,
                onAddProduct = { navController.navigate(Routes.ADD) },
                onEditProduct = { navController.navigate(Routes.edit(it)) },
                onMarkUsed = viewModel::markUsed,
                onMarkDiscarded = viewModel::markDiscarded,
                onUndo = viewModel::undoLastAction,
                onUndoDismissed = viewModel::dismissLastAction
            )
        }

        composable(Routes.STATS) {
            val stats by viewModel.statsState.collectAsStateWithLifecycle()

            StatsScreen(stats = stats, onTabSelected = onTabSelected)
        }

        composable(Routes.ADD) {
            val today by viewModel.today.collectAsStateWithLifecycle()

            ProductFormScreen(
                product = null,
                today = today,
                onBack = { navController.popBackStack() },
                onValidate = viewModel::validate,
                onSave = { name, category, quantity, expiryEpochDay ->
                    viewModel.saveProduct(null, name, category, quantity, expiryEpochDay)
                },
                onDelete = {}
            )
        }

        composable(
            route = Routes.EDIT,
            arguments = listOf(navArgument(Routes.ARG_PRODUCT_ID) { type = NavType.StringType })
        ) { entry ->
            val productId = entry.arguments?.getString(Routes.ARG_PRODUCT_ID).orEmpty()
            val product = remember(productId) { viewModel.productById(productId) }
            val today by viewModel.today.collectAsStateWithLifecycle()

            if (product == null) {
                ProductNotFoundScreen(onBack = { navController.popBackStack() })
            } else {
                ProductFormScreen(
                    product = product,
                    today = today,
                    onBack = { navController.popBackStack() },
                    onValidate = viewModel::validate,
                    onSave = { name, category, quantity, expiryEpochDay ->
                        viewModel.saveProduct(productId, name, category, quantity, expiryEpochDay)
                    },
                    onDelete = {
                        viewModel.deleteProduct(productId)
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
