package data

typealias TimeRange = Pair<Int, Int>

data class ShopSchedule(
	val jobs: List<String> = emptyList(),
	val machines: List<String> = emptyList(),
	val taskTimes: Map<String, List<Int>> = emptyMap(),
	val jobTasks: Map<String, List<Task>> = emptyMap(),
	val maxMakespan: Int = 0,
) {
	override fun toString(): String {
		val out = mutableListOf<String>()
		out.add("Jobs: $jobs")
		out.add("Machines: $machines")
		return out.joinToString("\n")
	}

	fun getTasks(): List<Task> {
		val tasks = mutableListOf<Task>()
		for(job in jobs) {
			for(task in jobTasks.getOrDefault(job, listOf())) {
				tasks.add(task)
			}
		}
		return tasks
	}


	companion object {
		fun new(name: String): ShopSchedule? {
			val data = load(name) ?: return null
			val jobTasks = mutableMapOf<String, List<Task>>()
			var totalDuration = 0
			for ((job, value) in data["tasks"].parseMap()) {
				val tasks = value.toStringList().mapIndexed { i, text ->
					val parts = text.split(":")
					Task(
						job = job,
						name = "J${job}_T$i",
						machine = parts[0],
						duration = parts[1].parseInt(),
					)
				}
				jobTasks[job] = tasks
				totalDuration += tasks.sumOf { it.duration }
			}
			val taskTimes = mutableMapOf<String, List<Int>>()
			for((task, value) in data["taskTimes"].parseMap()) {
				taskTimes[task] = value.toIntList()
			}
			return ShopSchedule(
				jobs = data["jobs"].toStringList(),
				machines = data["machines"].toStringList(),
				taskTimes = taskTimes,
				jobTasks = jobTasks,
				maxMakespan = totalDuration,
			)
		}
	}
}

data class Task(
	val job: String,
	val name: String,
	val machine: String,
	val duration: Int,
)

data class SlotSched(
	val start: Int,
	val end: Int,
	val name: String,
)
