package no.vegvesen.nvdb.kafka.model

import kotlinx.serialization.Serializable

@Serializable
data class Vegobjekt(
    val vegobjektId: Long,
    val vegobjektType: Int,
    val egenskaper: Map<Int, String>,
    val stedfestinger: List<Utstrekning>,
)

@Serializable
data class Utstrekning(
    val veglenkesekvensId: Long,
    val startposisjon: Double,
    val sluttposisjon: Double,
)

@Serializable
data class VegobjektDelta(
    val before: Vegobjekt?,
    val after: Vegobjekt?,
)
