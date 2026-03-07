package problem

import data.GraphCfg
import data.graphEdges
import data.newName
import data.spanTerminals
import discrete.Goal
import discrete.Problem
import discrete.Solution
import fn.asSubset
import fn.spannedVertices

fun newSteinerTree(variant: String, n: Int): Problem? {
	val name = newName(SteinerTree, variant, n)
	return when (variant) {
		"basic" -> steinerTree(name)
		"degree" -> degreeConstrainedSteinerTree(name)
		"group" -> groupSteinerTree(name)
		else -> null
	}
}

fun newSteinerTreeProblem(name: String): Pair<Problem?, GraphCfg?> {
	val (problem, cfg) = newSpanningTreeProblem(name, ::spanTerminals)
	val p = edgeWeightedProblem(problem, cfg)
	if (p == null || cfg == null || cfg.terminals.isEmpty()) return Pair(null, null)
	return Pair(p, cfg)
}

fun steinerTree(name: String): Problem? {
	val (p, _) = newSteinerTreeProblem(name)
	return p
}

fun degreeConstrainedSteinerTree(name: String): Problem? {
	val (p, cfg) = newSteinerTreeProblem(name)
	if(p == null || cfg == null || cfg.k == 0) return null
	val graph = cfg.graph

	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		val activeEdges = solution.asSubset().map { graph.edges[it] }.toSet()
		for(vertex in graph.vertices) {
			val neighbors = graph.activeNeighbors(vertex, activeEdges)
			if (neighbors.size > cfg.k) return false
		}
		return true
	})

	return p
}

fun groupSteinerTree(name: String): Problem? {
	val (problem, cfg) = newGraphSubsetProblem(name, ::graphEdges)
	val p = edgeWeightedProblem(problem, cfg)
	if(p == null || cfg == null || cfg.groups.isEmpty()) return null
	p.goal = Goal.MINIMIZE

	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		val spannedVertices = solution.spannedVertices(cfg.graph) ?: return false
		for(group in cfg.groups) {
			if(group.toSet().intersect(spannedVertices).isEmpty()) return false
		}
		return true
	})
	return p
}