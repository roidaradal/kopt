package problem

import data.FlowShop
import data.newName
import discrete.Domain
import discrete.Goal
import discrete.Inf
import discrete.Problem
import discrete.ProblemType
import discrete.Score
import discrete.Solution
import discrete.Variables
import fn.Constraint
import fn.StringFn
import fn.asSequence
import kotlin.math.max

fun newFlowShopScheduling(variant: String, n: Int): Problem? {
	val name = newName(FlowShopScheduling, variant, n)
	return when (variant) {
		"basic" -> flowShopScheduling(name)
		else -> null
	}
}

fun flowShopScheduling(name: String): Problem? {
	val cfg = FlowShop.new(name) ?: return null
	val p = Problem(
		name,
		description = cfg.toString(),
		type = ProblemType.SEQUENCE,
		goal = Goal.MINIMIZE,
		variables = Variables.from(cfg.jobs),
		solutionStringFn = StringFn.sequence(cfg.jobs),
	)
	p.addVariableDomains(Domain.index(cfg.jobs.size))
	p.addUniversalConstraint(Constraint::allUnique)

	p.objectiveFn = fun(solution: Solution): Score {
		val sequence = solution.asSequence()
		val end = mutableMapOf<IntPair, Double>()
		for(m in 0 until cfg.machines.size) {
			for((i, x) in sequence.withIndex()) {
				val above = end[IntPair(m-1, i)] ?: 0.0
				val prev = end[IntPair(m, i-1)] ?: 0.0
				val start = max(above, prev)
				val taskTimes = cfg.taskTimes[cfg.jobs[x]] ?: continue
				end[IntPair(m, i)] = start + taskTimes[m]
			}
		}
		val lastRow = cfg.machines.size - 1
		val lastCol = cfg.jobs.size - 1
		return end[IntPair(lastRow, lastCol)] ?: Inf
	}

	return p
}