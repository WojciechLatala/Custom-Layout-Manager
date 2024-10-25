package com.example.dasada.repository

import com.example.dasada.model.ListItemViewModel

class ItemProvider {

    // generic items for now, would fetch items from backend/database etc.
    fun getItems(itemCount: Int): List<ListItemViewModel> = if (itemCount > 0) (1..itemCount).map(::createListItemViewModel) else emptyList()

    fun addItem(itemsList: List<ListItemViewModel>): List<ListItemViewModel> =
        if (itemsList.isEmpty())
            getItems(1)
        else
            itemsList.plus(createListItemViewModel(itemsList.last().itemId + 1))

    private fun createListItemViewModel(index: Int) = ListItemViewModel(index, "item $index")
}
