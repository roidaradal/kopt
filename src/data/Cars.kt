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

data class CarSequence(
	val cars: List<String> = emptyList(),
	val carOptions: Map<String, List<Boolean>> = emptyMap(),
	val optionMax: List<Int> = emptyList(),
	val optionWindow: List<Int> =  emptyList(),
) {
	override fun toString(): String = "Cars: $cars\nCarOptions: $carOptions\nOptionMax: $optionMax\nOptionWindow: $optionWindow"

	companion object {
		fun new(name: String): CarSequence? {
			val data = load(name) ?: return null
			val carTypes = data["cars"].toStringList()
			val carCounts = data["carCount"].toIntList()
			if (carTypes.size != carCounts.size) return null
			val allCars = mutableListOf<String>()
			for ((i, car) in carTypes.withIndex()) {
				repeat(carCounts[i]) { allCars.add(car) }
			}
			val options = data["options"].toStringList()
			val carOptions = mutableMapOf<String, List<Boolean>>()
			for((car, v) in data["carOptions"].parseMap()) {
				val flags = MutableList(options.size) {false}
				for(option in v.toStringList()) {
					flags[options.indexOf(option)] = true
				}
				carOptions[car] = flags
			}
			return CarSequence(
				cars = allCars,
				carOptions = carOptions,
				optionMax = data["optionMax"].toIntList(),
				optionWindow = data["optionWindow"].toIntList(),
			)
		}
	}
}