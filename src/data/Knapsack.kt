package data

data class Knapsack(
	val capacity: Double = 0.0,
	val items: List<String> = emptyList(),
	val weight: List<Double> = emptyList(),
	val value: List<Double> = emptyList(),
	val pairBonus: Map<StringPair, Double> = emptyMap(),
) {
	companion object {
		fun new(name: String): Knapsack? {
			val data = load(name) ?: return null
			val pairBonus: MutableMap<StringPair, Double> = mutableMapOf()
			for(line in data["pairBonus"].parseList()) {
				val parts = line.toStringList()
				if (parts.size != 3) continue
				val pair = StringPair(parts[0], parts[1])
				pairBonus[pair] = parts[2].parseDouble()
			}
			return Knapsack(
				capacity = data["capacity"].parseDouble(),
				items = data["items"].toStringList(),
				weight = data["weight"].toDoubleList(),
				value = data["value"].toDoubleList(),
				pairBonus = pairBonus,
			)
		}
	}

	override fun toString(): String = "Capacity: $capacity\nItems: $items\nWeight: $weight\nValue: $value"
}