package data

data class FlowShop(
	val jobs: List<String> = emptyList(),
	val machines: List<String> = emptyList(),
	val taskTimes: Map<String, List<Int>> = emptyMap(),
) {
	override fun toString(): String {
		val out = mutableListOf<String>()
		out.add("Jobs: $jobs")
		out.add("Machines: $machines")
		out.add("Task Times:")
		for(job in jobs) {
			val times = taskTimes[job] ?: continue
			out.add("$job: $times")
		}
		return out.joinToString("\n")
	}

	companion object {
		fun new(name: String): FlowShop? {
			val data = load(name) ?: return null
			val taskTimes = mutableMapOf<String, List<Int>>()
			for((task, value) in data["taskTimes"].parseMap()) {
				taskTimes[task] = value.toIntList()
			}
			return FlowShop(
				jobs = data["jobs"].toStringList(),
				machines = data["machines"].toStringList(),
				taskTimes = taskTimes,
			)
		}
	}
}