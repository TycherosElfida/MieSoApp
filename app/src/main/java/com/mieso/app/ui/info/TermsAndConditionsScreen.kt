package com.mieso.app.ui.info

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsAndConditionsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Syarat & Ketentuan") },
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
                    Selamat datang di MieSo!

                    Harap baca Syarat dan Ketentuan ini dengan saksama sebelum menggunakan aplikasi kami.

                    1. Penerimaan Persyaratan
                    Dengan mengakses atau menggunakan Aplikasi MieSo, Anda setuju untuk terikat oleh Syarat dan Ketentuan ini. Jika Anda tidak setuju dengan bagian mana pun dari persyaratan ini, Anda tidak boleh menggunakan aplikasi.

                    2. Layanan
                    Aplikasi MieSo menyediakan platform online bagi pengguna untuk memesan makanan (mie ayam dan bakso) dari kami untuk diantar, dimakan di tempat, atau diambil sendiri.

                    3. Akun Pengguna
                    Anda bertanggung jawab untuk menjaga kerahasiaan informasi akun Anda, termasuk kata sandi Anda. Anda setuju untuk menerima tanggung jawab atas semua aktivitas yang terjadi di bawah akun Anda.

                    4. Pesanan
                    Semua pesanan tunduk pada ketersediaan. Kami berhak menolak pesanan apa pun karena alasan apa pun. Harga untuk produk kami dapat berubah tanpa pemberitahuan.

                    5. Pembatasan Tanggung Jawab
                    MieSo tidak akan bertanggung jawab atas kerusakan tidak langsung, insidental, khusus, konsekuensial, atau hukuman, termasuk namun tidak terbatas pada, kehilangan keuntungan, data, penggunaan, atau kerugian tidak berwujud lainnya.

                    Terakhir diperbarui: 28 Juni 2025
                    """.trimIndent(),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}