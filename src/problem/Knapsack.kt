package problem

import data.KnapsackCfg
import data.newName
import discrete.Domain
import discrete.Goal
import discrete.Problem
import discrete.ProblemType
import discrete.Score
import discrete.Solution
import discrete.Variables
import fn.asSubset
import fn.mapList
import fn.scoreSumWeightedValues
import fn.stringSubset

fun newKnapsack(variant: String, n: Int): Problem? {
	val name = newName(Knapsack, variant, n)
	return when (variant) {
		"basic" -> knapsack(name)
		"quadratic" -> quadraticKnapsack(name)
		else -> null
	}
}

fun newKnapsackProblem(name: String): Pair<Problem?, KnapsackCfg?> {
	val cfg = KnapsackCfg.new(name) ?: return Pair(null, null)
	val description = "Capacity: ${cfg.capacity}\nItems: ${cfg.items}\nWeight: ${cfg.weight}\nValue: ${cfg.value}"
	val p = Problem(
		name,
		description = description,
		type = ProblemType.Subset,
		goal = Goal.Maximize,
		variables = Variables.from(cfg.items),
		solutionStringFn = stringSubset(cfg.items),
	)
	p.addVariableDomains(Domain.boolean())
	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		val count = solution.map
		val weights = p.variables.map { (count[it] ?: 0) * cfg.weight[it] }
		return weights.sum() <= cfg.capacity
	})
	return Pair(p, cfg)
}

fun knapsack(name: String): Problem? {
	val (p, cfg) = newKnapsackProblem(name)
	if (p == null || cfg == null) return null

	p.objectiveFn = scoreSumWeightedValues(p.variables, cfg.value)
	return p
}

fun quadraticKnapsack(name: String): Problem? {
	val (p, cfg) = newKnapsackProblem(name)
	if (p == null || cfg == null) return null

	p.description += "\nPairBonus: ${cfg.pairBonus}"
	p.objectiveFn = fun(solution: Solution): Score {
		val baseValue = scoreSumWeightedValues(p.variables, cfg.value).invoke(solution)
		val selected = solution.asSubset().mapList(cfg.items).toSet()
		var bonusValue = 0.0
		for ((pair, bonus) in cfg.pairBonus) {
			val (item1, item2) = pair
			if(selected.contains(item1) && selected.contains(item2)) bonusValue += bonus
		}
		return baseValue + bonusValue
	}
	return p
}