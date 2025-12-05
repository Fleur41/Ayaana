package com.sam.ayaana.presentation.screens.hashtag

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sam.ayaana.navigation.NavigationDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HashtagScreen(
    navController: androidx.navigation.NavHostController? = null,
    viewModel: HashtagViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    var inputText by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        NavigationDestination.HashtagGenerator.title,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge,

                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController?.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
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
        ) {
            // Header
            Text(
                text = "AI Hashtag Generator",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Generate relevant hashtags for your posts",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Input Section with Clear Icon
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Describe your post:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // TextField with Clear Icon
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("e.g., Beach sunset with friends...") },
                            maxLines = 3,
                            shape = MaterialTheme.shapes.medium,
                            trailingIcon = {
                                if (inputText.isNotEmpty()) {
                                    IconButton(
                                        onClick = {
                                            inputText = ""
                                            focusManager.clearFocus()
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Clear text",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Clear Button (only when there's text)
                        if (inputText.isNotEmpty()) {
                            OutlinedButton(
                                onClick = {
                                    inputText = ""
                                    focusManager.clearFocus()
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Clear")
                            }
                        }

                        // Generate Button
                        Button(
                            onClick = {
                                if (inputText.isNotBlank()) {
                                    viewModel.generateHashtags(inputText)
                                    focusManager.clearFocus()
                                }
                            },
                            modifier = Modifier.weight(if (inputText.isEmpty()) 1f else 1f),
                            enabled = inputText.isNotBlank() && !uiState.isLoading
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Generating...")
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Tag,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Generate")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Results Section
            if (uiState.hashtags.isNotEmpty()) {
                // Results Header with Copy All button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Suggested Hashtags (${uiState.hashtags.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )

                    // Copy All Button
                    TextButton(
                        onClick = {
                            val allHashtags = uiState.hashtags.joinToString(" ") { it.tag }
                            clipboardManager.setText(AnnotatedString(allHashtags))

                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy all",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy All")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.hashtags) { hashtag ->
                        HashtagItem(
                            hashtag = hashtag,
                            onCopyClick = { tag ->
                                clipboardManager.setText(AnnotatedString(tag))
                                viewModel.copyToClipboard(tag)
                                // Show snackbar: "Copied to clipboard!"
                            }
                        )
                    }
                }
            } else if (uiState.generatedText.isNotEmpty() && !uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tag,
                            contentDescription = "No results",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No hashtags generated",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Try a different description",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            // Loading/Empty State
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Analyzing your post...")
                    }
                }
            } else if (inputText.isEmpty() && uiState.hashtags.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Get started",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "AI Hashtag Generator",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Describe your post to get hashtag suggestions",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Examples:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "• Beach sunset with friends\n• Delicious food at restaurant\n• Gym workout session\n• Travel adventure mountains",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            // Show copy feedback (Add snackbar in real implementation)
            uiState.copiedHashtag?.let { copiedTag ->
                LaunchedEffect(copiedTag) {
                    // TODO: Replace with proper snackbar
                    // For now, just log
                    println("Copied: $copiedTag")
                    kotlinx.coroutines.delay(2000)
                    viewModel.clearError()
                }
            }

            // Error handling
            uiState.error?.let { error ->
                AlertDialog(
                    onDismissRequest = { viewModel.clearError() },
                    title = { Text("Error") },
                    text = { Text(error) },
                    confirmButton = {
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }
}
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun HashtagScreen(
//    navController: androidx.navigation.NavHostController? = null,
//    viewModel: HashtagViewModel = hiltViewModel()
//) {
//    val uiState by viewModel.uiState.collectAsState()
//    val clipboardManager = LocalClipboardManager.current
//    var inputText by remember { mutableStateOf("") }
//
//    Scaffold(
//        topBar = {
//            CenterAlignedTopAppBar(
//                title = {
//                    Text(
//                        NavigationDestination.HashtagGenerator.title,
//                        fontWeight = FontWeight.Bold
//                    )
//                },
//                navigationIcon = {
//                    IconButton(onClick = { navController?.navigateUp() }) {
//                        Icon(
//                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                            contentDescription = "Back"
//                        )
//                    }
//                }
//            )
//        }
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .padding(16.dp)
//        ) {
//            // Header
//            Text(
//                text = "AI Hashtag Generator",
//                style = MaterialTheme.typography.headlineMedium,
//                fontWeight = FontWeight.Bold
//            )
//            Spacer(modifier = Modifier.height(4.dp))
//            Text(
//                text = "Generate relevant hashtags for your posts",
//                style = MaterialTheme.typography.bodyMedium,
//                color = MaterialTheme.colorScheme.onSurfaceVariant
//            )
//
//            Spacer(modifier = Modifier.height(24.dp))
//
//            // Input Section
//            Card(
//                modifier = Modifier.fillMaxWidth(),
//                colors = CardDefaults.cardColors(
//                    containerColor = MaterialTheme.colorScheme.surfaceVariant
//                )
//            ) {
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(16.dp)
//                ) {
//                    Text(
//                        text = "Describe your post:",
//                        style = MaterialTheme.typography.titleMedium,
//                        fontWeight = FontWeight.Medium
//                    )
//
//                    Spacer(modifier = Modifier.height(8.dp))
//
//                    OutlinedTextField(
//                        value = inputText,
//                        onValueChange = { inputText = it },
//                        modifier = Modifier.fillMaxWidth(),
//                        placeholder = { Text("e.g., Beach sunset with friends...") },
//                        maxLines = 3,
//                        shape = MaterialTheme.shapes.medium
//                    )
//
//                    Spacer(modifier = Modifier.height(16.dp))
//
//                    Button(
//                        onClick = {
//                            if (inputText.isNotBlank()) {
//                                viewModel.generateHashtags(inputText)
//                            }
//                        },
//                        modifier = Modifier.fillMaxWidth(),
//                        enabled = inputText.isNotBlank() && !uiState.isLoading
//                    ) {
//                        if (uiState.isLoading) {
//                            CircularProgressIndicator(
//                                modifier = Modifier.size(20.dp),
//                                color = MaterialTheme.colorScheme.onPrimary
//                            )
//                            Spacer(modifier = Modifier.width(8.dp))
//                            Text("Generating...")
//                        } else {
//                            Icon(
//                                imageVector = Icons.Default.Tag,
//                                contentDescription = null,
//                                modifier = Modifier.size(20.dp)
//                            )
//                            Spacer(modifier = Modifier.width(8.dp))
//                            Text("Generate Hashtags")
//                        }
//                    }
//                }
//            }
//
//            Spacer(modifier = Modifier.height(24.dp))
//
//            // Results Section
//            if (uiState.hashtags.isNotEmpty()) {
//                Text(
//                    text = "Suggested Hashtags (${uiState.hashtags.size})",
//                    style = MaterialTheme.typography.titleMedium,
//                    fontWeight = FontWeight.Medium
//                )
//
//                Spacer(modifier = Modifier.height(12.dp))
//
//                LazyColumn(
//                    verticalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    items(uiState.hashtags) { hashtag ->
//                        HashtagItem(
//                            hashtag = hashtag,
//                            onCopyClick = { tag ->
//                                clipboardManager.setText(AnnotatedString(tag))
//                                viewModel.copyToClipboard(tag)
//                                // Show snackbar in real implementation
//                            }
//                        )
//                    }
//                }
//            } else if (uiState.generatedText.isNotEmpty() && !uiState.isLoading) {
//                Box(
//                    modifier = Modifier.fillMaxSize(),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Text(
//                        text = "No hashtags generated. Try a different description.",
//                        color = MaterialTheme.colorScheme.onSurfaceVariant
//                    )
//                }
//            }
//
//            // Show copy feedback
//            uiState.copiedHashtag?.let { copiedTag ->
//                LaunchedEffect(copiedTag) {
//                    // In real app, show a snackbar
//                    // For now, we'll just clear after delay
//                    kotlinx.coroutines.delay(2000)
//                    viewModel.clearError()
//                }
//            }
//
//            // Error handling
//            uiState.error?.let { error ->
//                AlertDialog(
//                    onDismissRequest = { viewModel.clearError() },
//                    title = { Text("Error") },
//                    text = { Text(error) },
//                    confirmButton = {
//                        TextButton(onClick = { viewModel.clearError() }) {
//                            Text("OK")
//                        }
//                    }
//                )
//            }
//        }
//    }
//}

@Composable
fun HashtagItem(
    hashtag: com.sam.ayaana.domain.model.Hashtag,
    onCopyClick: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = hashtag.tag,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Category: ${hashtag.category}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = { onCopyClick(hashtag.tag) }
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy hashtag"
                )
            }
        }
    }
}