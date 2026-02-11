package problem

import data.GraphPath
import data.newName
import discrete.Domain
import discrete.Goal
import discrete.Inf
import discrete.Problem
import discrete.ProblemType
import discrete.Score
import discrete.Solution
import discrete.Variables
import fn.Constraint
import fn.CoreFn
import fn.StringFn
import fn.asSequence
import kotlin.math.max

fun newTravelingSalesman(variant: String, n: Int): Problem? {
	val name = newName(TravelingSalesman, variant, n)
	return when (variant) {
		"basic" -> travelingSalesman(name)
		"bottleneck" -> bottleneckTravelingSalesman(name)
		else -> null
	}
}

fun newTravelingSalesmanProblem(name: String): Pair<Problem?, GraphPath?> {
	val cfg = GraphPath.tour(name) ?: return Pair(null, null)
	val p = Problem(
		name,
		description = cfg.toString(),
		type = ProblemType.SEQUENCE,
		goal = Goal.MINIMIZE,
		variables = Variables.from(cfg.vertices),
		solutionCoreFn = CoreFn.sortedCycle(cfg.vertices),
		solutionStringFn = StringFn.sequence(cfg.vertices),
	)
	p.addVariableDomains(Domain.index(cfg.vertices.size))
	p.addUniversalConstraint(Constraint::allUnique)
	return Pair(p, cfg)
}

fun travelingSalesman(name: String): Problem? {
	val (p, cfg) = newTravelingSalesmanProblem(name)
	if (p == null || cfg == null) return null

	p.objectiveFn = fun(solution: Solution): Score {
		val sequence = solution.asSequence().toMutableList()
		sequence.add(sequence[0])
		var totalDistance: Score = 0.0
		for(i in 0 until cfg.vertices.size) {
			val (curr, next) = listOf(sequence[i], sequence[i+1])
			totalDistance += cfg.distance[curr][next]
		}
		return totalDistance
	}
	return p
}

fun bottleneckTravelingSalesman(name: String): Problem? {
	val (p, cfg) = newTravelingSalesmanProblem(name)
	if (p == null || cfg == null) return null

	p.objectiveFn = fun(solution: Solution): Score {
		val sequence = solution.asSequence().toMutableList()
		sequence.add(sequence[0])
		var maxDistance: Score = -Inf
		for(i in 0 until cfg.vertices.size) {
			val (curr, next) = listOf(sequence[i], sequence[i+1])
			maxDistance = max(maxDistance, cfg.distance[curr][next])
		}
		return maxDistance
	}
	return p
}

