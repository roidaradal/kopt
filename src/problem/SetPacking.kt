package problem

import data.newName
import discrete.Goal
import discrete.Problem
import discrete.Solution
import fn.asSubset
import fn.updateCounter

fun newSetPacking(variant: String, n: Int): Problem? {
	val name = newName(SetPacking, variant, n)
	return when (variant) {
		"basic" -> setPacking(name)
		else -> null
	}
}

fun setPacking(name: String): Problem? {
	val (p, cfg) = newSubsetsProblem(name)
	if(p == null || cfg == null) return null

	p.goal = Goal.MAXIMIZE
	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		val covered = mutableMapOf<String, Int>()
		solution.asSubset().forEach { x -> covered.updateCounter(cfg.subsets[x]) }
		return covered.values.all { it == 1 }
	})
	return p
}