package xyz.haff.mekachis.redis

import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.testcontainers.LifecycleMode
import io.kotest.extensions.testcontainers.TestContainerExtension
import io.kotest.matchers.shouldBe
import io.mockk.spyk
import io.mockk.verify
import xyz.haff.mekachis.redisClientFromContainer
import xyz.haff.mekachis.shortLivedStringCacheFromContainer
import xyz.haff.siths.client.api.SithsImmediateClient
import java.util.*

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

})
