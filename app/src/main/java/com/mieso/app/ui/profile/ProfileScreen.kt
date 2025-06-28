package com.mieso.app.ui.profile

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.mieso.app.data.auth.UserData
import com.mieso.app.ui.navigation.Screen
import com.mieso.app.ui.profile.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val user by viewModel.user.collectAsState()

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
                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (uiState.userData != null) {
                    ProfileHeader(userData = uiState.userData!!)
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

            item { HorizontalDivider(thickness = 8.dp, color = MaterialTheme.colorScheme.surfaceVariant) }

            // Admin Menu Button
            if (user?.role == "admin") {
                item {
                    ProfileMenuSection(title = "Admin") {
                        ProfileMenuItem(
                            text = "Manage Menu",
                            icon = Icons.Outlined.RestaurantMenu,
                            onClick = { navController.navigate(Screen.AdminMenu.route) }
                        )
                        ProfileMenuItem(
                            text = "Manage Categories",
                            icon = Icons.Outlined.Category,
                            onClick = { navController.navigate(Screen.AdminCategories.route) }
                        )
                        ProfileMenuItem(
                            text = "Manage Promo Banners",
                            icon = Icons.Outlined.Campaign,
                            onClick = { navController.navigate(Screen.AdminPromoBanners.route) }
                        )
                    }
                }
            }

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

            // Tombol Keluar
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Button(
                        onClick = viewModel::signOut,
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

@Composable
private fun ProfileHeader(userData: UserData) {
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
            Text(
                text = userData.username ?: "Pengguna MieSo",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            userData.email?.let { email ->
                Text(
                    text = email,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            // Tampilkan ID Pengguna
            Text(
                text = "ID: ${userData.userId}",
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