package data

data class Bins(
	val capacity: Double = 0.0,
	val bins: List<Int> = emptyList(),
	val weight: List<Double> = emptyList(),
) {
	companion object {
		fun new(name: String): Bins? {
			val data = load(name) ?: return null
			val numBins = data["numBins"].parseInt()
			return Bins(
				bins = (1..numBins).toList(),
				capacity = data["capacity"].parseDouble(),
				weight = data["weight"].toDoubleList(),
			)
		}
	}
}