package dave.dev.krawler.infra.util

import assertk.Assert
import assertk.assertions.isInstanceOf
import org.funktionale.either.Either

fun <A, B> Assert<Either<A, B>>.isLeft(): Assert<Either.Left<A, B>> =
    this.isInstanceOf(Either.Left::class).transform {
        @Suppress("UNCHECKED_CAST")
        (it as Either.Left<A, B>)
    }

fun <A, B> Assert<Either<A, B>>.isRight(): Assert<Either.Right<A, B>> =
    this.isInstanceOf(Either.Right::class).transform {
        @Suppress("UNCHECKED_CAST")
        (it as Either.Right<A, B>)
    }

fun <T> Assert<Either.Left<T, *>>.unwrapLeft(): Assert<T> = transform { it.l }

fun <T> Assert<Either.Right<*, T>>.unwrapRight(): Assert<T> = transform { it.r }
