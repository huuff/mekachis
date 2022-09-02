package xyz.haff.mekachis.api

import kotlinx.coroutines.runBlocking

interface Cache<Key, Value> {

    suspend fun containsKey(key: Key): Boolean
    suspend fun get(key: Key): Value?
    suspend fun put(key: Key, value: Value)
    suspend fun remove(key: Key)
    suspend fun clear()
    suspend fun getOrLoad(key: Key, loadingFunction: suspend (Key) -> Value): CacheResult<Value>
}