package com.mieso.app.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mieso.app.data.model.Order
import com.mieso.app.ui.admin.viewmodel.AdminViewModel
import com.mieso.app.ui.home.formatToRupiah
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminManageOrdersScreen(
    navController: NavController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val allOrders by viewModel.allOrders.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var orderToUpdate by remember { mutableStateOf<Order?>(null) }

    if (showDialog && orderToUpdate != null) {
        UpdateStatusDialog(
            order = orderToUpdate!!,
            onDismiss = { showDialog = false },
            onConfirm = { newStatus ->
                // Memastikan ID tidak kosong sebelum mengirim perintah update
                if (orderToUpdate!!.id.isNotBlank()) {
                    viewModel.updateOrderStatus(orderToUpdate!!.id, newStatus)
                }
                showDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage All Orders") },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (allOrders.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(
                        items = allOrders,
                        key = { order -> order.id } // Sekarang dijamin unik
                    ) { order ->
                        AdminOrderCard(
                            order = order,
                            onStatusClick = {
                                // Hanya tampilkan dialog jika ID pesanan valid
                                if (order.id.isNotBlank()) {
                                    orderToUpdate = order
                                    showDialog = true
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminOrderCard(order: Order, onStatusClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.createdAt?.toFormattedDate() ?: "No Date",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                // Tombol status hanya bisa diklik jika ID valid
                OrderStatusChip(
                    status = order.status,
                    onClick = onStatusClick,
                    enabled = order.id.isNotBlank()
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Menampilkan ID Dokumen yang sebenarnya
            Text(
                text = "Document ID: ${order.id}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            // ... Sisa kode card tidak berubah ...
            Text("Customer Info:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Text(text = "User ID: ${order.userId}", style = MaterialTheme.typography.bodyMedium)
            order.shippingAddress?.let {
                Text(
                    text = "${it.label}: ${it.addressLine1}, ${it.city}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("Items:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            order.items.forEach { cartItem ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = "${cartItem.quantity}x ${cartItem.menuItem.name}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = formatToRupiah(cartItem.menuItem.price * cartItem.quantity),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total: ", style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = formatToRupiah(order.total),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrderStatusChip(status: String, onClick: () -> Unit, enabled: Boolean) {
    val containerColor = when (status.lowercase()) {
        "pending" -> MaterialTheme.colorScheme.tertiaryContainer
        "completed" -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = when (status.lowercase()) {
        "pending" -> MaterialTheme.colorScheme.onTertiaryContainer
        "completed" -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    InputChip(
        selected = false,
        onClick = onClick,
        label = { Text(status, style = MaterialTheme.typography.labelMedium) },
        colors = InputChipDefaults.inputChipColors(
            containerColor = containerColor,
            labelColor = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.5f)
        ),
        border = null,
        enabled = enabled
    )
}

@Composable
private fun UpdateStatusDialog(order: Order, onDismiss: () -> Unit, onConfirm: (newStatus: String) -> Unit) {
    val nextStatus = if (order.status.equals("Pending", ignoreCase = true)) "Completed" else "Pending"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ubah Status Pesanan") },
        text = {
            // Tampilkan ID Dokumen yang valid di dialog
            Text("Apakah Anda yakin ingin mengubah status pesanan dengan ID: ${order.id} menjadi \"$nextStatus\"?")
        },
        confirmButton = {
            Button(onClick = { onConfirm(nextStatus) }) {
                Text("Konfirmasi")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@Composable
private fun BoxScope.EmptyState() {
    Column(
        modifier = Modifier
            .padding(horizontal = 32.dp)
            .align(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
            contentDescription = "No Orders",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.surfaceVariant
        )
        Text(
            text = "Belum Ada Pesanan",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Saat ada pesanan baru dari pelanggan, pesanan tersebut akan muncul di sini.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun Date.toFormattedDate(): String {
    val sdf = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("id", "ID"))
    return sdf.format(this)
}
