package data

data class Subsets(
	val universal: List<String> = emptyList(),
	val names: List<String> = emptyList(),
	val subsets: List<List<String>> = emptyList(),
	val limit: Int = 0,
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
			return Subsets(
				universal = data["universal"].toStringList(),
				names = names,
				subsets = subsets,
				limit = data["limit"].parseInt(),
			)
		}
	}
}