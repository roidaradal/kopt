package problem

import data.graphEdges
import data.newName
import discrete.Problem
import discrete.Solution
import fn.asSubset

fun newEdgeCover(variant: String, n: Int): Problem? {
	val name = newName(EdgeCover, variant, n)
	return when (variant) {
		"basic" -> edgeCover(name)
		else -> null
	}
}

fun edgeCover(name: String): Problem? {
	val (p, cfg) = newGraphCoverProblem(name, ::graphEdges)
	if (p == null || cfg == null) return null
	val graph = cfg.graph
	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		val count = graph.vertices.associateWith { 0 }.toMutableMap()
		for(x in solution.asSubset()) {
			val (v1, v2) = graph.edges[x]
			count[v1] = (count[v1] ?: 0) + 1
			count[v2] = (count[v2] ?: 0) + 1
		}
		return count.values.all { it > 0 }
	})
	return p
}
