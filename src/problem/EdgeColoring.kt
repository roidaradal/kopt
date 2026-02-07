package problem

import data.graphColors
import data.graphEdges
import data.newName
import discrete.Problem
import discrete.Solution
import fn.CoreFn
import fn.ScoreFn
import fn.isAllUnique

fun newEdgeColoring(variant: String, n: Int): Problem? {
	val name = newName(EdgeColoring, variant, n)
	return when (variant) {
		"basic" -> edgeColoring(name)
		else -> null
	}
}

fun edgeColoring(name: String): Problem? {
	val (p, cfg) = newGraphColoringProblem(name, ::graphEdges, ::graphColors)
	if (p == null || cfg == null || cfg.colors.isEmpty()) return null
	val graph = cfg.graph

	val edgeIndex = graph.edges.mapIndexed { index, edge -> edge to index  }.toMap()

	p.description += "\nColors: ${cfg.colors}"
	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		val color = solution.map
		return graph.vertices.all { vertex ->
			val edgeColors = graph.edgesOf(vertex).map { edge ->
				val index = edgeIndex[edge] ?: -1
				color[index] ?: -1
			}
			edgeColors.isAllUnique()
		}
	})
	p.objectiveFn = ScoreFn::countUniqueValues
	p.solutionCoreFn = CoreFn.lookupValueOrder(p)
	return p
}