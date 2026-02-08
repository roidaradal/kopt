package problem

import data.GraphCfg
import data.graphVertices
import data.newName
import discrete.Goal
import discrete.Problem
import discrete.Solution
import fn.Constraint
import fn.asSubset
import fn.mapList

fun newIndependentSet(variant: String, n: Int): Problem? {
	val name = newName(IndependentSet, variant, n)
	return when (variant) {
		"basic" -> independentSet(name)
		"rainbow" -> rainbowIndependentSet(name)
		else -> null
	}
}

fun newIndependentSetProblem(name: String): Pair<Problem?, GraphCfg?> {
	val (p, cfg) = newGraphSubsetProblem(name, ::graphVertices)
	if(p == null || cfg == null) return Pair(null, null)
	val graph = cfg.graph

	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		val vertices = solution.asSubset().mapList(graph.vertices)
		val vertexSet = vertices.toSet()
		for(vertex in vertices) {
			val adjacent = graph.neighborsOf(vertex)
			if(vertexSet.intersect(adjacent).isNotEmpty()) return false
		}
		return true
	})
	p.goal = Goal.MAXIMIZE
	return Pair(p, cfg)
}

fun independentSet(name: String): Problem? {
	val (p, _) = newIndependentSetProblem(name)
	return p
}

fun rainbowIndependentSet(name: String): Problem? {
	val (p, cfg) = newIndependentSetProblem(name)
	if (p == null || cfg == null) return null
	val graph = cfg.graph
	if (graph.vertices.size != cfg.vertexColor.size) return null
	p.addUniversalConstraint(Constraint.isRainbowColored(cfg.vertexColor))
	return p
}