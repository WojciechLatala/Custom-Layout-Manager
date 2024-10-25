package com.example.dasada.model

import com.example.dasada.recyclerview.diffutil.DiffUtilListItem

data class ListItemViewModel(override val itemId: Int, val itemName: String): DiffUtilListItem
