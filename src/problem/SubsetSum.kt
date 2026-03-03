package problem

import data.Numbers
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
import fn.asPartition
import fn.asSubset
import fn.mapList

fun newSubsetSum(variant: String, n: Int): Problem? {
	val name = newName(SubsetSum, variant, n)
	return when (variant) {
		"basic" -> subsetSum(name)
		"max_sum" -> maxSumMultipleSubsetSum(name)
		"max_min" -> maxMinMultipleSubsetSum(name)
		else -> null
	}
}

fun subsetSum(name: String): Problem? {
	val (p, cfg) = newNumbersSubsetProblem(name)
	if (p == null || cfg == null || cfg.target == 0) return null

	p.description += "\nTarget: ${cfg.target}"
	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		val total = solution.asSubset().mapList(cfg.numbers).sum()
		return if (p.isSatisfaction) (total == cfg.target) else (total <= cfg.target)
	})

	p.goal = Goal.MINIMIZE
	p.objectiveFn = fun(solution: Solution): Score {
		val total = solution.asSubset().mapList(cfg.numbers).sum()
		return if (total > cfg.target) Inf else (cfg.target - total).toDouble()
	}
	return p
}

fun newMultipleSubsetSumProblem(name: String): Pair<Problem?, Numbers?> {
	val cfg = Numbers.new(name) ?: return Pair(null, null)
	if (cfg.target == 0 || cfg.numBins == 0) return Pair(null, null)
	val validDomain = Domain.range(1, cfg.numBins)

	val p = Problem(
		name = name,
		description = cfg.toString() + "\nNumBins: ${cfg.numBins}",
		type = ProblemType.PARTITION,
		goal = Goal.MAXIMIZE,
		variables = Variables.from(cfg.numbers),
		solutionStringFn = StringFn.partition(validDomain, cfg.numbers),
	)
	p.addVariableDomains(Domain.range(0, cfg.numBins))

	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		for(partition in solution.asPartition(validDomain)) {
			val total = partition.sumOf { cfg.numbers[it] }
			if (total > cfg.target) return false
		}
		return true
	})

	return Pair(p, cfg)
}

fun maxSumMultipleSubsetSum(name: String): Problem? {
	val (p, cfg) = newMultipleSubsetSumProblem(name)
	if (p == null || cfg == null) return null
	val validDomain = Domain.range(1, cfg.numBins)

	p.objectiveFn = fun(solution: Solution): Score {
		return solution.asPartition(validDomain).sumOf { partition ->
			partition.mapList(cfg.numbers).sum()
		}.toDouble()
	}
	return p
}

fun maxMinMultipleSubsetSum(name: String): Problem? {
	val (p, cfg) = newMultipleSubsetSumProblem(name)
	if (p == null || cfg == null) return null
	val validDomain = Domain.range(1, cfg.numBins)

	p.objectiveFn = fun(solution: Solution): Score {
		return solution.asPartition(validDomain).minOf { partition ->
			partition.mapList(cfg.numbers).sum()
		}.toDouble()
	}
	return p
}