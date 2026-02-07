package problem

import data.graphVertices
import data.newName
import discrete.Problem
import discrete.Solution

fun newVertexCover(variant: String, n: Int): Problem? {
	val name = newName(VertexCover, variant, n)
	return when (variant) {
		"basic" -> vertexCover(name)
		else -> null
	}
}

fun vertexCover(name: String): Problem? {
	val (p, cfg) = newGraphCoverProblem(name, ::graphVertices)
	if (p == null || cfg == null) return null
	val graph = cfg.graph
	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		val used = solution.map.withDefault { 0 }
		return graph.edges.all { edge ->
			val x1 = graph.indexOf(edge.vertex1)
			val x2 = graph.indexOf(edge.vertex2)
			used.getValue(x1) + used.getValue(x2) > 0
		}
	})
	return p
}