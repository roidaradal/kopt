package problem

import data.Subsets
import data.newName
import discrete.Goal
import discrete.Problem
import discrete.Solution
import fn.ScoreFn
import fn.asSubset
import fn.updateCounter

fun newSetPacking(variant: String, n: Int): Problem? {
	val name = newName(SetPacking, variant, n)
	return when (variant) {
		"basic" -> setPacking(name)
		"weighted" -> weightedSetPacking(name)
		else -> null
	}
}

fun newSetPackingProblem(name: String): Pair<Problem?, Subsets?> {
	val (p, cfg) = newSubsetsProblem(name)
	if(p == null || cfg == null) return Pair(null, null)

	p.goal = Goal.MAXIMIZE
	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		val covered = mutableMapOf<String, Int>()
		solution.asSubset().forEach { x -> covered.updateCounter(cfg.subsets[x]) }
		return covered.values.all { it == 1 }
	})
	return Pair(p, cfg)
}

fun setPacking(name: String): Problem? {
	val (p, _) = newSetPackingProblem(name)
	return p
}

fun weightedSetPacking(name: String): Problem? {
	val (p, cfg) = newSetPackingProblem(name)
	if(p == null || cfg == null) return null
	if(cfg.weight.size != cfg.names.size) return null

	p.objectiveFn = ScoreFn.sumWeightedSubset(cfg.names, cfg.weight)
	return p
}