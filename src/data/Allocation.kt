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

data class Scene(
	val numDays: Int = 0,
	val dayMin: List<Int> = emptyList(),
	val dayMax: List<Int> = emptyList(),
	val dailyCost: Map<String, Double> = emptyMap(),
	val scenes: List<String> = emptyList(),
	val sceneActors: Map<String, List<String>> = emptyMap(),
) {
	override fun toString(): String {
		val days = mutableListOf<String>()
		for ((i, limit1) in dayMin.withIndex()) {
			val limit2 = dayMax[i]
			days.add("($limit1, $limit2)")
		}
		val actors = mutableListOf("Actors: ${dailyCost.size}")
		for((actor, cost) in dailyCost) {
			actors.add("$actor : $cost")
		}
		val sceneList = mutableListOf("Scenes: ${scenes.size}")
		for(scene in scenes) {
			val actors = sceneActors[scene] ?: emptyList()
			sceneList.add("$scene : ${actors.joinToString(" ")}")
		}
		return "Days: $numDays\n${days.joinToString(", ")}\n${actors.joinToString("\n")}\n${sceneList.joinToString("\n")}"
	}

	companion object {
		fun new(name: String): Scene? {
			val data = load(name) ?: return null
			val dailyCost = mutableMapOf<String, Double>()
			for((actor, value) in data["actors"].parseMap()) {
				dailyCost[actor] = value.parseDouble()
			}
			val scenes = mutableListOf<String>()
			val sceneActors = mutableMapOf<String, List<String>>()
			for ((scene, value) in data["scenes"].parseMap()) {
				sceneActors[scene] = value.toStringList()
				scenes.add(scene)
			}
			return Scene(
				numDays = data["numDays"].parseInt(),
				dayMin = data["dayMin"].toIntList(),
				dayMax = data["dayMax"].toIntList(),
				dailyCost = dailyCost,
				scenes = scenes.sorted(),
				sceneActors = sceneActors,
			)
		}
	}
}