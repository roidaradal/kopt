package problem

import data.newName
import discrete.Goal
import discrete.Inf
import discrete.Problem
import discrete.Score
import discrete.Solution
import fn.asSubset
import fn.mapList

fun newSubsetSum(variant: String, n: Int): Problem? {
	val name = newName(SubsetSum, variant, n)
	return when (variant) {
		"basic" -> subsetSum(name)
		else -> null
	}
}

fun subsetSum(name: String): Problem? {
	val (p, cfg) = newNumbersSubsetProblem(name)
	if (p == null || cfg == null || cfg.target == 0) return null

	p.description += "\nTarget: ${cfg.target}"
	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		val total = solution.asSubset().mapList(cfg.numbers).sum()
		return if (p.isSatisfaction) (total == cfg.target) else (total <= cfg.target)
	})

	p.goal = Goal.Minimize
	p.objectiveFn = fun(solution: Solution): Score {
		val total = solution.asSubset().mapList(cfg.numbers).sum()
		return if (total > cfg.target) Inf else (cfg.target - total).toDouble()
	}
	return p
}