package xyz.haff.mekachis.api

import kotlinx.coroutines.runBlocking

interface MeCache<Key, Value> {

    suspend fun getOrLoad(key: Key, loadingFunction: (Key) -> Value): CacheResult<Value>
    fun syncGetOrLoad(key: Key, loadingFunction: (Key) -> Value): CacheResult<Value> = runBlocking {
        getOrLoad(key, loadingFunction)
    }
}