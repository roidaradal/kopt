package problem

import data.GraphPath
import data.newName
import discrete.Domain
import discrete.Goal
import discrete.Problem
import discrete.ProblemType
import discrete.Score
import discrete.Solution
import discrete.Variables
import fn.Constraint
import fn.ScoreFn
import fn.StringFn
import fn.pathDistances

fun newGraphPath(variant: String, n: Int): Problem? {
	val name = newName(GraphPathing, variant, n)
	return when (variant) {
		"longest" -> longestPath(name)
		"minimax" -> minimaxPath(name)
		"shortest" -> shortestPath(name)
		"widest" -> widestPath(name)
		else -> null
	}
}

fun newGraphPathProblem(name: String): Pair<Problem?, GraphPath?> {
	val cfg = GraphPath.new(name) ?: return Pair(null, null)
	val description = "$cfg\nStart: ${cfg.vertices[cfg.start]}, End: ${cfg.vertices[cfg.end]}"
	val p = Problem(
		name,
		description = description,
		type = ProblemType.PATH,
		variables = Variables.from(cfg.between),
		solutionStringFn = StringFn.graphPath(cfg),
	)
	p.addVariableDomains(Domain.path(cfg.between.size))
	p.addUniversalConstraint(Constraint.simplePath(cfg))
	return Pair(p, cfg)
}

fun longestPath(name: String): Problem? {
	val (p, cfg) = newGraphPathProblem(name)
	if (p == null || cfg == null) return null
	p.goal = Goal.MAXIMIZE
	p.objectiveFn = ScoreFn.pathCost(cfg)
	return p
}

fun shortestPath(name: String): Problem? {
	val (p, cfg) = newGraphPathProblem(name)
	if (p == null || cfg == null) return null
	p.goal = Goal.MINIMIZE
	p.objectiveFn = ScoreFn.pathCost(cfg)
	return p
}

fun minimaxPath(name: String): Problem? {
	val (p, cfg) = newGraphPathProblem(name)
	if (p == null || cfg == null) return null
	p.goal = Goal.MINIMIZE
	p.objectiveFn = fun(solution: Solution): Score {
		return solution.pathDistances(cfg).max()
	}
	return p
}

fun widestPath(name: String): Problem? {
	val (p, cfg) = newGraphPathProblem(name)
	if (p == null || cfg == null) return null
	p.goal = Goal.MAXIMIZE
	p.objectiveFn = fun(solution: Solution): Score {
		return solution.pathDistances(cfg).min()
	}
	return p
}