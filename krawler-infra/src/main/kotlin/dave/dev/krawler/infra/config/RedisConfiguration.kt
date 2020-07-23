package dave.dev.krawler.infra.config

import dave.dev.krawler.infra.util.GzipUtil
import io.lettuce.core.SocketOptions
import io.lettuce.core.cluster.ClusterClientOptions
import io.lettuce.core.event.DefaultEventPublisherOptions
import io.lettuce.core.resource.ClientResources
import io.lettuce.core.resource.DefaultClientResources
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.RedisClusterConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.nio.ByteBuffer
import java.time.Duration

@Configuration
class RedisConfiguration(
    @Value("#{'\${redis.cluster.nodes}'.split(',')}")
    private val nodes: List<String>
) {

    @Primary
    @Bean
    fun lettuceConnectionFactory(clientResources: ClientResources): LettuceConnectionFactory =
        LettuceConnectionFactory(
            RedisClusterConfiguration(nodes).apply {
                maxRedirects = 2
            },
            clientConfig(clientResources)
        )

    @Bean(destroyMethod = "shutdown")
    fun clientResources(): ClientResources = DefaultClientResources.builder()
        .commandLatencyPublisherOptions(
            DefaultEventPublisherOptions.builder()
                .eventEmitInterval(Duration.ofSeconds(10))
                .build()
        )
        .ioThreadPoolSize(8)
        .computationThreadPoolSize(8)
        .build()

    private fun clientConfig(
        clientResources: ClientResources
    ) = LettuceClientConfiguration.builder()
        .commandTimeout(Duration.ofSeconds(1))
        .shutdownTimeout(Duration.ZERO)
        .clientResources(clientResources)
        .clientOptions(
            ClusterClientOptions.builder()
                .socketOptions(
                    SocketOptions.builder()
                        .connectTimeout(Duration.ofSeconds(3))
                        .tcpNoDelay(true).build()
                )
                .build()
        )
        .build()

    @Bean("stringReactiveRedisOperations", "reactiveStringRedisTemplate")
    fun stringReactiveRedisOperations(
        lettuceConnectionFactory: LettuceConnectionFactory
    ): ReactiveRedisOperations<String, String> = ReactiveStringRedisTemplate(lettuceConnectionFactory)

    @Bean
    fun reactiveByteArrayRedisOperations(
        lettuceConnectionFactory: LettuceConnectionFactory
    ): ReactiveRedisOperations<String, ByteArray> = ReactiveByteArrayRedisTemplate(lettuceConnectionFactory)

    class ReactiveByteArrayRedisTemplate(
        connectionFactory: ReactiveRedisConnectionFactory
    ) : ReactiveRedisTemplate<String, ByteArray>(
        connectionFactory,
        RedisSerializationContext
            .newSerializationContext<String, ByteArray>()
            .key(StringRedisSerializer())
            .value(object : RedisSerializer<ByteArray> {
                override fun serialize(bytes: ByteArray?): ByteArray? = bytes
                override fun deserialize(bytes: ByteArray?): ByteArray? = bytes
            })
            .hashKey(RedisSerializer.string())
            .hashValue(RedisSerializer.string())
            .build()
    )

    @Bean
    fun reactiveGzipByteArrayRedisOperations(
        lettuceConnectionFactory: LettuceConnectionFactory
    ): ReactiveRedisOperations<String, ByteArray> = ReactiveGzipByteArrayRedisTemplate(lettuceConnectionFactory)

    class ReactiveGzipByteArrayRedisTemplate(
        connectionFactory: ReactiveRedisConnectionFactory
    ) : ReactiveRedisTemplate<String, ByteArray>(
        connectionFactory,
        RedisSerializationContext
            .newSerializationContext<String, ByteArray>()
            .key(StringRedisSerializer())
            .value(object : RedisSerializer<ByteArray> {
                override fun serialize(bytes: ByteArray?): ByteArray? = bytes?.let { GzipUtil.gzip(it) }
                override fun deserialize(bytes: ByteArray?): ByteArray? = bytes?.let { GzipUtil.ungzip(it) }
            })
            .hashKey(RedisSerializer.string())
            .hashValue(RedisSerializer.string())
            .build()
    )

    @Bean
    fun reactiveLongRedisOperations(
        lettuceConnectionFactory: LettuceConnectionFactory
    ): ReactiveRedisOperations<String, Long> = ReactiveLongRedisTemplate(
        lettuceConnectionFactory
    )

    class ReactiveLongRedisTemplate(
        connectionFactory: ReactiveRedisConnectionFactory
    ) : ReactiveRedisTemplate<String, Long>(
        connectionFactory,
        RedisSerializationContext
            .newSerializationContext<String, Long>()
            .key(StringRedisSerializer())
            .value(object : RedisSerializer<Long> {
                override fun serialize(value: Long?): ByteArray? = value?.let {
                    ByteBuffer.allocate(Long.SIZE_BYTES).putLong(0, it).array()
                }

                override fun deserialize(bytes: ByteArray?): Long? = bytes?.let {
                    ByteBuffer.allocate(Long.SIZE_BYTES).put(it, 0, it.size).apply { flip() }.long
                }
            })
            .hashKey(RedisSerializer.string())
            .hashValue(RedisSerializer.string())
            .build()
    )
}
