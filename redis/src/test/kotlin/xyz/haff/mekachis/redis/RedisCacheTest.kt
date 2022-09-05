package xyz.haff.mekachis.redis

import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.testcontainers.LifecycleMode
import io.kotest.extensions.testcontainers.TestContainerExtension
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.mockk.spyk
import io.mockk.verify
import xyz.haff.mekachis.redisClientFromContainer
import xyz.haff.mekachis.shortLivedStringCacheFromContainer
import xyz.haff.siths.client.api.SithsImmediateClient
import xyz.haff.siths.option.SyncMode
import xyz.haff.siths.protocol.RedisConnection
import xyz.haff.siths.protocol.SithsConnectionPool
import java.util.*
import kotlin.time.Duration.Companion.seconds

class RedisCacheTest : FunSpec({
    val container = install(TestContainerExtension("redis:7.0.4-alpine", LifecycleMode.Root)) {
        withExposedPorts(6379)
    }

    lateinit var redis: SithsImmediateClient

    beforeAny {
        redis = redisClientFromContainer(container)
    }


    test("getOrLoad") {
        // ARRANGE
        val cache = shortLivedStringCacheFromContainer(container)
        val loadingFunction = spyk( { UUID.randomUUID().toString() })

        // ACT
        val (firstResult, firstIsHit, firstKeyHash) = cache.getOrLoad("key") { loadingFunction() }
        val (secondResult, secondIsHit, secondKeyHash) = cache.getOrLoad("key") { loadingFunction() }

        // ASSERT
        verify(exactly = 1) { loadingFunction() }

        firstIsHit shouldBe false
        secondIsHit shouldBe true
        firstResult shouldBe secondResult
        firstKeyHash shouldBe secondKeyHash
    }

    test("get and put") {
        // ARRANGE
        val cache = shortLivedStringCacheFromContainer(container)

        // ACT
        cache.put("key", "value")

        // ASSERT
        cache.get("key") shouldBe "value"
    }

    test("remove") {
        // ARRANGE
        val cache = shortLivedStringCacheFromContainer(container)
        cache.put("key", "value")

        // SANITY CHECK
        cache.containsKey("key") shouldBe true

        // ACT
        cache.remove("key")

        // ASSERT
        cache.containsKey("key") shouldBe false
    }

    test("clear") {
        // ARRANGE
        redis.flushDb(SyncMode.SYNC)
        val cache = shortLivedStringCacheFromContainer(container)
        cache.put("key", "value")

        // SANITY CHECK
        cache.containsKey("key") shouldBe true
        redis.dbSize() shouldBeGreaterThan 0

        // ACT
        cache.clear()

        // ASSERT
        redis.dbSize() shouldBe 0
        cache.containsKey("key") shouldBe false
    }

    test("cache of ints") {
        // ARRANGE
        val cache = RedisCache<Int, Int>(
            lockTimeout = 10.seconds,
            keyTtl = 10.seconds,
            name = UUID.randomUUID().toString(),
            connectionPool = SithsConnectionPool(RedisConnection(host = container.host, port = container.firstMappedPort)),
            serialize = Int::toString,
            deserialize = String::toInt,
        )

        // ACT
        cache.put(38, 47)

        // ASSERT
        cache.get(38) shouldBe 47
    }

})
