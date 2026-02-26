package problem

import data.Subsets
import data.newName
import discrete.Domain
import discrete.Goal
import discrete.Problem
import discrete.ProblemType
import discrete.Score
import discrete.Solution
import discrete.Value
import discrete.Variables
import fn.CoreFn
import fn.StringFn
import fn.partitionStrings

fun newSetSplitting(variant: String, n: Int): Problem? {
	val name= newName(SetSplitting, variant, n)
	return when (variant) {
		"basic" -> setSplitting(name)
		"weighted" -> weightedSetSplitting(name)
		else -> null
	}
}

fun newSetSplittingProblem(name: String): Pair<Problem?, Subsets?> {
	val cfg = Subsets.new(name) ?: return Pair(null, null)
	val domain = Domain.range(1,2)
	val p = Problem(
		name,
		description = cfg.toString(),
		type = ProblemType.PARTITION,
		goal = Goal.MAXIMIZE,
		variables = Variables.from(cfg.universal),
		solutionCoreFn = CoreFn.sortedPartition(domain, cfg.universal),
		solutionStringFn = StringFn.partition(domain, cfg.universal),
	)
	p.addVariableDomains(domain)
	return Pair(p, cfg)
}

fun setSplitting(name: String): Problem? {
	val (p, cfg) = newSetSplittingProblem(name)
	if (p == null || cfg == null) return null

	p.objectiveFn = fun(solution: Solution): Score {
		val subsets = splitSubsets(solution, cfg, p.uniformDomain)
		return subsets.size.toDouble()
	}
	return p
}

fun weightedSetSplitting(name: String): Problem? {
	val (p, cfg) = newSetSplittingProblem(name)
	if (p == null || cfg == null) return null
	if (cfg.weight.size != cfg.names.size) return null

	p.objectiveFn = fun(solution: Solution): Score {
		val subsets = splitSubsets(solution, cfg, p.uniformDomain)
		return subsets.sumOf { cfg.weight[it] ?: 0.0 }
	}
	return p
}

fun splitSubsets(solution: Solution, cfg: Subsets, domain: List<Value>): List<String> {
	val partitions = solution.partitionStrings(domain, cfg.universal)
	val part1 = partitions[0].toSet()
	val part2 = partitions[1].toSet()
	val splitSubsets = mutableListOf<String>()
	for((i, s) in cfg.subsets.withIndex()) {
		val subset = s.toSet()
		val diff1 = (subset - part1).size
		val diff2 = (subset - part2).size
		if (diff1 > 0 && diff2 > 0) splitSubsets.add(cfg.names[i])
	}
	return splitSubsets
}