package com.mieso.app.ui.profile

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
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
import com.mieso.app.ui.navigation.AdminGraph

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    val updateResult by viewModel.updateResult.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }

    var username by remember(user?.username) { mutableStateOf(user?.username ?: "") }
    var profilePictureUrl by remember(user?.profilePictureUrl) { mutableStateOf(user?.profilePictureUrl ?: "") }

    val context = LocalContext.current

    LaunchedEffect(updateResult) {
        updateResult?.let { result ->
            if (result.isSuccess) {
                Toast.makeText(context, "Profil berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                isEditing = false
            } else {
                val errorMessage = result.exceptionOrNull()?.message ?: "Gagal memperbarui profil."
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
            viewModel.consumeUpdateResult()
        }
    }

    if (showLogoutDialog) {
        LogoutConfirmationDialog(
            onConfirmLogout = {
                showLogoutDialog = false
                viewModel.signOut()
                val intent = Intent(context, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                (context as? Activity)?.finish()
            },
            onDismiss = { showLogoutDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil Saya") },
                actions = {
                    if (user != null) {
                        IconButton(onClick = {
                            if (isEditing) {
                                viewModel.updateProfile(username, profilePictureUrl)
                            } else {
                                isEditing = true
                            }
                        }) {
                            Icon(
                                imageVector = if (isEditing) Icons.Default.Done else Icons.Default.Edit,
                                contentDescription = if (isEditing) "Simpan" else "Edit"
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                if (user == null) {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    ProfileHeader(
                        userData = user!!,
                        isEditing = isEditing,
                        username = username,
                        profilePictureUrl = profilePictureUrl,
                        onUsernameChange = { username = it },
                        onProfilePictureUrlChange = { profilePictureUrl = it }
                    )
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
                        icon = Icons.AutoMirrored.Outlined.ReceiptLong,
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
                            onClick = { navController.navigate(AdminGraph.route) }
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

            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Button(
                        onClick = { showLogoutDialog = true },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
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
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
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

@Composable
private fun ProfileHeader(
    userData: User,
    isEditing: Boolean,
    username: String,
    profilePictureUrl: String,
    onUsernameChange: (String) -> Unit,
    onProfilePictureUrlChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AsyncImage(
            model = if (isEditing) profilePictureUrl.ifBlank { userData.profilePictureUrl } else userData.profilePictureUrl,
            contentDescription = "Foto Profil",
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(96.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant)
        )

        if (isEditing) {
            OutlinedTextField(
                value = username,
                onValueChange = onUsernameChange,
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = profilePictureUrl,
                onValueChange = onProfilePictureUrlChange,
                label = { Text("URL Gambar Profil") },
                placeholder = { Text("https://example.com/image.png") },
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = userData.username ?: "Pengguna MieSo",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                userData.email?.let { email ->
                    Text(text = email)
                }
            }
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