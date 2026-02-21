package problem

import data.AssignmentCfg
import data.QuadraticAssignment
import data.Weapons
import data.combinations
import data.newName
import discrete.Domain
import discrete.Goal
import discrete.Problem
import discrete.ProblemType
import discrete.Score
import discrete.Solution
import discrete.Variables
import fn.Constraint
import fn.StringFn
import fn.wrapBraces

fun newAssignment(variant: String, n: Int): Problem? {
	val name = newName(Assignment, variant, n)
	return when (variant) {
		"basic" -> assignment(name)
		"bottleneck" -> bottleneckAssignment(name)
		"general" -> generalizedAssignment(name)
		"quadratic" -> quadraticAssignment(name)
		"quadratic_bottleneck" -> quadraticBottleneckAssignment(name)
		"weapon" -> weaponTargetAssignment(name)
		else -> null
	}
}

fun newAssignmentProblem(name: String): Pair<Problem?, AssignmentCfg?> {
	val cfg = AssignmentCfg.new(name, true) ?: return Pair(null, null)
	val p = Problem(
		name,
		description = cfg.toString(),
		type = ProblemType.SEQUENCE,
		goal = Goal.MINIMIZE,
		variables = Variables.from(cfg.workers),
	)
	p.addVariableDomains(Domain.index(cfg.workers.size))
	p.addUniversalConstraint(Constraint::allUnique)

	p.solutionStringFn = StringFn.assignment(p, cfg)
	p.solutionCoreFn = StringFn.assignment(p, cfg)
	return Pair(p, cfg)
}

fun assignment(name: String): Problem? {
	val (p, cfg) = newAssignmentProblem(name)
	if(p == null || cfg == null) return null

	if (cfg.teams.size > 1) {
		p.addUniversalConstraint(fun(solution: Solution): Boolean {
			for (team in cfg.teams) {
				var count = 0
				for (workerName in team) {
					val worker = cfg.workers.indexOf(workerName)
					val task = solution.map[worker] ?: continue
					if (cfg.cost[worker][task] > 0.0) count += 1
				}
				if (count > cfg.maxPerTeam) {
					return false
				}
			}
			return true
		})
	}

	p.objectiveFn = fun(solution: Solution): Score {
		return solution.map.entries.sumOf { (worker, task) -> cfg.cost[worker][task] }
	}
	return p
}

fun bottleneckAssignment(name: String): Problem? {
	val (p, cfg) = newAssignmentProblem(name)
	if (p == null || cfg == null) return null

	p.objectiveFn = fun(solution: Solution): Score {
		if(solution.map.isEmpty()) return 0.0
		return solution.map.entries.maxOf { (worker, task) -> cfg.cost[worker][task] }
	}
	return p
}

fun generalizedAssignment(name: String): Problem? {
	val cfg = AssignmentCfg.new(name, false) ?: return null
	val p = Problem(
		name,
		description = cfg.toString(),
		type = ProblemType.ASSIGNMENT,
		goal = Goal.MAXIMIZE,
		variables = Variables.from(cfg.tasks),
	)
	p.addVariableDomains(Domain.from(cfg.workers))

	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		val total = mutableMapOf<Int, Double>()
		for ((task, worker) in solution.map) {
			total[worker] = (total[worker] ?: 0.0) + cfg.cost[worker][task]
		}
		return cfg.capacity.withIndex().all { (worker, limit) -> (total[worker] ?: 0.0) <= limit }
	})

	p.objectiveFn = fun(solution: Solution): Score {
		return solution.map.entries.sumOf { (task, worker) -> cfg.value[worker][task] }
	}

	p.solutionStringFn = fun(solution: Solution): String {
		return p.variables.map { task ->
			val worker = solution.map[task] ?: return ""
			"t${cfg.tasks[task]} = w${cfg.workers[worker]}"
		}.filter { it != "" }.wrapBraces()
	}

	return p
}

fun newQuadraticAssignmentProblem(name: String): Pair<Problem?, QuadraticAssignment?> {
	val cfg = QuadraticAssignment.new(name) ?: return Pair(null, null)
	val p = Problem(
		name,
		description = cfg.toString(),
		type = ProblemType.SEQUENCE,
		goal = Goal.MINIMIZE,
		variables = Variables.index(cfg.count),
	)
	p.addVariableDomains(Domain.index(cfg.count))
	p.addUniversalConstraint(Constraint::allUnique)
	p.solutionStringFn = StringFn.sequence((1..cfg.count).toList())
	return Pair(p, cfg)
}

fun quadraticAssignment(name: String): Problem? {
	val (p, cfg) = newQuadraticAssignmentProblem(name)
	if (p == null || cfg == null) return null

	p.objectiveFn = fun(solution: Solution): Score {
		var totalCost: Score = 0.0
		for(pair in p.variables.combinations(2)) {
			val (facility1, facility2) = pair
			val location1 = solution.map[facility1] ?: continue
			val location2 = solution.map[facility2] ?: continue
			totalCost += cfg.flow[facility1][facility2] * cfg.distance[location1][location2]
			totalCost += cfg.flow[facility2][facility1] * cfg.distance[location2][location1]
		}
		return totalCost
	}
	return p
}

fun quadraticBottleneckAssignment(name: String): Problem? {
	val (p, cfg) = newQuadraticAssignmentProblem(name)
	if (p == null || cfg == null) return null

	p.objectiveFn = fun(solution: Solution): Score {
		var maxCost: Score = 0.0
		for(pair in p.variables.combinations(2)) {
			val (facility1, facility2) = pair
			val location1 = solution.map[facility1] ?: continue
			val location2 = solution.map[facility2] ?: continue
			val cost1 = cfg.flow[facility1][facility2] * cfg.distance[location1][location2]
			val cost2 = cfg.flow[facility2][facility1] * cfg.distance[location2][location1]
			maxCost = listOf(maxCost, cost1, cost2).max()
		}
		return maxCost
	}
	return p
}

fun weaponTargetAssignment(name: String): Problem? {
	val cfg = Weapons.new(name) ?: return null

	val weapons = mutableListOf<Int>()
	for(i in 0 until cfg.weapons.size) {
		repeat(cfg.count[i]) { weapons.add(i) }
	}

	val p = Problem(
		name,
		description = cfg.toString(),
		type = ProblemType.ASSIGNMENT,
		goal = Goal.MINIMIZE,
		variables = Variables.from(cfg.weapons),
	)
	p.addVariableDomains(Domain.from(cfg.targets))

	p.objectiveFn = fun(solution: Solution): Score {
		val survival = cfg.value.toMutableList()
		for((w, target) in solution.map) {
			val weapon = weapons[w]
			survival[target] *= 1.0 - cfg.chance[weapon][target]
		}
		return survival.sum()
	}

	val weaponTargets = fun(solution: Solution): String {
		val matrix = mutableListOf<MutableList<Int>>()
		for(i in cfg.weapons.indices) {
			matrix.add(MutableList(cfg.targets.size) { 0 })
		}
		for((w, target) in solution.map) {
			matrix[weapons[w]][target] += 1
		}
		val output = mutableListOf<String>()
		for((i, weapon) in cfg.weapons.withIndex()) {
			for((j, target) in cfg.targets.withIndex()) {
				if(matrix[i][j] == 0) continue
				output.add("${matrix[i][j]}*$weapon = $target")
			}
		}
		return output.wrapBraces()
	}

	p.solutionStringFn = weaponTargets
	p.solutionCoreFn = weaponTargets
	return p
}