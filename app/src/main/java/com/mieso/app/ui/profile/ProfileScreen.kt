// File: app/src/main/java/com/mieso/app/ui/profile/ProfileScreen.kt

package com.mieso.app.ui.profile

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.mieso.app.MainActivity
import com.mieso.app.data.model.User
import com.mieso.app.ui.navigation.Screen
import com.mieso.app.ui.profile.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    // Menggunakan state 'user' secara langsung dari ViewModel. Ini lebih ringkas.
    val user by viewModel.user.collectAsState()

    // State untuk mengontrol dialog logout
    var showLogoutDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Menampilkan dialog jika 'showLogoutDialog' bernilai true
    if (showLogoutDialog) {
        LogoutConfirmationDialog(
            onConfirmLogout = {
                showLogoutDialog = false
                viewModel.signOut()

                // Logika untuk memulai ulang aplikasi
                val intent = Intent(context, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                (context as? Activity)?.finish()
            },
            onDismiss = {
                showLogoutDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Profil Saya") })
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                // Menampilkan header profil jika data user sudah tersedia
                if (user == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    // Menggunakan Composable ProfileHeader yang sudah diperbarui
                    ProfileHeader(userData = user!!)
                }
            }

            item { HorizontalDivider(thickness = 8.dp, color = MaterialTheme.colorScheme.surfaceVariant) }

            // Menu Akun
            item {
                ProfileMenuSection(title = "Akun Saya") {
                    ProfileMenuItem(
                        text = "Alamat Tersimpan",
                        icon = Icons.Outlined.LocationOn,
                        onClick = { navController.navigate(Screen.ManageAddresses.route) }
                    )
                    ProfileMenuItem(
                        text = "Riwayat Transaksi",
                        icon = Icons.Outlined.Discount,
                        onClick = { navController.navigate(Screen.Orders.route) }
                    )
                }
            }

            // Integrasi Admin Dashboard
            if (user?.role == "admin") {
                item {
                    HorizontalDivider(thickness = 8.dp, color = MaterialTheme.colorScheme.surfaceVariant)
                    ProfileMenuSection(title = "Panel Admin") {
                        ProfileMenuItem(
                            text = "Admin Dashboard",
                            icon = Icons.Outlined.AdminPanelSettings,
                            onClick = { navController.navigate(Screen.AdminDashboard.route) }
                        )
                    }
                }
            }

            item { HorizontalDivider(thickness = 8.dp, color = MaterialTheme.colorScheme.surfaceVariant) }

            // Menu Informasi
            item {
                ProfileMenuSection(title = "Informasi & Bantuan") {
                    ProfileMenuItem(
                        text = "Pusat Bantuan",
                        icon = Icons.AutoMirrored.Outlined.HelpOutline,
                        onClick = { navController.navigate(Screen.HelpCenter.route) }
                    )
                    ProfileMenuItem(
                        text = "Syarat & Ketentuan",
                        icon = Icons.Outlined.Description,
                        onClick = { navController.navigate(Screen.TermsAndConditions.route) }
                    )
                    ProfileMenuItem(
                        text = "Kebijakan Privasi",
                        icon = Icons.Outlined.Shield,
                        onClick = { navController.navigate(Screen.PrivacyPolicy.route) }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            // Tombol Keluar dengan logika dialog
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Button(
                        onClick = { showLogoutDialog = true }, // Menampilkan dialog
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Keluar",
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text("Keluar")
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

// Composable untuk dialog logout
@Composable
private fun LogoutConfirmationDialog(
    onConfirmLogout: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Konfirmasi Keluar") },
        text = { Text(text = "Apakah Anda yakin ingin keluar dari akun Anda?") },
        confirmButton = {
            Button(
                onClick = onConfirmLogout,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Keluar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

// ProfileHeader yang sudah disesuaikan dengan model User
@Composable
private fun ProfileHeader(userData: User) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = userData.profilePictureUrl,
            contentDescription = "Foto Profil",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
//            Text(
//                text = userData.username ?: "Pengguna MieSo",
//                style = MaterialTheme.typography.titleLarge,
//                fontWeight = FontWeight.Bold
//            )
            userData.email?.let { email ->
                Text(text = email) // Style bisa disesuaikan lagi jika perlu
            }
            Text(
                text = "ID: ${userData.id}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}


@Composable
private fun ProfileMenuSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
        )
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileMenuItem(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = { Text(text, style = MaterialTheme.typography.bodyLarge) },
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingContent = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null
            )
        },
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
    HorizontalDivider(modifier = Modifier.padding(start = 16.dp, end = 16.dp))
}
