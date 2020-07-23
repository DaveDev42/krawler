package dave.dev.krawler.infra.util

import java.time.Duration
import java.time.LocalDateTime

class MemoizeWithExpiration<T : Any, R : Any>(
    private val duration: Duration,
    private val f: (T) -> R
) : (T) -> R {
    private var expires: LocalDateTime = LocalDateTime.now()

    private lateinit var cachedKey: T
    private lateinit var cachedValue: R
    override fun invoke(x: T): R {
        val now = LocalDateTime.now()
        if (!this::cachedKey.isInitialized || !this::cachedValue.isInitialized || expires.isBefore(now) || cachedKey != x) {
            cachedKey = x
            cachedValue = f(x)
            expires = now + duration
        }
        return cachedValue
    }
}

fun <T : Any, R : Any> ((T) -> R).memoize(duration: Duration): (T) -> R = MemoizeWithExpiration(duration, this)

fun <A : Any, B : Any, R : Any> ((A, B) -> R).memoize(duration: Duration): (A, B) -> R = object : (A, B) -> R {
    private val memoize = MemoizeWithExpiration<Pair<A, B>, R>(duration) { (a, b) -> this@memoize(a, b) }
    override fun invoke(p1: A, p2: B): R = memoize.invoke(Pair(p1, p2))
}

fun <A : Any, B : Any, C : Any, R : Any> ((A, B, C) -> R).memoize(duration: Duration): (A, B, C) -> R = object : (A, B, C) -> R {
    private val memoize = MemoizeWithExpiration<Triple<A, B, C>, R>(duration) { (a, b, c) -> this@memoize(a, b, c) }
    override fun invoke(p1: A, p2: B, p3: C): R = memoize.invoke(Triple(p1, p2, p3))
}
