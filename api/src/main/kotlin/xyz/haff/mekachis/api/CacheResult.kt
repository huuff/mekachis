package xyz.haff.mekachis.api

data class CacheResult<Value>(val contents: Value, val hit: Boolean, val keyIdentifier: String)
