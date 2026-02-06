package problem

import data.newName
import discrete.Goal
import discrete.Problem
import discrete.Score
import discrete.Solution
import fn.asSubset

fun newMaxCoverage(variant: String, n: Int): Problem? {
	val name = newName(MaxCoverage, variant, n)
	return when (variant) {
		"basic" -> maxCoverage(name)
		else -> null
	}
}

fun maxCoverage(name: String): Problem? {
	val (p, cfg) = newSubsetsProblem(name)
	if (p == null || cfg == null || cfg.limit == 0) return null

	p.description += "\nLimit: ${cfg.limit}"
	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		return solution.asSubset().size <= cfg.limit
	})

	p.goal = Goal.MAXIMIZE
	p.objectiveFn = fun(solution: Solution): Score {
		val covered = mutableSetOf<String>()
		for(x in solution.asSubset()) {
			covered.addAll(cfg.subsets[x])
		}
		return covered.size.toDouble()
	}
	return p
}