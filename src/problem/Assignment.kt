package problem

import data.AssignmentCfg
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

fun newAssignment(variant: String, n: Int): Problem? {
	val name = newName(Assignment, variant, n)
	return when (variant) {
		"basic" -> assignment(name)
		"bottleneck" -> bottleneckAssignment(name)
		else -> null
	}
}

fun newAssignmentProblem(name: String): Pair<Problem?, AssignmentCfg?> {
	val cfg = AssignmentCfg.new(name) ?: return Pair(null, null)
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