package no.geirsagberg.kafkaathome.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Represents a road link sequence (Veglenkesekvens) from NVDB Uberiket API.
 * A road link sequence is a continuous sequence of road links that together form
 * a logical road segment.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class Veglenkesekvens(
    @JsonProperty("veglenkesekvensid")
    val veglenkesekvensId: Long,

    @JsonProperty("startdato")
    val startdato: String? = null,

    @JsonProperty("sluttdato")
    val sluttdato: String? = null,

    @JsonProperty("kortform")
    val kortform: String? = null,

    @JsonProperty("veglenker")
    val veglenker: List<Veglenke> = emptyList(),

    @JsonProperty("geometri")
    val geometri: Geometri? = null
)

/**
 * Represents a single road link (Veglenke) from the NVDB vegnett API.
 * Road links are the atomic segments that make up the road network.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class Veglenke(
    @JsonProperty("veglenkesekvensId")
    val veglenkesekvensId: Long? = null,

    @JsonProperty("veglenkenummer")
    val veglenkenummer: Long? = null,

    @JsonProperty("gyldighetsperiode")
    val gyldighetsperiode: Gyldighetsperiode? = null,

    @JsonProperty("konnektering")
    val konnektering: Boolean? = null,

    @JsonProperty("topologiniva")
    val topologiniva: String? = null,

    @JsonProperty("maledato")
    val maledato: String? = null,

    @JsonProperty("malemetode")
    val malemetode: String? = null,

    @JsonProperty("detaljniva")
    val detaljniva: String? = null,

    @JsonProperty("typeVeg")
    val typeVeg: String? = null,

    @JsonProperty("startnode")
    val startnode: Long? = null,

    @JsonProperty("sluttnode")
    val sluttnode: Long? = null,

    @JsonProperty("startposisjon")
    val startposisjon: Double? = null,

    @JsonProperty("sluttposisjon")
    val sluttposisjon: Double? = null,

    @JsonProperty("kommune")
    val kommune: Int? = null,

    @JsonProperty("geometri")
    val geometri: Geometri? = null,

    @JsonProperty("lengde")
    val lengde: Double? = null,

    @JsonProperty("feltoversikt")
    val feltoversikt: List<String>? = null
)

/**
 * Represents geometry data in WKT or other formats.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class Geometri(
    @JsonProperty("wkt")
    val wkt: String? = null,

    @JsonProperty("srid")
    val srid: Int? = null,

    @JsonProperty("lengde")
    val lengde: Double? = null,

    @JsonProperty("datafangstdato")
    val datafangstdato: String? = null,

    @JsonProperty("temakode")
    val temakode: Int? = null,

    @JsonProperty("kommune")
    val kommune: Int? = null,

    @JsonProperty("oppdateringsdato")
    val oppdateringsdato: String? = null,

    @JsonProperty("kvalitet")
    val kvalitet: GeometriKvalitet? = null
)

/**
 * Represents quality metadata for geometry data.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class GeometriKvalitet(
    @JsonProperty("malemetode")
    val malemetode: Int? = null,

    @JsonProperty("malemetodeHoyde")
    val malemetodeHoyde: Int? = null,

    @JsonProperty("noyaktighet")
    val noyaktighet: Int? = null,

    @JsonProperty("noyaktighetHoyde")
    val noyaktighetHoyde: Int? = null,

    @JsonProperty("synbarhet")
    val synbarhet: Int? = null,

    @JsonProperty("maksimaltAvvik")
    val maksimaltAvvik: Int? = null
)

/**
 * Represents a road object (Vegobjekt) from NVDB Uberiket API.
 * Road objects are features placed along roads, such as speed limits, signs, etc.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class Vegobjekt(
    @JsonProperty("id")
    val id: Long,

    @JsonProperty("typeId")
    val typeId: Int? = null,

    @JsonProperty("versjon")
    val versjon: Int? = null,

    @JsonProperty("gyldighetsperiode")
    val gyldighetsperiode: Gyldighetsperiode? = null,

    @JsonProperty("egenskaper")
    val egenskaper: Map<String, EgenskapValue>? = null,

    @JsonProperty("barn")
    val barn: Map<String, Any>? = null,

    @JsonProperty("stedfesting")
    val stedfesting: Stedfesting? = null,

    @JsonProperty("geometri")
    val geometri: Geometri? = null,

    @JsonProperty("sistEndret")
    val sistEndret: String? = null
)

/**
 * Represents the validity period of a road object.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class Gyldighetsperiode(
    @JsonProperty("startdato")
    val startdato: String? = null,

    @JsonProperty("sluttdato")
    val sluttdato: String? = null
)

/**
 * Represents a property/attribute of a road object.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class Egenskap(
    @JsonProperty("id")
    val id: Long? = null,

    @JsonProperty("navn")
    val navn: String? = null,

    @JsonProperty("verdi")
    val verdi: Any? = null,

    @JsonProperty("datatype")
    val datatype: String? = null
)

/**
 * Represents a property value in the egenskaper map.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class EgenskapValue(
    @JsonProperty("type")
    val type: String? = null,

    @JsonProperty("verdi")
    val verdi: Any? = null
)

/**
 * Represents the location/placement of a road object on the road network.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class Stedfesting(
    @JsonProperty("type")
    val type: String? = null,

    @JsonProperty("linjer")
    val linjer: List<StedfestingLinje>? = null,

    @JsonProperty("geometries")
    val geometries: List<String>? = null
)

/**
 * Represents a line segment in stedfesting (StedfestingLinjer type).
 * The id field in the JSON is actually a veglenkesekvensId.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class StedfestingLinje(
    @JsonProperty("id")
    val veglenkesekvensId: Long? = null,

    @JsonProperty("startposisjon")
    val startposisjon: Double? = null,

    @JsonProperty("sluttposisjon")
    val sluttposisjon: Double? = null,

    @JsonProperty("retning")
    val retning: String? = null
)

/**
 * API response wrapper for paginated road object results.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class VegobjekterResponse(
    @JsonProperty("vegobjekter")
    val vegobjekter: List<Vegobjekt> = emptyList(),

    @JsonProperty("metadata")
    val metadata: ResponseMetadata? = null
)

/**
 * API response wrapper for paginated road link sequence results.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class VeglenkesekvensResponse(
    @JsonProperty("objekter")
    val objekter: List<Veglenkesekvens> = emptyList(),

    @JsonProperty("metadata")
    val metadata: ResponseMetadata? = null
)

/**
 * Metadata for paginated API responses.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class ResponseMetadata(
    @JsonProperty("antall")
    val antall: Int? = null,

    @JsonProperty("returnert")
    val returnert: Int? = null,

    @JsonProperty("neste")
    val neste: NesteLink? = null
)

/**
 * Link to the next page of results.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class NesteLink(
    @JsonProperty("start")
    val start: String? = null,

    @JsonProperty("href")
    val href: String? = null
)
