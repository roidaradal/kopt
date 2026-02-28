package problem

import data.Subsets
import data.newName
import discrete.Goal
import discrete.Problem
import discrete.Solution
import fn.ScoreFn
import fn.asSubset

fun newSetCover(variant: String, n: Int): Problem? {
	val name = newName(SetCover, variant, n)
	return when (variant) {
		"basic" -> setCover(name)
		"weighted" -> weightedSetCover(name)
		else -> null
	}
}

fun newSetCoverProblem(name: String): Pair<Problem?, Subsets?> {
	val (p, cfg) = newSubsetsProblem(name)
	if(p == null || cfg == null) return Pair(null, null)

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
	return Pair(p, cfg)
}

fun setCover(name: String): Problem? {
	val (p, _) = newSetCoverProblem(name)
	return p
}

fun weightedSetCover(name: String): Problem? {
	val (p, cfg) = newSetCoverProblem(name)
	if(p == null || cfg == null) return null
	if(cfg.weight.size != cfg.names.size) return null

	p.objectiveFn = ScoreFn.sumWeightedSubset(cfg.names, cfg.weight)
	return p
}
