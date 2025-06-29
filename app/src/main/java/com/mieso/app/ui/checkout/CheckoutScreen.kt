package com.mieso.app.ui.checkout

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.DeliveryDining
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mieso.app.ui.checkout.state.ServiceType
import com.mieso.app.ui.checkout.viewmodel.CheckoutViewModel
import com.mieso.app.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    navController: NavController,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pilih Metode Layanan") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ServiceTypeCard(
                    title = "Delivery",
                    description = "Pesanan akan diantar ke lokasimu.",
                    icon = Icons.Outlined.DeliveryDining,
                    isSelected = uiState.selectedServiceType == ServiceType.DELIVERY,
                    onClick = { viewModel.onServiceTypeSelected(ServiceType.DELIVERY) }
                )
                ServiceTypeCard(
                    title = "Dine-In",
                    description = "Makan di tempat, pesan dari mejamu.",
                    icon = Icons.Outlined.Restaurant,
                    isSelected = uiState.selectedServiceType == ServiceType.DINE_IN,
                    onClick = { viewModel.onServiceTypeSelected(ServiceType.DINE_IN) }
                )
                ServiceTypeCard(
                    title = "Take Away",
                    description = "Ambil sendiri pesananmu di restoran.",
                    icon = Icons.Outlined.ShoppingBag,
                    isSelected = uiState.selectedServiceType == ServiceType.TAKE_AWAY,
                    onClick = { viewModel.onServiceTypeSelected(ServiceType.TAKE_AWAY) }
                )
            }

            Button(
                onClick = {
                    when (uiState.selectedServiceType) {
                        ServiceType.DELIVERY -> {
                            navController.navigate(Screen.DeliveryDetails.route)
                        }
                        ServiceType.DINE_IN -> {
                            //belom-an
                        }
                        ServiceType.TAKE_AWAY -> {
                            navController.navigate(Screen.Payment.route)
                        }
                        ServiceType.NONE -> {

                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = uiState.selectedServiceType != ServiceType.NONE
            ) {
                Text("Lanjutkan")
            }
        }
    }
}

@Composable
private fun ServiceTypeCard(
    title: String,
    description: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(
                width = 2.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                shape = MaterialTheme.shapes.large
            ),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = title, modifier = Modifier.size(40.dp))
            Spacer(Modifier.width(16.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.titleLarge)
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
