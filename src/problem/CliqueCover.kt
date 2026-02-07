package problem

import data.newName
import discrete.Goal
import discrete.Problem
import discrete.Solution
import fn.asPartition
import fn.isClique
import fn.mapList

fun newCliqueCover(variant: String, n: Int): Problem? {
	val name = newName(CliqueCover, variant, n)
	return when (variant) {
		"basic" -> cliqueCover(name)
		else -> null
	}
}

fun cliqueCover(name: String): Problem? {
	val (p, cfg) = newGraphPartitionProblem(name)
	if (p == null || cfg == null) return null
	val graph = cfg.graph

	p.goal = Goal.MINIMIZE
	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		return solution.asPartition(p.uniformDomain).all { group ->
			graph.isClique(group.mapList(graph.vertices))
		}
	})
	return p
}