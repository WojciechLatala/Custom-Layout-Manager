package com.example.dasada.recyclerview.diffutil

interface DiffUtilListItem {

    val itemId: Int
    fun isTheSameItemAs(other: DiffUtilListItem) = this::class == other::class && this.itemId == other.itemId
    fun hasSameContentAs(other: DiffUtilListItem) = this == other
}
