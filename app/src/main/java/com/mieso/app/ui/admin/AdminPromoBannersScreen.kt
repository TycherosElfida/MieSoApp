package com.mieso.app.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.mieso.app.data.model.PromoBanner
import com.mieso.app.ui.admin.viewmodel.AdminViewModel
import com.mieso.app.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPromoBannersScreen(
    navController: NavController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val banners by viewModel.promoBanners.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Promo Banners") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(Screen.AddEditPromoBanner.createRoute())
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Promo Banner")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(banners) { banner ->
                AdminPromoBannerItem(
                    banner = banner,
                    onEditClick = {
                        navController.navigate(Screen.AddEditPromoBanner.createRoute(banner.id))
                    },
                    onDeleteClick = { viewModel.deletePromoBanner(banner.id) }
                )
            }
        }
    }
}

@Composable
fun AdminPromoBannerItem(
    banner: PromoBanner,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = banner.imageUrl,
                contentDescription = "Promo Banner Image",
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Order: ${banner.order}", fontWeight = FontWeight.Bold)
                Text("Target: ${banner.targetScreen}", maxLines = 1)
            }
            IconButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
            }
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}