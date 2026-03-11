package problem

import data.Graph
import data.GraphCfg
import data.graphVertices
import data.newName
import discrete.Goal
import discrete.Inf
import discrete.Problem
import discrete.Score
import discrete.Solution
import fn.asSubset
import kotlin.math.max
import kotlin.math.min

fun newKCenter(variant: String, n: Int): Problem? {
	val name = newName(KCenter, variant, n)
	return when (variant) {
		"basic" -> kCenter(name)
		"weighted" -> weightedKCenter(name)
		else -> null
	}
}

fun newKCenterProblem(name: String): Pair<Problem?, GraphCfg?> {
	val (p, cfg) = newGraphSubsetProblem(name, ::graphVertices)
	if (p == null || cfg == null || cfg.k == 0) return Pair(null, null)
	val graph = cfg.graph
	if (graph.edges.size != cfg.edgeWeight.size) return Pair(null, null)

	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		return solution.asSubset().size == cfg.k
	})
	p.goal = Goal.MINIMIZE
	return Pair(p, cfg)
}

fun kCenter(name: String): Problem? {
	val (p, cfg) = newKCenterProblem(name)
	if (p == null || cfg == null) return null
	val graph = cfg.graph
	val pairEdgeIndex = createPairEdgeIndex(graph)

	p.objectiveFn = fun(solution: Solution): Score {
		val selected = solution.asSubset()
		var maxDistance = 0.0
		for(i in 0 until graph.vertices.size) {
			var minDistance: Double = Inf
			for(j in selected) {
				val edgeIndex = pairEdgeIndex[Pair(i, j)] ?: continue
				minDistance = min(minDistance, cfg.edgeWeight[edgeIndex])
			}
			maxDistance = max(maxDistance, minDistance)
		}
		return maxDistance
	}
	return p
}

fun weightedKCenter(name: String): Problem? {
	val (p, cfg) = newKCenterProblem(name)
	if (p == null || cfg == null) return null
	val graph = cfg.graph
	if (graph.vertices.size != cfg.vertexWeight.size) return null
	val pairEdgeIndex = createPairEdgeIndex(graph)

	p.objectiveFn = fun(solution: Solution): Score {
		val selected = solution.asSubset()
		var maxDistance = 0.0
		for(i in 0 until graph.vertices.size) {
			var minDistance: Double = Inf
			val weight = cfg.vertexWeight[i]
			for(j in selected) {
				val edgeIndex = pairEdgeIndex[Pair(i, j)] ?: continue
				val score = cfg.edgeWeight[edgeIndex] * weight
				minDistance = min(minDistance, score)
			}
			maxDistance = max(maxDistance, minDistance)
		}
		return maxDistance
	}
	return p
}

fun createPairEdgeIndex(graph: Graph): Map<Pair<Int, Int>, Int> {
	val pairEdgeIndex = mutableMapOf<Pair<Int, Int>, Int>()
	for((i, edge) in graph.edges.withIndex()) {
		val (v1, v2) = edge
		val x1 = graph.indexOf(v1)
		val x2 = graph.indexOf(v2)
		pairEdgeIndex[Pair(x1, x2)] = i
		pairEdgeIndex[Pair(x2, x1)] = i
	}
	return pairEdgeIndex
}