package com.mieso.app.ui.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Dns
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mieso.app.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(navController: NavController) {
    val adminMenuItems = listOf(
        AdminDashboardItem(
            title = "Manage Menu",
            description = "Atur semua menu makanan dan minuman.",
            icon = Icons.Outlined.RestaurantMenu,
            screen = Screen.AdminMenu
        ),

        AdminDashboardItem(
            title = "Manage Orders",
            description = "Lihat dan kelola semua pesanan masuk.",
            icon = Icons.Outlined.Dns, // Ikon yang merepresentasikan data/pesanan
            screen = Screen.AdminManageOrders
        ),
        AdminDashboardItem(
            title = "Manage Categories",
            description = "Kelola kategori untuk menu.",
            icon = Icons.Outlined.Category,
            screen = Screen.AdminCategories
        ),
        AdminDashboardItem(
            title = "Manage Promo Banners",
            description = "Ubah dan perbarui banner promosi.",
            icon = Icons.Outlined.Campaign,
            screen = Screen.AdminPromoBanners
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(adminMenuItems) { item ->
                AdminDashboardCard(item = item) {
                    navController.navigate(item.screen.route)
                }
            }
        }
    }
}

/**
 * Composable untuk menampilkan satu kartu item di dashboard admin.
 */
@Composable
private fun AdminDashboardCard(
    item: AdminDashboardItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Data class untuk merepresentasikan setiap item di dashboard.
 */
private data class AdminDashboardItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val screen: Screen
)
