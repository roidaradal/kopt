package data

typealias TimeRange = Pair<Int, Int>

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

data class JobShop(
	val jobs: List<String> = emptyList(),
	val machines: List<String> = emptyList(),
	val jobTasks: Map<String, List<Task>> = emptyMap(),
	val maxMakespan: Int = 0,
) {
	override fun toString(): String {
		val out = mutableListOf<String>()
		out.add("Jobs: $jobs")
		out.add("Machines: $machines")
		return out.joinToString("\n")
	}

	companion object {
		fun new(name: String): JobShop? {
			val data = load(name) ?: return null
			val jobTasks = mutableMapOf<String, List<Task>>()
			var totalDuration = 0
			for ((job, value) in data["tasks"].parseMap()) {
				val tasks = value.toStringList().mapIndexed { i, text ->
					val parts = text.split(":")
					Task(
						name = "J${job}_T$i",
						machine = parts[0],
						duration = parts[1].parseInt(),
					)
				}
				jobTasks[job] = tasks
				totalDuration += tasks.sumOf { it.duration }
			}
			return JobShop(
				jobs = data["jobs"].toStringList(),
				machines = data["machines"].toStringList(),
				jobTasks = jobTasks,
				maxMakespan = totalDuration
			)
		}
	}
}

data class Task(
	val name: String,
	val machine: String,
	val duration: Int,
)

data class SlotSched(
	val start: Int,
	val end: Int,
	val name: String,
)
