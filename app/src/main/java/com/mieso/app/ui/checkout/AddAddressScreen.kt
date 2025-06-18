package com.mieso.app.ui.checkout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mieso.app.ui.checkout.viewmodel.CheckoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAddressScreen(
    navController: NavController,
    viewModel: CheckoutViewModel = hiltViewModel() // Use the shared ViewModel
) {
    // Local state for the form fields
    var label by remember { mutableStateOf("") }
    var addressLine1 by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var postalCode by remember { mutableStateOf("") }

    val isFormValid by remember(label, addressLine1, city, postalCode) {
        derivedStateOf {
            label.isNotBlank() && addressLine1.isNotBlank() && city.isNotBlank() && postalCode.isNotBlank()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Alamat Baru") },
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
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Label Alamat (Contoh: Rumah, Kantor)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = addressLine1,
                    onValueChange = { addressLine1 = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Alamat") }
                )
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Kota / Kabupaten") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = postalCode,
                    onValueChange = { postalCode = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Kode Pos") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            Button(
                onClick = {
                    viewModel.saveNewAddress(label, addressLine1, city, postalCode)
                    navController.popBackStack() // Go back to the previous screen
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = isFormValid
            ) {
                Text("Simpan Alamat")
            }
        }
    }
}
