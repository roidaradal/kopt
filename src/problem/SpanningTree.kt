package problem

import data.GraphCfg
import data.graphEdges
import data.newName
import discrete.Goal
import discrete.Problem
import discrete.Score
import discrete.Solution
import fn.Constraint
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

fun newSpanningTreeProblem(name: String): Pair<Problem?, GraphCfg?> {
	val (p, cfg) = newGraphSubsetProblem(name, ::graphEdges)
	if (p == null || cfg == null) {
		return Pair(null, null)
	}
	val graph = cfg.graph

	p.addUniversalConstraint(Constraint.allVerticesCovered(graph, graph.vertices))
	p.addUniversalConstraint(Constraint.spanningTree(graph, graph.vertices))
	p.goal = Goal.MINIMIZE
	return Pair(p, cfg)
}

fun minimumSpanningTree(name: String): Problem? {
	val (p, cfg) = newSpanningTreeProblem(name)
	return edgeWeightedProblem(p, cfg)
}

fun minDegreeSpanningTree(name: String): Problem? {
	val (p, cfg) = newSpanningTreeProblem(name)
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
	val (problem, cfg) = newSpanningTreeProblem(name)
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