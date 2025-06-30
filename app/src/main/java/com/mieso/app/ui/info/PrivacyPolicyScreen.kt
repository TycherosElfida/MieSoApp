package com.mieso.app.ui.info

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kebijakan Privasi") },
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
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Text(
                    """
                    Kebijakan Privasi ini menjelaskan bagaimana MieSo mengumpulkan, menggunakan, dan mengungkapkan informasi Anda.

                    1. Informasi yang Kami Kumpulkan
                    Kami dapat mengumpulkan informasi pribadi yang Anda berikan kepada kami secara langsung, seperti:
                    - Nama, email, dan informasi kontak lainnya saat Anda membuat akun.
                    - Informasi alamat untuk layanan pengiriman.
                    - Informasi lokasi jika Anda memberikan izin.

                    2. Bagaimana Kami Menggunakan Informasi Anda
                    Kami menggunakan informasi yang kami kumpulkan untuk:
                    - Menyediakan, mengoperasikan, dan memelihara layanan kami.
                    - Memproses transaksi dan pesanan Anda.
                    - Berkomunikasi dengan Anda, termasuk untuk layanan pelanggan.
                    - Memperbaiki dan mempersonalisasi layanan kami.

                    3. Berbagi Informasi
                    Kami tidak membagikan informasi pribadi Anda dengan pihak ketiga kecuali sebagaimana diwajibkan oleh hukum atau untuk menyediakan layanan kami.

                    4. Keamanan
                    Keamanan informasi Anda penting bagi kami, tetapi ingatlah bahwa tidak ada metode transmisi melalui internet atau metode penyimpanan elektronik yang 100% aman.

                    Terakhir diperbarui: 28 Juni 2025
                    """.trimIndent(),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}