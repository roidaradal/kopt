package problem

import data.GraphCfg
import data.graphVertices
import data.newName
import discrete.Goal
import discrete.Problem
import discrete.Solution
import fn.asSubset
import fn.isClique
import fn.mapList

fun newClique(variant: String, n: Int): Problem? {
	val name = newName(Clique, variant, n)
	return when (variant) {
		"basic" -> clique(name)
		"k" -> kClique(name)
		else -> null
	}
}

fun newCliqueProblem(name: String): Pair<Problem?, GraphCfg?> {
	val (p, cfg) = newGraphSubsetProblem(name, ::graphVertices)
	if(p == null || cfg == null) return Pair(null, null)
	val graph = cfg.graph

	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		val vertices = solution.asSubset().mapList(graph.vertices)
		return graph.isClique(vertices)
	})
	p.goal = Goal.MAXIMIZE
	return Pair(p, cfg)
}

fun clique(name: String): Problem? {
	val (p, _) = newCliqueProblem(name)
	return p
}

fun kClique(name: String): Problem? {
	val (p, cfg) = newCliqueProblem(name)
	if(p == null || cfg == null || cfg.k == 0) return null

	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		return solution.asSubset().size == cfg.k
	})
	p.goal = Goal.SATISFY
	p.objectiveFn = null
	return p
}