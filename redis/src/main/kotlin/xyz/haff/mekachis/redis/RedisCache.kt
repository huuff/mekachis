package xyz.haff.mekachis.redis

import xyz.haff.mekachis.api.CacheResult
import xyz.haff.mekachis.api.Cache
import xyz.haff.siths.client.SithsDSL
import xyz.haff.siths.protocol.SithsConnectionPool
import xyz.haff.siths.scripts.RedisScripts
import kotlin.time.Duration
import java.util.zip.CRC32

class RedisCache<Key, Value>(
    connectionPool: SithsConnectionPool,
    private val lockTimeout: Duration,
    private val keyTtl: Duration,
    private val name: String = "cache",
    private val serialize: (Value) -> String,
    private val deserialize: (String) -> Value,
) : Cache<Key, Value> {
    private val redis = SithsDSL(connectionPool)

    private fun hash(key: Key) = CRC32().apply { update(key.toString().toByteArray()) }.value.toString()
    private fun keyName(key: Key) = "$name:${hash(key)}"

    override suspend fun getOrLoad(key: Key, loadingFunction: suspend (Key) -> Value): CacheResult<Value> {
        val keyName = keyName(key)

        redis.withLock(keyName, lockTimeout) {
            val storedValue = redis.getOrNull(keyName)
            if (storedValue != null) {
                return CacheResult(deserialize(storedValue), true, hash(key))
            } else {
                val value = loadingFunction(key)
                redis.set(key = keyName, value = serialize(value), timeToLive = keyTtl)
                return CacheResult(value, false, hash(key))
            }
        }
    }

    override suspend fun containsKey(key: Key): Boolean = redis.exists(keyName(key))

    override suspend fun get(key: Key): Value? = redis.getOrNull(keyName(key))?.let(deserialize)

    override suspend fun put(key: Key, value: Value) = redis.set(keyName(key), serialize(value))

    override suspend fun remove(key: Key) { redis.del(keyName(key)) }

    override suspend fun clear() {
        redis.runScript(RedisScripts.PDEL, args = listOf("$name:*"))
    }
}