package problem

import data.newName
import discrete.Goal
import discrete.Problem
import discrete.Score
import discrete.Solution
import fn.tallyValues

fun newGraphPartition(variant: String, n: Int): Problem? {
	val name = newName(GraphPartition, variant, n)
	return when (variant) {
		"basic" -> graphPartition(name)
		else -> null
	}
}

fun graphPartition(name: String): Problem? {
	val (p, cfg) = newGraphPartitionProblem(name)
	if(p == null || cfg == null) return null
	val graph = cfg.graph

	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		val partitionSizes = solution.tallyValues(p.uniformDomain)
		return partitionSizes.values.all { it >= cfg.minSize }
	})

	p.goal = Goal.MINIMIZE
	p.objectiveFn = fun(solution: Solution): Score {
		var score: Score = 0.0
		val group = solution.map
		for((i, edge) in graph.edges.withIndex()) {
			val x1 = graph.indexOf(edge.vertex1)
			val x2 = graph.indexOf(edge.vertex2)
			if (group[x1] != group[x2]) score += cfg.edgeWeight[i]
		}
		return score
	}
	return p
}