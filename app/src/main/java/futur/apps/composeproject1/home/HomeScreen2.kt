package com.anouar.myscreens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import futur.apps.composeproject1.home.CategoryScoreSection

@Preview
@Composable
fun HomeScreen2() {

    Column(
        modifier = Modifier.padding(4.dp)
    ) {
       // HomeTopBar()
        InfoSection()
        CategoryScoreSection()
    }
}


