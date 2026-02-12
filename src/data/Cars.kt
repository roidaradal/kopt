package data

data class Cars(
	val cars: List<String> = emptyList(),
	val sequence: List<String> = emptyList(),
	val carColors: List<String> = emptyList(),
	val maxShift: Int = 0,
) {
	companion object {
		fun new(name: String): Cars? {
			val data = load(name) ?: return null
			return Cars(
				cars = data["cars"].toStringList(),
				sequence = data["sequence"].toStringList(),
				carColors = data["carColors"].toStringList(),
				maxShift = data["maxShift"].parseInt(),
			)
		}
	}
}