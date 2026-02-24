package problem

import data.Warehouse
import data.newName
import discrete.Domain
import discrete.Goal
import discrete.Inf
import discrete.Problem
import discrete.ProblemType
import discrete.Score
import discrete.Solution
import discrete.Variables
import fn.StringFn
import fn.asSubset
import fn.tallyValues
import kotlin.math.max
import kotlin.math.min

fun newWarehouseLocation(variant: String, n: Int): Problem? {
	val name = newName(WarehouseLocation, variant, n)
	return when (variant) {
		"basic" -> warehouseLocation(name)
		"minimax" -> minimaxWarehouseLocation(name)
		"maxmin" -> maxminWarehouseLocation(name)
		else -> null
	}
}

fun warehouseLocation(name: String) : Problem? {
	val cfg = Warehouse.new(name) ?: return null
	val p = Problem(
		name,
		description = cfg.toString(),
		type = ProblemType.ASSIGNMENT,
		goal = Goal.MINIMIZE,
		variables = Variables.from(cfg.stores),
	)
	val domain = Domain.from(cfg.warehouses)
	p.addVariableDomains(domain)

	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		val usage = solution.tallyValues(domain)
		return domain.mapIndexed { i, w ->  (usage[w] ?: 0) <= cfg.capacity[i] }.all { it }
	})

	p.objectiveFn = fun(solution: Solution): Score {
		var totalCost: Score = 0.0
		val usage = solution.tallyValues(domain)
		for((i, w) in domain.withIndex()) {
			if ((usage[w] ?: 0) > 0) totalCost += cfg.warehouseCost[i]
		}
		for((x,w) in solution.map) {
			totalCost += cfg.storeCost[x][w]
		}
		return totalCost
	}

	p.solutionStringFn = StringFn.partition(domain, cfg.stores)
	return p
}

fun newWarehouseSubsetProblem(name: String): Pair<Problem?, Warehouse?> {
	val cfg = Warehouse.new(name) ?: return Pair(null, null)
	val p = Problem(
		name,
		description = "Count: ${cfg.count}\nDistance: ${cfg.distance}",
		type = ProblemType.SUBSET,
		variables = Variables.from(cfg.warehouses),
		solutionStringFn = StringFn.subset(cfg.warehouses),
	)
	p.addVariableDomains(Domain.boolean())
	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		return solution.asSubset().size == cfg.count
	})
	return Pair(p, cfg)
}

fun minimaxWarehouseLocation(name: String): Problem? {
	val (p, cfg) = newWarehouseSubsetProblem(name)
	if (p == null || cfg == null) return null

	p.goal = Goal.MINIMIZE
	p.objectiveFn = fun(solution: Solution): Score {
		var maxDistance = 0.0
		val warehouses = solution.asSubset()
		for(store in cfg.stores.indices) {
			for(warehouse in warehouses) {
				maxDistance = max(maxDistance, cfg.distance[warehouse][store])
			}
		}
		return maxDistance
	}
	return p
}

fun maxminWarehouseLocation(name: String): Problem? {
	val (p, cfg) = newWarehouseSubsetProblem(name)
	if (p == null || cfg == null) return null

	p.goal = Goal.MAXIMIZE
	p.objectiveFn = fun(solution: Solution): Score {
		var minDistance = Inf
		val warehouses = solution.asSubset()
		for(store in cfg.stores.indices) {
			for(warehouse in warehouses) {
				minDistance = min(minDistance, cfg.distance[warehouse][store])
			}
		}
		return minDistance
	}
	return p
}
