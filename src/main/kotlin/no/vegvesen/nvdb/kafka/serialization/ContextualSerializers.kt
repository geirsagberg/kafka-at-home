package no.vegvesen.nvdb.kafka.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import java.net.URI

object URISerializer : KSerializer<URI> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("URI", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: URI) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): URI {
        return URI.create(decoder.decodeString())
    }
}

/**
 * Shared SerializersModule for all JSON configurations.
 * Registers contextual serializers for types marked with @Contextual in generated code.
 */
val contextualSerializersModule = SerializersModule {
    contextual(URI::class, URISerializer)
}
