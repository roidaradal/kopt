package problem

import data.GraphCfg
import data.newName
import discrete.Domain
import discrete.Goal
import discrete.Problem
import discrete.ProblemType
import discrete.Solution
import discrete.Variables
import fn.Constraint
import fn.ScoreFn
import fn.StringFn
import fn.asSubset

fun newGraphMatching(variant: String, n: Int): Problem? {
	val name = newName(GraphMatching, variant, n)
	return when (variant) {
		"cardinal" -> maxCardinalityMatching(name)
		"weighted" -> maxWeightMatching(name)
		"rainbow" -> rainbowMatching(name)
		else -> null
	}
}

fun newGraphMatchingProblem(name: String): Pair<Problem?, GraphCfg?> {
	val cfg = GraphCfg.undirected(name) ?: return Pair(null, null)
	val graph = cfg.graph
	val p = Problem(
		name,
		description = cfg.toString(),
		type = ProblemType.SUBSET,
		goal = Goal.MAXIMIZE,
		variables = Variables.from(graph.edges),
		solutionStringFn = StringFn.subset(graph.edgeNames),
	)
	p.addVariableDomains(Domain.boolean())
	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		val count = mutableMapOf<String, Int>()
		for(x in solution.asSubset()) {
			val (v1, v2) = graph.edges[x]
			count[v1] = (count[v1] ?: 0) + 1
			count[v2] = (count[v2] ?: 0) + 1
		}
		return count.values.all { it == 1}
	})
	return Pair(p, cfg)
}

fun maxCardinalityMatching(name: String): Problem? {
	val (p, _) = newGraphMatchingProblem(name)
	if(p == null) return null
	p.objectiveFn = ScoreFn::subsetSize
	return p
}

fun maxWeightMatching(name: String): Problem? {
	val (p, cfg) = newGraphMatchingProblem(name)
	if(p == null || cfg == null) return null
	val graph = cfg.graph
	if(graph.edges.size != cfg.edgeWeight.size) return null
	p.objectiveFn = ScoreFn.sumWeightedValues(p.variables, cfg.edgeWeight)
	return p
}

fun rainbowMatching(name: String): Problem? {
	val (p, cfg) = newGraphMatchingProblem(name)
	if(p == null || cfg == null) return null
	val graph = cfg.graph
	if(graph.edges.size != cfg.edgeColor.size) return null
	p.addUniversalConstraint(Constraint.isRainbowColored(cfg.edgeColor))
	p.objectiveFn = ScoreFn::subsetSize
	return p
}