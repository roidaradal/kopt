package problem

import data.GraphCfg
import data.newName
import discrete.Domain
import discrete.Goal
import discrete.Problem
import discrete.ProblemType
import discrete.Score
import discrete.Solution
import discrete.Variables
import fn.asPathOrder
import fn.mapList
import fn.mirroredItems

fun newInducedPath(variant: String, n: Int): Problem? {
	val name = newName(InducedPath, variant, n)
	return when (variant) {
		"basic" -> maxInducedPath(name)
		else -> null
	}
}

fun newInducedPathProblem(name: String): Pair<Problem?, GraphCfg?> {
	val cfg = GraphCfg.undirected(name) ?: return Pair(null, null)
	val graph = cfg.graph
	val p = Problem(
		name,
		description = cfg.toString(),
		type = ProblemType.PATH,
		goal = Goal.MAXIMIZE,
		variables = Variables.from(graph.vertices),
	)
	p.addVariableDomains(Domain.path(graph.vertices.size))
	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		val pathOrder = solution.asPathOrder()
		if (pathOrder.isEmpty()) return false
		for (i in 0 until pathOrder.size-1) {
			val v1 = graph.vertices[pathOrder[i]]
			val v2 = graph.vertices[pathOrder[i+1]]
			val neighbors = graph.neighborsOf(v1)
			if(!neighbors.contains(v2)) return false
			for(j in pathOrder.slice(i+2 .. pathOrder.lastIndex)) {
				if(neighbors.contains(graph.vertices[j])) return false
			}
		}
		return true
	})
	p.solutionStringFn = fun(solution: Solution): String {
		return solution.asPathOrder().mapList(graph.vertices).joinToString(separator = "-")
	}
	p.solutionCoreFn = fun(solution: Solution): String {
		return solution.asPathOrder().mapList(graph.vertices).mirroredItems("-")
	}
	return Pair(p, cfg)
}

fun maxInducedPath(name: String): Problem? {
	val (p, _) = newInducedPathProblem(name)
	if(p == null) return null

	p.objectiveFn = fun(solution: Solution): Score {
		return solution.values.filter { it >= 0 }.size.toDouble()
	}
	return p
}