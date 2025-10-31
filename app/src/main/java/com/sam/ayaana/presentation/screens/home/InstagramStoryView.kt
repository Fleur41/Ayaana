package com.sam.ayaana.presentation.screens.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.sam.ayaana.R
import com.sam.ayaana.settings.SettingsViewModel


@Composable
fun IgStoryView() {
    val stories = remember { (1..100).toList() }
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = stories,
            key = {itemId -> itemId}
        ){ itemId ->
            StoryItemIG(
                itemId = itemId,
                onItemClick = {
                    Log.d("TAG", "InstagramStoryView: $itemId")
                }
            )
        }
    }
}
// https://picsum.photos/id/{id}/200/300
@Composable
fun StoryItemIG (
    modifier: Modifier = Modifier,
    itemId: Int,
    onItemClick: () -> Unit
) {
    val gradientColors = listOf(Color.Red, Color.Yellow, Color.Green)

    Box (modifier = modifier
        .clip(CircleShape)
        .size(60.dp)
        .border(
            width = 2.dp, brush = Brush.linearGradient(colors = gradientColors), shape = CircleShape
        )
        .padding(4.dp)
        .background(Color.LightGray, CircleShape)
        .clickable { onItemClick() },
        contentAlignment = Alignment.Center
    ){
        AsyncImage(
            modifier = Modifier.fillMaxSize().clip(CircleShape),
            model = "https://picsum.photos/id/${itemId}/200/300",
            contentDescription = null,
            contentScale = ContentScale.Crop,
            placeholder =  painterResource(R.drawable.placeholder),
            fallback = painterResource(R.drawable.placeholder)
//            onError = {
//                Log.d("TAG", "StoryItem: $itemId result: ${it.result.throwable.message}");
//            }
        )
//        Text(
//            text = itemId.toString(),
//            style = MaterialTheme.typography.titleMedium
//        )
    }
}

@Preview
@Composable
private fun StoryItemIGPreview() {
    StoryItemIG(
        itemId = 1,
        onItemClick = {}
    )
}
@Preview
@Composable
private fun IgStoryViewPreview() {
    IgStoryView()
}


//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun HomeScreen(
//    settingsViewModel: SettingsViewModel = hiltViewModel()
//) {
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text(text = "Instagram Story View") },
//                actions = {
//                    IconButton(
//                        onClick = {
//                            settingsViewModel.logout()
//                        }){
//                        Icon(
//                            painter = painterResource(R.drawable.ic_logout),
//                            contentDescription = null
//                        )
//                    }
//                }
//            )
//        }
//    ) { innerPadding ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(innerPadding),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//
//
//            Button(onClick = { /* Handle button click */ }) {
//                Text(text = "Home")
//            }
//        }
//    }
//}