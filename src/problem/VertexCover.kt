package problem

import data.GraphCfg
import data.graphVertices
import data.newName
import discrete.Problem
import discrete.Solution
import fn.ScoreFn

fun newVertexCover(variant: String, n: Int): Problem? {
	val name = newName(VertexCover, variant, n)
	return when (variant) {
		"basic" -> vertexCover(name)
		"weighted" -> weightedVertexCover(name)
		else -> null
	}
}

fun newVertexCoverProblem(name: String): Pair<Problem?, GraphCfg?> {
	val (p, cfg) = newGraphCoverProblem(name, ::graphVertices)
	if (p == null || cfg == null) return Pair(null, null)
	val graph = cfg.graph
	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		val used = solution.map.withDefault { 0 }
		return graph.edges.all { edge ->
			val x1 = graph.indexOf(edge.vertex1)
			val x2 = graph.indexOf(edge.vertex2)
			used.getValue(x1) + used.getValue(x2) > 0
		}
	})
	return Pair(p, cfg)
}

fun vertexCover(name: String): Problem? {
	val (p, _) = newVertexCoverProblem(name)
	return p
}

fun weightedVertexCover(name: String): Problem? {
	val (p, cfg) = newVertexCoverProblem(name)
	if (p == null || cfg == null) return null
	if (cfg.graph.vertices.size != cfg.vertexWeight.size) return null
	p.objectiveFn = ScoreFn.sumWeightedValues(p.variables, cfg.vertexWeight)
	return p
}