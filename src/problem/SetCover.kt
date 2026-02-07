package problem

import data.newName
import discrete.Goal
import discrete.Problem
import discrete.Solution
import fn.asSubset

fun newSetCover(variant: String, n: Int): Problem? {
	val name = newName(SetCover, variant, n)
	return when (variant) {
		"basic" -> setCover(name)
		else -> null
	}
}

fun setCover(name: String): Problem? {
	val (p, cfg) = newSubsetsProblem(name)
	if(p == null || cfg == null) return null

	p.goal = Goal.MINIMIZE
	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		val covered = cfg.universal.associateWith { false }.toMutableMap()
		for(x in solution.asSubset()) {
			for(item in cfg.subsets[x]) {
				covered[item] = true
			}
		}
		return covered.values.all { it }
	})
	return p
}
