package problem

import data.Subsets
import data.newName
import discrete.Domain
import discrete.Goal
import discrete.Problem
import discrete.ProblemType
import discrete.Score
import discrete.Solution
import discrete.Variables
import fn.CoreFn
import fn.StringFn
import fn.partitionStrings

fun newSetSplitting(variant: String, n: Int): Problem? {
	val name= newName(SetSplitting, variant, n)
	return when (variant) {
		"basic" -> setSplitting(name)
		else -> null
	}
}
fun setSplitting(name: String): Problem? {
	val cfg = Subsets.new(name) ?: return null
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
	p.objectiveFn = fun(solution: Solution): Score {
		val partitions = solution.partitionStrings(domain, cfg.universal)
		val part1 = partitions[0].toSet()
		val part2 = partitions[1].toSet()
		var count: Score = 0.0
		for(s in cfg.subsets) {
			val subset = s.toSet()
			val diff1 = (subset - part1).size
			val diff2 = (subset - part2).size
			if (diff1 > 0 && diff2 > 0) count += 1
		}
		return count
	}
	return p
}