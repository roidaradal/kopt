package problem

import data.newName
import discrete.Goal
import discrete.Problem
import discrete.Solution
import fn.partitionSums

fun newBinCover(variant: String, n: Int): Problem? {
	val name = newName(BinCover, variant, n)
	return when (variant) {
		"basic" -> binCover(name)
		else -> null
	}
}

fun binCover(name: String): Problem? {
	val (p, cfg) = newBinPartitionProblem(name)
	if (p == null || cfg == null) return null

	p.goal = Goal.MAXIMIZE
	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		val sums = solution.partitionSums(cfg.bins, cfg.weight).filter { it > 0 }
		return sums.all { it >= cfg.capacity }
	})
	return p
}