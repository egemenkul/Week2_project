package com.example.week2_assignment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DevClusterScreen(modifier: Modifier = Modifier) {
    // Ana veri: değişince ekran güncellenir. Silme/stok düşme bu listeyi yeniden oluşturarak yapılır.
    var hardwareList by remember { mutableStateOf(SampleData) }

    // Ekran döndürülünce kaybolmaması için rememberSaveable
    var searchQuery by rememberSaveable { mutableStateOf("") }

    // Turnike: sadece searchQuery (veya liste) değişince çalışır
    val filteredList = remember(searchQuery, hardwareList) {
        if (searchQuery.isBlank()) {
            hardwareList
        } else {
            hardwareList.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.category.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    // Toplam harcanan ödenek: cost x stockCount toplamı
    val totalCost = remember(hardwareList) {
        hardwareList.sumOf { it.cost * it.stockCount }
    }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Toplam harcanan ödenek: $${"%,.2f".format(totalCost)}",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(12.dp))

        HardwareSearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it }
        )
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filteredList, key = { it.id }) { item ->
                HardwareCard(
                    item = item,
                    onClick = {
                        // Geçici davranış: tıklayınca stok 1 azalır (copy ile yeni nesne üretilir)
                        hardwareList = hardwareList.map {
                            if (it.id == item.id && it.stockCount > 0) {
                                it.copy(stockCount = it.stockCount - 1)
                            } else it
                        }
                    },
                    onDelete = {
                        hardwareList = hardwareList.filter { it.id != item.id }
                    }
                )
            }
        }
    }
}