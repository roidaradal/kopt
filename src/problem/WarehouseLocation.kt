package problem

import data.Warehouse
import data.newName
import discrete.Domain
import discrete.Goal
import discrete.Problem
import discrete.ProblemType
import discrete.Score
import discrete.Solution
import discrete.Variables
import fn.StringFn
import fn.tallyValues

fun newWarehouseLocation(variant: String, n: Int): Problem? {
	val name = newName(WarehouseLocation, variant, n)
	return when (variant) {
		"basic" -> warehouseLocation(name)
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