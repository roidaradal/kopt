package problem

import discrete.Goal
import discrete.Problem
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
	p.addUniversalConstraint { solution ->
		val sums = solution.partitionSums(cfg.bins, cfg.weight)
		sums.all { sum -> sum <= cfg.capacity }
	}
	return p
}