package dave.dev.krawler.infra.util

import org.funktionale.either.Either
import reactor.core.publisher.Mono

fun <T> Mono<T>.toEither(): Mono<Either<Throwable, T>> = materialize().map<Either<Throwable, T>> { signal ->
    signal.throwable?.let {
        return@map Either.Left(it)
    }
    when (val value = signal.get()) {
        else -> Either.Right(value)
    }
}

fun <T> Either<Throwable, T>.toMono(): Mono<T> = when (this) {
    is Either.Left -> Mono.error(l)
    is Either.Right -> Mono.just(r)
}
