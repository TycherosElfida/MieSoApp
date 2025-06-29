package com.mieso.app.ui.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.mieso.app.ui.admin.viewmodel.AdminViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditMenuItemScreen(
    navController: NavController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val uiState by viewModel.addEditScreenUiState.collectAsState()
    val categories by viewModel.categories.collectAsState()

//    val imagePickerLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent()
//    ) { uri: Uri? ->
//        uri?.let { viewModel.onImageSelected(it) }
//    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isEditing) "Edit Menu Item" else "Add Menu Item") },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- Form Fields ---
            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChanged,
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = uiState.description,
                onValueChange = viewModel::onDescriptionChanged,
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = uiState.price,
                onValueChange = viewModel::onPriceChanged,
                label = { Text("Price") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // --- Category Dropdown ---
            CategoryDropdown(
                categories = categories,
                selectedCategoryId = uiState.categoryId,
                onCategorySelected = viewModel::onCategoryChanged
            )

            // --- Image Picker ---
            AsyncImage(
                model = uiState.imageUrl, // Gunakan state baru
                contentDescription = "Menu Item Image",
                modifier = Modifier.fillMaxWidth().height(200.dp).align(Alignment.CenterHorizontally),
                contentScale = ContentScale.Crop
            )
            // GANTI TOMBOL DENGAN TEXTFIELD INI
            OutlinedTextField(
                value = uiState.imageUrl,
                onValueChange = viewModel::onImageUrlChanged, // Panggil fungsi baru
                label = { Text("Image URL") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // --- Is Recommended Switch ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Recommended Item", modifier = Modifier.weight(1f))
                Switch(
                    checked = uiState.isRecommended,
                    onCheckedChange = viewModel::onIsRecommendedChanged
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Save Button ---
            Button(
                onClick = {
                    viewModel.saveMenuItem()
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !uiState.isSaving
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("Save Item")
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    categories: List<com.mieso.app.data.model.FoodCategory>,
    selectedCategoryId: String,
    onCategorySelected: (String, String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedCategoryName = categories.find { it.id == selectedCategoryId }?.name ?: "Select Category"

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedCategoryName,
            onValueChange = {},
            readOnly = true,
            label = { Text("Category") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.name) },
                    onClick = {
                        onCategorySelected(category.id, category.name)
                        expanded = false
                    }
                )
            }
        }
    }
}