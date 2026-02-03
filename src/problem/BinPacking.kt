package problem

import data.newName
import discrete.Goal
import discrete.Problem
import discrete.Solution
import fn.partitionSums

fun newBinPacking(variant: String, n: Int): Problem? {
	val name = newName(BinPacking, variant, n)
	return when (variant) {
		"basic" -> binPacking(name)
		else -> null
	}
}

fun binPacking(name: String): Problem? {
	val (p, cfg) = newBinPartitionProblem(name)
	if (p == null || cfg == null) return null

	p.goal = Goal.Minimize
	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		val sums = solution.partitionSums(cfg.bins, cfg.weight)
		return sums.all { it <= cfg.capacity }
	})
	return p
}