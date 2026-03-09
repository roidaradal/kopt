package data

data class Subsets(
	val universal: List<String> = emptyList(),
	val names: List<String> = emptyList(),
	val subsets: List<List<String>> = emptyList(),
	val limit: Int = 0,
	val weight: Map<String, Double> = emptyMap(),
	val budget: Double = 0.0,
	val value: List<Double> = emptyList(),
) {
	companion object {
		fun new(name: String): Subsets? {
			val data = load(name) ?: return null
			val names = mutableListOf<String>()
			val subsets = mutableListOf<List<String>>()
			for ((key, value) in data["subsets"].parseMap()) {
				names.add(key)
				subsets.add(value.toStringList())
			}
			val weight = mutableMapOf<String, Double>()
			for((key, value) in data["weight"].parseMap()) {
				weight[key] = value.toDouble()
			}
			return Subsets(
				universal = data["universal"].toStringList(),
				names = names,
				subsets = subsets,
				limit = data["limit"].parseInt(),
				weight = weight,
				budget = data["budget"].parseDouble(),
				value = data["value"].toDoubleList(),
			)
		}
	}

	override fun toString(): String = "Universal: $universal\nNames: $names\nSubsets: $subsets"
}