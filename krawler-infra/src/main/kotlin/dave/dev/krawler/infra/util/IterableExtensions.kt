package dave.dev.krawler.infra.util

inline fun <T> Iterable<T>.sumByLong(selector: (T) -> Long): Long = fold(0L) { acc, t -> acc + selector(t) }
fun Iterable<Long>.sumByLong(): Long = fold(0L) { acc, t -> acc + t }

fun <T : Pair<K, V>, K, V> Iterable<T>.groupBy(): Map<K, List<V>> =
    this.groupBy({ (key, _) -> key }, { (_, value) -> value })
