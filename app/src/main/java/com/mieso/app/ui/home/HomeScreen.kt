// ---------------------------------------------------------------------------------------------
// FILE: app/src/main/java/com/mieso/app/ui/home/HomeScreen.kt
// ---------------------------------------------------------------------------------------------
// EXPLANATION: This is the main UI file for the Home Screen. It's a "stateful" composable
// because it holds a reference to the ViewModel. It observes the UI state and passes
// data down to "stateless" child composables.

package com.mieso.app.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.mieso.app.ui.home.state.FoodCategory
import com.mieso.app.ui.home.state.MenuItem
import com.mieso.app.ui.home.state.PromoBanner
import com.mieso.app.ui.home.viewmodel.HomeViewModel
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    // Collect the UI state from the ViewModel in a lifecycle-aware manner.
    val uiState by viewModel.uiState.collectAsState()

    // The main layout for the screen. LazyColumn is used for efficient scrolling.
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Welcome Header
        item {
            WelcomeHeader()
        }

        // Search Bar
        item {
            SearchBar(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
        }

        // Promo Banners Section
        item {
            PromoBanners(
                banners = uiState.promoBanners,
                isLoading = uiState.isLoading
            )
        }

        // Categories Section
        item {
            SectionHeader(title = "Kategori")
            CategoryChips(
                categories = uiState.categories,
                isLoading = uiState.isLoading,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // Recommended Items Section
        item {
            SectionHeader(title = "Rekomendasi Untukmu")
            MenuItemCarousel(
                items = uiState.recommendedItems,
                isLoading = uiState.isLoading
            )
        }
    }
}

// --- Reusable Child Composables ---

@Composable
fun WelcomeHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Selamat Datang!",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Mau makan apa hari ini?",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SearchBar(modifier: Modifier = Modifier) {
    TextField(
        value = "",
        onValueChange = {},
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text("Cari mie ayam, bakso...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
        shape = MaterialTheme.shapes.extraLarge,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        readOnly = true // Making it read-only, tap would navigate to a dedicated search screen
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PromoBanners(banners: List<PromoBanner>, isLoading: Boolean) {
    val pagerState = rememberPagerState(pageCount = { banners.size })

    // Auto-scroll effect
    LaunchedEffect(pagerState.pageCount) {
        while (true) {
            delay(4000) // Wait for 4 seconds
            val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
            pagerState.animateScrollToPage(nextPage)
        }
    }

    if (isLoading) {
        // Shimmer loading placeholder
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth()
                .height(150.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
        )
    } else {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
            ) { page ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    AsyncImage(
                        model = banners[page].imageUrl,
                        contentDescription = "Promo Banner ${banners[page].id}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                    )
                }
            }
            // Page Indicator
            Row(
                Modifier
                    .height(24.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pagerState.pageCount) { iteration ->
                    val color = if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

@Composable
fun CategoryChips(categories: List<FoodCategory>, isLoading: Boolean, modifier: Modifier = Modifier) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        if (isLoading) {
            items(5) { // Show 5 shimmer placeholders
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(40.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                )
            }
        } else {
            items(categories) { category ->
                FilterChip(
                    selected = false,
                    onClick = { /* TODO: Navigate to category screen */ },
                    label = { Text(category.name) }
                )
            }
        }
    }
}

@Composable
fun MenuItemCarousel(items: List<MenuItem>, isLoading: Boolean) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (isLoading) {
            items(3) { // Show 3 shimmer placeholders
                Card(
                    modifier = Modifier.width(160.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Spacer(modifier = Modifier.height(220.dp))
                }
            }
        } else {
            items(items) { item ->
                MenuItemCard(item)
            }
        }
    }
}

@Composable
fun MenuItemCard(item: MenuItem) {
    Card(
        onClick = { /* TODO: Navigate to item detail */ },
        modifier = Modifier.width(160.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = item.name, style = MaterialTheme.typography.titleSmall, maxLines = 1)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = item.price, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
