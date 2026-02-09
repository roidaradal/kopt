package problem

import data.newName
import data.spanVertices
import discrete.Problem
import discrete.Score
import discrete.Solution
import fn.asSubset
import fn.increment
import fn.mapList
import fn.spannedVertices

fun newSpanningTree(variant: String, n: Int): Problem? {
	val name = newName(SpanningTree, variant, n)
	return when (variant) {
		"mst" -> minimumSpanningTree(name)
		"mdst" -> minDegreeSpanningTree(name)
		"kmst" -> kMinimumSpanningTree(name)
		else -> null
	}
}

fun minimumSpanningTree(name: String): Problem? {
	val (p, cfg) = newSpanningTreeProblem(name, ::spanVertices)
	return edgeWeightedProblem(p, cfg)
}

fun minDegreeSpanningTree(name: String): Problem? {
	val (p, cfg) = newSpanningTreeProblem(name, ::spanVertices)
	if (p == null || cfg == null) {
		return null
	}
	val graph = cfg.graph
	p.objectiveFn = fun(solution: Solution): Score {
		val degree = mutableMapOf<String, Int>()
		for(x in solution.asSubset()) {
			val (v1, v2) = graph.edges[x]
			degree.increment(v1)
			degree.increment(v2)
		}
		return degree.values.max().toDouble()
	}
	return p
}

fun kMinimumSpanningTree(name: String): Problem? {
	val (problem, cfg) = newSpanningTreeProblem(name, ::spanVertices)
	val p = edgeWeightedProblem(problem, cfg)
	if (p == null || cfg == null || cfg.k == 0) return null
	val graph = cfg.graph
	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		val edges = solution.asSubset().mapList(graph.edges)
		if (edges.size != cfg.k-1) return false
		val reachableCount = solution.spannedVertices(graph)?.size ?: 0
		return reachableCount == cfg.k
	})
	return p
}