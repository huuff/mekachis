package xyz.haff.mekachis.api

/**
 * A cache implementation that never caches anything. For testing purposes or as a fallback so that no errors are thrown
 */
// TODO: Test? There's not much to test tho
class NoOpCache<Key, Value> : Cache<Key, Value> {
    override suspend fun containsKey(key: Key): Boolean = false

    override suspend fun get(key: Key): Value? = null

    override suspend fun put(key: Key, value: Value) {}

    override suspend fun remove(key: Key) {  }

    override suspend fun clear() {  }

    override suspend fun getOrLoad(key: Key, loadingFunction: suspend (Key) -> Value): CacheResult<Value>
        = CacheResult(
            contents = loadingFunction(key),
            hit = false,
            keyIdentifier = key.hashCode().toString(),
        )
}