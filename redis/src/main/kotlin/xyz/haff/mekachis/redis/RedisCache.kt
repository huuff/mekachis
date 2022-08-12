package xyz.haff.mekachis.redis

import xyz.haff.mekachis.api.CacheResult
import xyz.haff.mekachis.api.Cache
import xyz.haff.siths.client.SithsDSL
import xyz.haff.siths.client.SithsPool
import java.time.Duration
import java.util.zip.CRC32

class RedisCache<Key, Value>(
    redisConnectionPool: SithsPool,
    private val lockTimeout: Duration,
    private val keyTtl: Duration,
    private val name: String = "cache",
    private val serializingFunction: (Value) -> String,
    private val deserializingFunction: (String) -> Value,
) : Cache<Key, Value> {
    private val redis = SithsDSL(redisConnectionPool)

    override suspend fun getOrLoad(key: Key, loadingFunction: (Key) -> Value): CacheResult<Value> {
        val keyHash = CRC32().apply { update(key.toString().toByteArray()) }.value.toString()
        val keyName = "$name:$keyHash"

        redis.withLock(keyName, lockTimeout) {
            val storedValue = redis.getOrNull(keyName)
            if (storedValue != null) {
                return CacheResult(deserializingFunction(storedValue), true, keyHash)
            } else {
                val value = loadingFunction(key)
                redis.set(key = keyName, value = serializingFunction(value), timeToLive = keyTtl)
                return CacheResult(value, false, keyHash)
            }
        }
    }
}