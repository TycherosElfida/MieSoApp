package com.mieso.app.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.mieso.app.data.model.FoodCategory
import com.mieso.app.data.model.MenuItem
import com.mieso.app.data.model.PromoBanner
import com.mieso.app.ui.home.viewmodel.HomeViewModel
import com.mieso.app.ui.navigation.Screen
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.error != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = uiState.error!!)
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item { WelcomeHeader() }
        item {
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable { navController.navigate(Screen.Search.route) }
            ) {
                SearchBar(enabled = false)
            }
        }
        item { PromoBanners(banners = uiState.promoBanners, isLoading = uiState.isLoading) }
        item {
            SectionHeader(title = "Kategori")
            CategoryChips(
                categories = uiState.categories,
                isLoading = uiState.isLoading,
                onCategoryClick = { categoryId ->
                    navController.navigate(Screen.Menu.createRoute(categoryId))
                }
            )
        }
        item {
            SectionHeader(title = "Rekomendasi Untukmu")
            MenuItemCarousel(
                items = uiState.recommendedItems,
                isLoading = uiState.isLoading,
                onItemClick = { menuItemId ->
                    navController.navigate(Screen.MenuItemDetail.createRoute(menuItemId))
                }
            )
        }

        // ==========================================================
        // ===       BAGIAN MENU YANG TELAH DIPERBAIKI            ===
        // ==========================================================
        item {
            SectionHeader(title = "Menu", modifier = Modifier.padding(top = 16.dp))
        }

        if (uiState.isLoading) {
            // Tampilkan placeholder loading jika sedang memuat
            items(2) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        Card(
                            modifier = Modifier.fillMaxWidth().height(220.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {}
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        Card(
                            modifier = Modifier.fillMaxWidth().height(220.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {}
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        } else if (uiState.allMenuItems.isEmpty()) {
            // Tampilkan pesan jika tidak ada menu
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Menu belum tersedia.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            // Tampilkan menu dalam grid 2 kolom
            items(uiState.allMenuItems.chunked(2)) { rowItems ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowItems.forEach { menuItem ->
                        Box(modifier = Modifier.weight(1f)) {
                            MenuItemCard(
                                item = menuItem,
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    navController.navigate(Screen.MenuItemDetail.createRoute(menuItem.id))
                                }
                            )
                        }
                    }
                    // Jika jumlah item ganjil, tambahkan Spacer agar rata kiri
                    if (rowItems.size < 2) {
                        Spacer(Modifier.weight(1f))
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
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
fun SearchBar(modifier: Modifier = Modifier, enabled: Boolean = true) {
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
        readOnly = true,
        enabled = enabled
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PromoBanners(banners: List<PromoBanner>, isLoading: Boolean) {
    if (isLoading) {
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth()
                .height(150.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
        )
        return
    }

    if (banners.isEmpty()) {
        return
    }

    val pagerState = rememberPagerState(pageCount = { banners.size })

    LaunchedEffect(pagerState.pageCount) {
        if (pagerState.pageCount > 1) {
            while (true) {
                delay(4000)
                val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

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
                    contentDescription = "Promo Banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )
            }
        }
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
fun CategoryChips(
    categories: List<com.mieso.app.data.model.FoodCategory>,
    isLoading: Boolean,
    onCategoryClick: (categoryId: String) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 4.dp, horizontal = 16.dp)
    ) {
        if (isLoading) {
            items(5) {
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(40.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                )
            }
        } else {
            items(categories, key = { it.id }) { category ->
                FilterChip(
                    selected = false,
                    onClick = { onCategoryClick(category.id) },
                    label = { Text(category.name) }
                )
            }
        }
    }
}

@Composable
fun MenuItemCarousel(
    items: List<MenuItem>,
    isLoading: Boolean,
    onItemClick: (menuItemId: String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (isLoading) {
            items(3) {
                Card(
                    modifier = Modifier.width(160.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Spacer(modifier = Modifier.height(220.dp))
                }
            }
        } else {
            items(items, key = { it.id }) { item ->
                MenuItemCard(
                    item = item,
                    modifier = Modifier.width(160.dp),
                    onClick = { onItemClick(item.id) }
                )
            }
        }
    }
}

@Composable
fun MenuItemCard(
    item: MenuItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier,
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
                Text(
                    text = formatToRupiah(item.price),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

fun formatToRupiah(price: Long): String {
    val localeID = Locale("in", "ID")
    val formatter = NumberFormat.getCurrencyInstance(localeID)
    formatter.maximumFractionDigits = 0
    return formatter.format(price)
}