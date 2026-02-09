package problem

import data.newName
import data.spanTerminals
import discrete.Problem

fun newSteinerTree(variant: String, n: Int): Problem? {
	val name = newName(SteinerTree, variant, n)
	return when (variant) {
		"basic" -> steinerTree(name)
		else -> null
	}
}

fun steinerTree(name: String): Problem? {
	val (problem, cfg) = newSpanningTreeProblem(name, ::spanTerminals)
	val p = edgeWeightedProblem(problem, cfg)
	if (p == null || cfg == null || cfg.terminals.isEmpty()) return null
	return p
}