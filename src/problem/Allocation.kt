package problem

import data.Resource
import data.newName
import discrete.Domain
import discrete.Goal
import discrete.Problem
import discrete.ProblemType
import discrete.Solution
import discrete.Variables
import fn.ScoreFn
import fn.StringFn

fun newAllocation(variant: String, n: Int): Problem? {
	val name = newName(Allocation, variant, n)
	return when (variant) {
		"resource" -> resourceAllocation(name)
		else -> null
	}
}

fun resourceAllocation(name: String): Problem? {
	val cfg = Resource.new(name) ?: return null
	val p = Problem(
		name,
		description = cfg.toString(),
		type = ProblemType.ASSIGNMENT,
		goal = Goal.MAXIMIZE,
		variables = Variables.from(cfg.items),
	)
	for((i, variable) in p.variables.withIndex()) {
		p.domain[variable] = Domain.range(0, cfg.count[i])
	}

	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		val count = solution.map
		return p.variables.sumOf { (count[it] ?: 0) * cfg.cost[it] } <= cfg.budget
	})

	p.objectiveFn = ScoreFn.sumWeightedValues(p.variables, cfg.value)
	p.solutionStringFn = StringFn.values<Int>(p, null)
	return p
}