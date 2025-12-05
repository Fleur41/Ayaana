package com.sam.ayaana.presentation.screens.privacy

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sam.ayaana.domain.model.PrivacyUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyScreen(
    navController: NavHostController,
    viewModel: PrivacyViewModel = hiltViewModel()
) {
    // CHANGED: Use ViewModel's state instead of local remember
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Privacy Settings",
                        style = MaterialTheme.typography.titleLarge
                        // style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        PrivacySettingsContent(
            modifier = Modifier.padding(paddingValues),
            uiState = uiState,
            onAccountPrivacyChange = { viewModel.updateAccountPrivacy(it) },
            onCloseFriendsChange = { viewModel.updateCloseFriends(it) },
            onCrosspostingChange = { viewModel.updateCrossposting(it) }
        )
    }
}

@Composable
fun PrivacySettingsContent(
    modifier: Modifier = Modifier,
    uiState: PrivacyUiState,
    onAccountPrivacyChange: (Boolean) -> Unit,
    onCloseFriendsChange: (Boolean) -> Unit,
    onCrosspostingChange: (Boolean) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Account Privacy Section
        item {
            Text(
                text = "Account Settings",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        item {
            PrivacyToggleItem(
                title = "Private Account",
                description = "When your account is private, only people you approve can see your photos and videos.",
                isEnabled = uiState.isAccountPrivate, // CHANGED: Use UIState value
                onToggleChange = onAccountPrivacyChange
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // Content Visibility Section
        item {
            Text(
                text = "Who can see your content",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        item {
            PrivacyToggleItem(
                title = "Close Friends",
                description = "Share your content only with close friends.",
                isEnabled = uiState.closeFriendsEnabled, // CHANGED: Use UIState value
                onToggleChange = onCloseFriendsChange
            )
        }

        item {
            PrivacyToggleItem(
                title = "Crossposting",
                description = "Allow sharing to connected apps.",
                isEnabled = uiState.crosspostingEnabled, // CHANGED: Use UIState value
                onToggleChange = onCrosspostingChange
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // Additional Privacy Options
        item {
            Text(
                text = "Additional Privacy",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        item {
            PrivacySettingsItem(
                title = "Blocked Accounts",
                onClick = { /* Navigate to blocked accounts */ }
            )
        }

        item {
            PrivacySettingsItem(
                title = "Restricted Accounts",
                onClick = { /* Navigate to restricted accounts */ }
            )
        }

        item {
            PrivacySettingsItem(
                title = "Muted Accounts",
                onClick = { /* Navigate to muted accounts */ }
            )
        }
    }
}

@Composable
fun PrivacyToggleItem(
    title: String,
    description: String,
    isEnabled: Boolean,
    onToggleChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onToggleChange(!isEnabled) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Switch(
                checked = isEnabled,
                onCheckedChange = onToggleChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

@Composable
fun PrivacySettingsItem(
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Navigate",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}