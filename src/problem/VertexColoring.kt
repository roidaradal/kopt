package problem

import data.GraphColoring
import data.combinations
import data.graphColors
import data.graphVertices
import data.newName
import discrete.Goal
import discrete.Problem
import discrete.Solution
import fn.Constraint
import fn.CoreFn
import fn.ScoreFn

fun newVertexColoring(variant: String, n: Int): Problem? {
	val name = newName(VertexColoring, variant, n)
	return when (variant) {
		"basic" -> vertexColoring(name)
		"complete" -> completeColoring(name)
		"harmonious" -> harmoniousColoring(name)
		else -> null
	}
}

fun newVertexColoringProblem(name: String): Pair<Problem?, GraphColoring?> {
	val (p, cfg) = newGraphColoringProblem(name, ::graphVertices, ::graphColors)
	if (p == null || cfg == null || cfg.colors.isEmpty()) return Pair(null, null)

	p.addUniversalConstraint(Constraint.properVertexColoring(cfg.graph))
	p.objectiveFn = ScoreFn::countUniqueValues
	p.solutionCoreFn = CoreFn.lookupValueOrder(p)
	return Pair(p, cfg)
}

fun vertexColoring(name: String): Problem? {
	val (p, _) = newVertexColoringProblem(name)
	return p
}

fun completeColoring(name: String): Problem? {
	val (p, cfg) = newVertexColoringProblem(name)
	if (p == null || cfg == null) return null
	val graph = cfg.graph

	p.goal = Goal.MAXIMIZE
	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		val color = solution.map
		val count = mutableMapOf<IntPair, Int>()
		for(pair in p.uniformDomain.combinations(2)) {
			val key = IntPair(pair[0], pair[1])
			count[key] = 0
		}
		for((v1, v2) in graph.edges) {
			val c1 = color[graph.indexOf(v1)] ?: return false
			val c2 = color[graph.indexOf(v2)] ?: return false
			var key = IntPair(c1, c2)
			if (!count.containsKey(key)) key = IntPair(c2, c1)
			count[key] = (count[key] ?: 0) + 1
		}
		return count.values.all { it >= 1 }
	})
	return p
}

fun harmoniousColoring(name: String): Problem? {
	val (p, cfg) = newVertexColoringProblem(name)
	if (p == null || cfg == null) return null
	val graph = cfg.graph

	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		val color = solution.map
		val count = mutableMapOf<IntPair, Int>()
		for((v1, v2) in graph.edges) {
			val c1 = color[graph.indexOf(v1)] ?: return false
			val c2 = color[graph.indexOf(v2)] ?: return false
			val colors = listOf(c1, c2).sorted()
			val key = IntPair(colors[0], colors[1])
			count[key] = (count[key] ?: 0) + 1
		}
		return count.values.all {it <= 1}
	})
	return p
}