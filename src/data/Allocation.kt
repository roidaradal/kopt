package data

data class Resource(
	val budget: Double = 0.0,
	val items: List<String> = emptyList(),
	val count: List<Int> = emptyList(),
	val cost: List<Double> = emptyList(),
	val value: List<Double> = emptyList(),
) {
	override fun toString(): String {
		val out = mutableListOf("Budget: $budget")
		out.add("Items: ${items.size}")
		for ((i, item) in items.withIndex()) {
			val line = "$item: ${count[i]}x, Cost: ${cost[i]}, Value: ${value[i]}"
			out.add(line)
		}
		return out.joinToString("\n")
	}

	companion object {
		fun new(name: String): Resource? {
			val data = load(name) ?: return null
			return Resource(
				budget = data["budget"].parseDouble(),
				items = data["items"].toStringList(),
				count = data["count"].toIntList(),
				cost = data["cost"].toDoubleList(),
				value = data["value"].toDoubleList()
			)
		}
	}
}