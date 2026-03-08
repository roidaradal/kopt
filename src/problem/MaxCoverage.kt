package problem

import data.Subsets
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
		"weighted" -> weightedMaxCoverage(name)
		else -> null
	}
}

fun newMaxCoverageProblem(name: String): Pair<Problem?, Subsets?> {
	val (p, cfg) = newSubsetsProblem(name)
	if (p == null || cfg == null || cfg.limit == 0) return Pair(null, null)

	p.description += "\nLimit: ${cfg.limit}"
	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		return solution.asSubset().size <= cfg.limit
	})

	p.goal = Goal.MAXIMIZE
	return Pair(p, cfg)
}

fun maxCoverage(name: String): Problem? {
	val (p, cfg) = newMaxCoverageProblem(name)
	if (p == null || cfg == null) return null

	p.objectiveFn = fun(solution: Solution): Score {
		val covered = mutableSetOf<String>()
		for(x in solution.asSubset()) {
			covered.addAll(cfg.subsets[x])
		}
		return covered.size.toDouble()
	}
	return p
}

fun weightedMaxCoverage(name: String): Problem? {
	val (p, cfg) = newMaxCoverageProblem(name)
	if (p == null || cfg == null) return null
	if (cfg.weight.size != cfg.universal.size) return null

	p.objectiveFn = fun(solution: Solution): Score {
		val covered = mutableSetOf<String>()
		for(x in solution.asSubset()) {
			covered.addAll(cfg.subsets[x])
		}
		return covered.sumOf { cfg.weight[it] ?: 0.0 }
	}
	return p
}