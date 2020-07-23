package dave.dev.krawler.infra.util

class PatternCache(seed: Map<String, Regex> = emptyMap()) {
    private val cache: HashMap<String, Regex> = HashMap(seed)

    fun toRegex(pattern: String): Regex = cache.computeIfAbsent(pattern) { it.toRegex() }
}

fun String.toRegex(patternCache: PatternCache) = patternCache.toRegex(this)
