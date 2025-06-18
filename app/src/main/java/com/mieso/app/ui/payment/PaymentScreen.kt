package com.mieso.app.ui.payment

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Money
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mieso.app.data.model.CartItem
import com.mieso.app.data.model.UserAddress
import com.mieso.app.ui.home.formatToRupiah
import com.mieso.app.ui.navigation.Screen
import com.mieso.app.ui.payment.viewmodel.PaymentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    navController: NavController,
    viewModel: PaymentViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // This LaunchedEffect block listens for the result of the order placement.
    LaunchedEffect(key1 = uiState.orderPlacementResult) {
        val result = uiState.orderPlacementResult
        if (result != null) {
            if (result.isSuccess) {
                Toast.makeText(context, "Pesanan berhasil dibuat!", Toast.LENGTH_LONG).show()
                // On success, navigate to the Orders screen, clearing the back stack.
                navController.navigate(Screen.Orders.route) {
                    popUpTo(Screen.Home.route) { inclusive = false }
                }
            } else {
                val errorMessage = result.exceptionOrNull()?.message ?: "Gagal membuat pesanan."
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            }
            // Reset the result so the effect doesn't run again on configuration change.
            viewModel.consumeOrderPlacementResult()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Konfirmasi Pesanan") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            PaymentFooter(
                totalPrice = uiState.total,
                isLoading = uiState.isPlacingOrder,
                onPlaceOrderClicked = { viewModel.placeOrder() }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                AddressSection(address = uiState.selectedAddress)
            }
            item {
                SectionHeader(title = "Ringkasan Pesanan")
                Card {
                    Column(Modifier.padding(vertical = 8.dp)) {
                        uiState.cartItems.forEach { cartItem ->
                            OrderItemRow(item = cartItem)
                            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                        }
                    }
                }
            }
            item {
                PaymentMethodSection()
            }
            item {
                PaymentSummarySection(
                    subtotal = uiState.subtotal,
                    deliveryFee = uiState.deliveryFee,
                    total = uiState.total
                )
            }
        }
    }
}


@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun AddressSection(address: UserAddress?) {
    SectionHeader(title = "Alamat Pengiriman")
    Card {
        if (address != null) {
            Row(Modifier.padding(16.dp)) {
                Icon(Icons.Outlined.Home, contentDescription = "Address", modifier = Modifier.padding(end = 16.dp))
                Column {
                    Text(address.label, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text(address.addressLine1, style = MaterialTheme.typography.bodyMedium)
                    Text("${address.city}, ${address.postalCode}", style = MaterialTheme.typography.bodyMedium)
                }
            }
        } else {
            Text("Alamat tidak tersedia.", modifier = Modifier.padding(16.dp))
        }
    }
}

@Composable
private fun OrderItemRow(item: CartItem) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("${item.quantity}x ${item.menuItem.name}")
        Text(formatToRupiah(item.quantity * item.menuItem.price))
    }
}

@Composable
private fun PaymentMethodSection() {
    SectionHeader(title = "Metode Pembayaran")
    Card {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.Money, contentDescription = "Payment Method", modifier = Modifier.padding(end = 16.dp))
            Text("Cash on Delivery (COD)", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun PaymentSummarySection(subtotal: Long, deliveryFee: Long, total: Long) {
    SectionHeader(title = "Rincian Pembayaran")
    Card {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SummaryRow(label = "Subtotal", value = formatToRupiah(subtotal))
            SummaryRow(label = "Ongkos Kirim", value = formatToRupiah(deliveryFee))
            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            SummaryRow(label = "Total Pembayaran", value = formatToRupiah(total), isTotal = true)
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String, isTotal: Boolean = false) {
    val fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = fontWeight)
        Text(value, fontWeight = fontWeight)
    }
}

@Composable
private fun PaymentFooter(totalPrice: Long, isLoading: Boolean, onPlaceOrderClicked: () -> Unit) {
    Surface(shadowElevation = 8.dp) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Total Bayar", style = MaterialTheme.typography.bodyMedium)
                Text(
                    formatToRupiah(totalPrice),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Button(
                onClick = onPlaceOrderClicked,
                enabled = !isLoading,
                modifier = Modifier.height(50.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Bayar Sekarang", fontSize = 16.sp)
                }
            }
        }
    }
}