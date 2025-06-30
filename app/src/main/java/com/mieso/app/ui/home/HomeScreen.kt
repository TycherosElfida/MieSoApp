@file:Suppress("DEPRECATION")

package com.mieso.app.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.mieso.app.data.model.FoodCategory
import com.mieso.app.data.model.MenuItem
import com.mieso.app.data.model.PromoBanner
import com.mieso.app.ui.home.viewmodel.HomeViewModel
import com.mieso.app.ui.navigation.Screen
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp),
        userScrollEnabled = !uiState.isLoading // Disable scroll while shimmering
    ) {
        if (uiState.isLoading) {
            // Shimmering UI when loading
            item { ShimmerWelcomeHeader() }
            item { ShimmerSearchBar() }
            item { ShimmerPromoBanners() }
            item { SectionHeader(title = "Kategori") }
            item { ShimmerCategoryChips() }
            item { SectionHeader(title = "Rekomendasi Untukmu") }
            item { ShimmerMenuItemCarousel() }
        } else if (uiState.error != null) {
            // Error state
            item {
                Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = uiState.error!!)
                }
            }
        } else {
            // Content loaded successfully
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
            item { PromoBanners(banners = uiState.promoBanners) }
            item {
                SectionHeader(title = "Kategori")
                CategoryChips(categories = uiState.categories, onCategoryClick = { categoryId, categoryName ->
                    navController.navigate(Screen.Menu.createRoute(categoryId, categoryName))
                })
            }
            item {
                SectionHeader(title = "Rekomendasi Untukmu")
                MenuItemCarousel(items = uiState.recommendedItems, onItemClick = { menuItemId ->
                    navController.navigate(Screen.MenuItemDetail.createRoute(menuItemId))
                })
            }

            // All Menu Items Section
            item {
                SectionHeader(title = "Menu", modifier = Modifier.padding(top = 16.dp))
            }

            if (uiState.allMenuItems.isEmpty()) {
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
                items(uiState.allMenuItems.chunked(2)) { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowItems.forEach { menuItem ->
                            Box(modifier = Modifier.weight(1f)) {
                                MenuItemCard(item = menuItem, onClick = {
                                    navController.navigate(Screen.MenuItemDetail.createRoute(menuItem.id))
                                })
                            }
                        }
                        if (rowItems.size < 2) {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }
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
fun PromoBanners(banners: List<PromoBanner>) {
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
                val color =
                    if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
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
    categories: List<FoodCategory>,
    onCategoryClick: (categoryId: String, categoryName: String) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 4.dp, horizontal = 16.dp)
    ) {
        items(categories, key = { it.id }) { category ->
            FilterChip(
                selected = false,
                onClick = { onCategoryClick(category.id, category.name) },
                label = { Text(category.name) }
            )
        }
    }
}

@Composable
fun MenuItemCarousel(
    items: List<MenuItem>,
    onItemClick: (menuItemId: String) -> Unit,
    isLoading: Boolean = false
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (isLoading) {
            items(3) {
                Card(
                    modifier = Modifier
                        .width(160.dp)
                        .height(220.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {}
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
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(125.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatToRupiah(item.price),
                    style = MaterialTheme.typography.bodyLarge,
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

// --- Shimmer Placeholder Composables ---

@Composable
private fun ShimmerWelcomeHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .shimmer()
    ) {
        Spacer(
            modifier = Modifier
                .height(30.dp)
                .fillMaxWidth(0.6f)
                .background(Color.Gray)
        )
        Spacer(Modifier.height(8.dp))
        Spacer(
            modifier = Modifier
                .height(20.dp)
                .fillMaxWidth(0.8f)
                .background(Color.Gray)
        )
    }
}

@Composable
private fun ShimmerSearchBar() {
    Spacer(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .height(56.dp)
            .clip(MaterialTheme.shapes.extraLarge)
            .background(Color.Gray)
            .shimmer()
    )
}

@Composable
private fun ShimmerPromoBanners() {
    Spacer(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Gray)
            .shimmer()
    )
}

@Composable
private fun ShimmerCategoryChips() {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .shimmer(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 4.dp, horizontal = 16.dp)
    ) {
        items(5) {
            Spacer(
                modifier = Modifier
                    .width(100.dp)
                    .height(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Gray)
            )
        }
    }
}

@Composable
private fun ShimmerMenuItemCarousel() {
    MenuItemCarousel(items = emptyList(), isLoading = true, onItemClick = {})
}