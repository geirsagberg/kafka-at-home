package no.vegvesen.nvdb.kafka.service

import no.vegvesen.nvdb.kafka.api.NvdbApiClient
import no.vegvesen.nvdb.kafka.model.Vegobjekt
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import kotlin.collections.mapNotNull

@Service
class GeometryEnrichmentService(
    private val nvdbApiClient: NvdbApiClient
) {
    private val logger = LoggerFactory.getLogger(GeometryEnrichmentService::class.java)

    /**
     * Enrich a vegobjekt with geometry data from veglenker.
     * Returns a copy of the vegobjekt with enriched stedfesting.
     */
    fun enrichWithGeometry(vegobjekt: Vegobjekt): Vegobjekt {
        val stedfesting = vegobjekt.stedfesting ?: return vegobjekt
        val veglenkesekvensIds = stedfesting.linjer?.mapNotNull { it.veglenkesekvensId }
            ?: return vegobjekt

        if (veglenkesekvensIds.isEmpty()) return vegobjekt

        return try {
            val veglenker = nvdbApiClient.fetchVeglenkerByVeglenkesekvensIdsBlocking(veglenkesekvensIds)

            val geometries = veglenker.mapNotNull { veglenke -> veglenke.geometri?.wkt }

            if (geometries.isEmpty()) {
                logger.debug("No geometries found for vegobjekt {}", vegobjekt.id)
                return vegobjekt
            }

            logger.debug("Enriched vegobjekt {} with {} geometries", vegobjekt.id, geometries.size)

            vegobjekt.copy(
                stedfesting = stedfesting.copy(geometries = geometries)
            )
        } catch (e: Exception) {
            logger.warn("Failed to enrich vegobjekt {} with geometry: {}", vegobjekt.id, e.message)
            vegobjekt
        }
    }
}
