package xyz.haff.mekachis.fallback

import xyz.haff.mekachis.api.Cache
import xyz.haff.mekachis.api.CacheResult

class FallbackCache<Key, Value>(
    options: Iterable<Cache<Key, Value>?>
): Cache<Key, Value> {
    private val availableOptions = options.filterNotNull()
    private val firstAvailable: Cache<Key, Value> get() = availableOptions.first()

    override suspend fun containsKey(key: Key): Boolean = firstAvailable.containsKey(key)

    override suspend fun get(key: Key): Value? = firstAvailable.get(key)

    override suspend fun put(key: Key, value: Value) = firstAvailable.put(key, value)

    override suspend fun remove(key: Key) = firstAvailable.remove(key)

    override suspend fun clear() = firstAvailable.clear()

    override suspend fun getOrLoad(key: Key, loadingFunction: suspend (Key) -> Value): CacheResult<Value>
        = firstAvailable.getOrLoad(key, loadingFunction)
}