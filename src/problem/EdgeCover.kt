package problem

import data.GraphCfg
import data.graphEdges
import data.newName
import discrete.Problem
import fn.Constraint
import fn.ScoreFn

fun newEdgeCover(variant: String, n: Int): Problem? {
	val name = newName(EdgeCover, variant, n)
	return when (variant) {
		"basic" -> edgeCover(name)
		"weighted" -> weightedEdgeCover(name)
		else -> null
	}
}

fun newEdgeCoverProblem(name: String): Pair<Problem?, GraphCfg?> {
	val (p, cfg) = newGraphCoverProblem(name, ::graphEdges)
	if (p == null || cfg == null) return Pair(null, null)
	val graph = cfg.graph
	p.addUniversalConstraint(Constraint.allVerticesCovered(graph, graph.vertices))
	return Pair(p, cfg)
}

fun edgeCover(name: String): Problem? {
	val (p, _) = newEdgeCoverProblem(name)
	return p
}

fun weightedEdgeCover(name: String): Problem? {
	val (p, cfg) = newEdgeCoverProblem(name)
	if (p == null || cfg == null) return null
	if (cfg.edgeWeight.size != cfg.graph.edges.size) return null
	p.objectiveFn = ScoreFn.sumWeightedValues(p.variables, cfg.edgeWeight)
	return p
}