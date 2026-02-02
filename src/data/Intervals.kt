package data

data class Intervals(
	val activities: List<String> = emptyList(),
	val start: List<Double> = emptyList(),
	val end: List<Double> = emptyList(),
	val weight: List<Double> = emptyList(),
) {
	companion object {
		fun new(name: String): Intervals? {
			val data = load(name) ?: return null
			return Intervals(
				activities = data["activities"].toStringList(),
				start = data["start"].toDoubleList(),
				end = data["end"].toDoubleList(),
				weight = data["weight"].toDoubleList(),
			)
		}
	}
}