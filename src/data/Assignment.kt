package data

data class AssignmentCfg(
	val workers: List<String> = emptyList(),
	val capacity: List<Double> = emptyList(),
	val tasks: List<String> = emptyList(),
	val cost: List<List<Double>> = emptyList(),
	val value: List<List<Double>> = emptyList(),
	val teams: List<List<String>> = emptyList(),
	val maxPerTeam: Int = 0,
) {
	override fun toString(): String {
		return "Workers: $workers\nTasks: $tasks"
	}

	companion object {
		fun new(name: String, mustBeEqual: Boolean): AssignmentCfg? {
			val data = load(name) ?: return null
			val workers = data["workers"].toStringList()
			val tasks = data["tasks"].toStringList()
			if (tasks.size > workers.size && mustBeEqual) {
				println("Invalid assignment problem: more tasks than workers")
				return null
			}
			val cost = mutableListOf<List<Double>>()
			for(line in data["cost"].parseList()) {
				val row = line.matrixRow(true)
				val costRow = if (row.size < workers.size && mustBeEqual) {
					row + List(workers.size - row.size) { 0.0 }
				} else  {
					row.subList(0, workers.size)
				}
				cost.add(costRow)
			}
			val value = mutableListOf<List<Double>>()
			for(line in data["value"].parseList()) {
				value.add(line.matrixRow(true))
			}
			val teams = mutableListOf<List<String>>()
			for(line in data["teams"].parseList()) {
				teams.add(line.toStringList())
			}
			return AssignmentCfg(
				workers = workers,
				capacity = data["capacity"].toDoubleList(),
				tasks = tasks,
				cost = cost,
				value = value,
				teams = teams,
				maxPerTeam = data["maxPerTeam"].parseInt(),
			)
		}
	}
}

data class QuadraticAssignment(
	val count: Int = 0,
	val distance: List<List<Double>> = emptyList(),
	val flow: List<List<Double>> = emptyList(),
) {
	override fun toString(): String {
		return "Count: $count\nDistance: $distance \nFlow: $flow"
	}

	companion object {
		fun new(name: String): QuadraticAssignment? {
			val data = load(name) ?: return null
			val distance = mutableListOf<List<Double>>()
			for(line in data["distance"].parseList()) {
				distance.add(line.matrixRow(false))
			}
			val flow = mutableListOf<List<Double>>()
			for(line in data["flow"].parseList()) {
				flow.add(line.matrixRow(false))
			}
			return QuadraticAssignment(
				count = data["count"].parseInt(),
				distance = distance,
				flow = flow,
			)
		}
	}
}

data class Weapons(
	val weapons: List<String> = emptyList(),
	val count: List<Int> = emptyList(),
	val targets: List<String> = emptyList(),
	val value: List<Double> = emptyList(),
	val chance: List<List<Double>> = emptyList(),
) {
	override fun toString(): String {
		return "Weapons: $weapons\nCount: $count\nTargets: $targets\nValue: $value"
	}

	companion object {
		fun new(name: String): Weapons? {
			val data = load(name) ?: return null
			val chance = mutableListOf<List<Double>>()
			for(line in data["chance"].parseList()) {
				chance.add(line.matrixRow(true))
			}
			return Weapons(
				weapons = data["weapons"].toStringList(),
				targets = data["targets"].toStringList(),
				count = data["count"].toIntList(),
				value = data["value"].toDoubleList(),
				chance = chance,
			)
		}
	}
}