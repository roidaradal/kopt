package problem

import data.graphEdges
import data.newName
import discrete.Goal
import discrete.Problem
import discrete.Solution
import fn.ScoreFn
import fn.asSubset
import fn.connectedComponents
import fn.mapList

fun newKCut(variant: String, n: Int): Problem? {
	val name = newName(KCut, variant, n)
	return when (variant) {
		"min" -> minKCut(name)
		"max" -> maxKCut(name)
		else -> null
	}
}

fun newKCutProblem(name: String, countTest:(Int, Int) -> Boolean): Problem? {
	val (p, cfg) = newGraphSubsetProblem(name, ::graphEdges)
	if (p == null || cfg == null || cfg.k == 0) return null
	if (cfg.graph.edges.size != cfg.edgeWeight.size) return null
	val graph = cfg.graph

	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		val cutEdges = solution.asSubset().mapList(graph.edges).toSet()
		val activeEdges = graph.edges.toSet() - cutEdges
		val components = graph.connectedComponents(activeEdges)
		return countTest(components.size, cfg.k)
	})
	p.objectiveFn = ScoreFn.sumWeightedValues(p.variables, cfg.edgeWeight)
	return p
}

fun minKCut(name: String): Problem? {
	val p = newKCutProblem(name) { numComponents, k -> numComponents >= k } ?: return null
	p.goal = Goal.MINIMIZE
	return p
}

fun maxKCut(name: String): Problem? {
	val p = newKCutProblem(name) { numComponents, k -> numComponents == k } ?: return null
	p.goal = Goal.MAXIMIZE
	return p
}