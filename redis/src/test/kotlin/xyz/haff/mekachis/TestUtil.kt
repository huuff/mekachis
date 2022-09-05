package xyz.haff.mekachis

import org.testcontainers.containers.Container
import xyz.haff.mekachis.redis.RedisCache
import xyz.haff.siths.client.SithsDSL
import xyz.haff.siths.protocol.RedisConnection
import xyz.haff.siths.protocol.SithsConnectionPool
import java.util.*
import kotlin.time.Duration.Companion.seconds

fun shortLivedStringCacheFromContainer(container: Container<*>) = RedisCache<String, String>(
    lockTimeout = 10.seconds,
    keyTtl = 10.seconds,
    name = UUID.randomUUID().toString(),
    connectionPool = SithsConnectionPool(RedisConnection(host = container.host, port = container.firstMappedPort)),
    serialize = { it },
    deserialize = { it }
)

fun redisClientFromContainer(container: Container<*>) = SithsDSL(
    SithsConnectionPool(RedisConnection(host = container.host, port = container.firstMappedPort))
)