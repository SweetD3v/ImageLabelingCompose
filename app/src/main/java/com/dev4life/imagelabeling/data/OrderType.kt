package com.dev4life.imagelabeling.data

sealed class OrderType {
    object Ascending : OrderType()
    object Descending : OrderType()
}