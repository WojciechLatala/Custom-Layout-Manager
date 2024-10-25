package com.example.dasada.common

fun Int.ceilDiv(y: Int): Int {
    return this.floorDiv(y) + if (this % y == 0) 0 else 1
}
