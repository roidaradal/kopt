package problem

import discrete.Goal
import discrete.Problem
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

	p.goal = Goal.Maximize
	p.addUniversalConstraint { solution ->
		val sums = solution.partitionSums(cfg.bins, cfg.weight).filter { it > 0 }
		sums.all { sum -> sum >= cfg.capacity }
	}
	return p
}