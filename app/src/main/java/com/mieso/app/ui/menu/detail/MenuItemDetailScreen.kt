package com.mieso.app.ui.menu.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.mieso.app.ui.home.formatToRupiah
import com.mieso.app.ui.menu.detail.viewmodel.MenuItemDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuItemDetailScreen(
    navController: NavController,
    viewModel: MenuItemDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = uiState.menuItem?.name ?: "Detail") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            // The "Add to Cart" button is placed in the bottom bar for easy access.
            AddToCartFooter(
                price = uiState.totalPrice,
                onAddToCart = { viewModel.onAddToCartClicked() }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.error != null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = uiState.error!!)
            }
        } else if (uiState.menuItem != null) {
            val item = uiState.menuItem!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()) // Make the content scrollable
            ) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                )

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = item.name, style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "A placeholder description for the delicious ${item.name}. This section would contain more details about the ingredients and preparation.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                    QuantitySelector(
                        quantity = uiState.quantity,
                        onQuantityChanged = viewModel::onQuantityChanged
                    )
                }
            }
        }
    }
}

@Composable
fun QuantitySelector(
    quantity: Int,
    onQuantityChanged: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Jumlah", style = MaterialTheme.typography.titleMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedIconButton(onClick = { onQuantityChanged(quantity - 1) }) {
                Text("-", fontSize = 20.sp)
            }
            Text(
                text = "$quantity",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            OutlinedIconButton(onClick = { onQuantityChanged(quantity + 1) }) {
                Text("+", fontSize = 20.sp)
            }
        }
    }
}

@Composable
fun AddToCartFooter(
    price: Long,
    onAddToCart: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Total Harga", style = MaterialTheme.typography.bodySmall)
                Text(
                    text = formatToRupiah(price),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Button(onClick = onAddToCart, modifier = Modifier.height(50.dp)) {
                Text("Tambah ke Keranjang", fontSize = 16.sp)
            }
        }
    }
}
