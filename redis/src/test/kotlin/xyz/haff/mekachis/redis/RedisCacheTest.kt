package xyz.haff.mekachis.redis

import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.testcontainers.LifecycleMode
import io.kotest.extensions.testcontainers.TestContainerExtension
import io.kotest.matchers.shouldBe
import io.mockk.spyk
import io.mockk.verify
import xyz.haff.siths.client.SithsPool
import java.time.Duration
import java.util.*

class RedisCacheTest : FunSpec({
    val container = install(TestContainerExtension("redis:7.0.4-alpine", LifecycleMode.Root)) {
        withExposedPorts(6379)
    }

    test("value is cached") {
        // ARRANGE
        val cache = RedisCache<String, String>(
            lockTimeout = Duration.ofSeconds(10),
            keyTtl = Duration.ofSeconds(10),
            redisConnectionPool = SithsPool(host = container.host, port = container.firstMappedPort),
            serializingFunction = { it },
            deserializingFunction = { it }
        )
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

})