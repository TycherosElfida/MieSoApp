package com.mieso.app.ui.info

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpCenterScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pusat Bantuan") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
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
                Text("Frequently Asked Questions (FAQ)", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(8.dp))
            }
            item {
                HelpItem(
                    question = "Bagaimana cara memesan?",
                    answer = "Untuk memesan, pilih menu yang Anda inginkan dari halaman utama atau kategori, tambahkan ke keranjang, lalu lanjutkan ke proses checkout untuk menyelesaikan pesanan."
                )
            }
            item {
                HelpItem(
                    question = "Metode pembayaran apa saja yang diterima?",
                    answer = "Saat ini kami hanya menerima metode pembayaran Cash on Delivery (COD). Kami sedang berupaya untuk menambahkan metode pembayaran online lainnya di masa mendatang."
                )
            }
            item {
                HelpItem(
                    question = "Bagaimana cara mengubah atau membatalkan pesanan?",
                    answer = "Untuk saat ini, pembatalan atau perubahan pesanan belum dapat dilakukan melalui aplikasi. Silakan hubungi kami langsung melalui kontak yang tersedia untuk bantuan lebih lanjut."
                )
            }
            item {
                Spacer(Modifier.height(16.dp))
                Text("Hubungi Kami", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(8.dp))
                Text("Jika Anda memiliki pertanyaan lain, jangan ragu untuk menghubungi kami di:")
                Text("Email: support@mieso.app", fontWeight = FontWeight.Bold)
                Text("Telepon: 0812-3456-7890", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun HelpItem(question: String, answer: String) {
    Column {
        Text(question, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text(answer, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}