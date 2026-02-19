package data

data class AssignmentCfg(
	val workers: List<String> = emptyList(),
	val tasks: List<String> = emptyList(),
	val cost: List<List<Double>> = emptyList(),
	val teams: List<List<String>> = emptyList(),
	val maxPerTeam: Int = 0,
) {
	override fun toString(): String {
		return "Workers: $workers\nTasks: $tasks"
	}

	companion object {
		fun new(name: String): AssignmentCfg? {
			val data = load(name) ?: return null
			val workers = data["workers"].toStringList()
			val tasks = data["tasks"].toStringList()
			if (tasks.size > workers.size) {
				println("Invalid assignment problem: more tasks than workers")
				return null
			}
			val cost = mutableListOf<List<Double>>()
			for(line in data["cost"].parseList()) {
				val row = line.matrixRow(true)
				val costRow = if (row.size < workers.size) {
					row + List(workers.size - row.size) { 0.0 }
				} else  {
					row.subList(0, workers.size)
				}
				cost.add(costRow)
			}
			val teams = mutableListOf<List<String>>()
			for(line in data["teams"].parseList()) {
				teams.add(line.toStringList())
			}
			return AssignmentCfg(
				workers = workers,
				tasks = tasks,
				cost = cost,
				teams = teams,
				maxPerTeam = data["maxPerTeam"].parseInt(),
			)
		}
	}
}