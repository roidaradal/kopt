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
import fn.asPathOrder
import fn.increment
import fn.mapList

fun newTravelingPurchaser(variant: String, n: Int): Problem? {
	val name = newName(TravelingPurchaser, variant, n)
	return when (variant) {
		"basic" -> travelingPurchaser(name)
		else -> null
	}
}

fun travelingPurchaser(name: String): Problem? {
	val cfg = GraphPath.tour(name) ?: return null

	val itemMarkets = mutableListOf<IntPair>()
	val names = mutableListOf<String>()
	for ((i, item) in cfg.items.withIndex()) {
		for((m, market) in cfg.vertices.withIndex()) {
			if (cfg.cost[i][m] == Inf) continue
			itemMarkets.add(IntPair(i, m))
			names.add("$item@$market")
		}
	}
	val p = Problem(
		name,
		description = cfg.toString(),
		type = ProblemType.PATH,
		goal = Goal.MINIMIZE,
		variables = Variables.from(itemMarkets)
	)
	p.addVariableDomains(Domain.path(itemMarkets.size))

	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		val covered = cfg.items.associateWith { 0 }.toMutableMap()
		for ((idx, order) in solution.map) {
			if (order < 0) continue
			val (itemIdx, _) = itemMarkets[idx]
			covered.increment(cfg.items[itemIdx])
		}
		return covered.values.all { it == 1 }
	})

	p.objectiveFn = fun(solution: Solution): Score {
		var totalCost: Score = 0.0
		val path = solution.asPathOrder()
		if (path.isEmpty()) return totalCost
		for(idx in path) {
			val (item, market) = itemMarkets[idx]
			totalCost += cfg.cost[item][market]
		}
		for(i in 0 until path.lastIndex) {
			val (_, market1) = itemMarkets[path[i]]
			val (_, market2) = itemMarkets[path[i+1]]
			totalCost += cfg.distance[market1][market2]
		}
		val (_, marketStart) = itemMarkets[path.first()]
		val (_, marketEnd) = itemMarkets[path.last()]
		totalCost += cfg.fromOrigin[marketStart]
		totalCost += cfg.toOrigin[marketEnd]
		return totalCost
	}

	p.solutionStringFn = fun(solution: Solution): String {
		return solution.asPathOrder().mapList(names).joinToString(separator = " -> ")
	}

	return p
}