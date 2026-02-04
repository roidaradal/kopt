package data

data class Numbers(
	val numbers: List<Int> = emptyList(),
	val target: Int = 0,
) {
	companion object {
		fun new(name: String): Numbers? {
			val data = load(name) ?: return null
			return Numbers(
				numbers = data["numbers"].toIntList(),
				target = data["target"].parseInt(),
			)
		}
	}
}