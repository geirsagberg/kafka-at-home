package no.vegvesen.nvdb.kafka.extensions

fun <K, V, KR, VR> Map<K, V>.associate(transform: (Map.Entry<K, V>) -> Pair<KR, VR>): Map<KR, VR> =
    entries.associate(transform)
