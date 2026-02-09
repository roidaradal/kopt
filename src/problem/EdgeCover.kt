package problem

import data.graphEdges
import data.newName
import discrete.Problem
import fn.Constraint

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
	p.addUniversalConstraint(Constraint.allVerticesCovered(graph, graph.vertices))
	return p
}
