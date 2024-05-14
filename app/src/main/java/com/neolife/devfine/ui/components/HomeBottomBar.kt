package com.neolife.devfine.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.neolife.devfine.ui.navigation.BottomNavItem

@Composable
fun HomeBottomBar(
    modifier: Modifier = Modifier,
    destinations: List<BottomNavItem>,
    currentDestination: NavDestination?,
    onNavigateDestination: (route: String) -> Unit
) {
    NavigationBar(
        modifier = modifier.height(70.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        destinations.forEach { destination ->
            val selected =
                currentDestination?.hierarchy?.any { it.route == destination.route } == true
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigateDestination(destination.route) },
                icon = { Icon(imageVector = destination.icon, contentDescription = null) })
        }
    }
}