package data

data class Numbers(
	val numbers: List<Int> = emptyList(),
	val weight: List<Double> = emptyList(),
	val target: Int = 0,
	val numBins: Int = 0,
) {
	companion object {
		fun new(name: String): Numbers? {
			val data = load(name) ?: return null
			return Numbers(
				numbers = data["numbers"].toIntList(),
				weight = data["weight"].toDoubleList(),
				target = data["target"].parseInt(),
				numBins = data["numBins"].parseInt(),
			)
		}
		fun n(name: String): Int {
			val data = load(name) ?: return 0
			return data["n"].parseInt()
		}
	}

	override fun toString(): String = "Numbers: $numbers"
}