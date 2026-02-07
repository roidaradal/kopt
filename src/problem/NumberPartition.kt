package problem

import data.Numbers
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
import fn.partitionSums
import kotlin.math.absoluteValue

fun newNumberPartition(variant: String, n: Int): Problem? {
	val name = newName(NumberPartition, variant, n)
	return when(variant) {
		"basic" -> numberPartition(name)
		else -> null
	}
}
fun numberPartition(name: String): Problem? {
	val cfg = Numbers.new(name) ?: return null
	val domain = Domain.range(1,2)
	val p = Problem(
		name,
		description = cfg.toString(),
		type = ProblemType.PARTITION,
		variables = Variables.from(cfg.numbers),
		goal = Goal.MINIMIZE,
		solutionCoreFn = CoreFn.sortedPartition(domain, cfg.numbers),
		solutionStringFn = StringFn.partition(domain, cfg.numbers),
	)
	p.addVariableDomains(domain)
	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		if (p.isOptimization) return true
		val sums = solution.partitionSums(domain, cfg.numbers)
		return sums.toSet().size == 1
	})
	p.objectiveFn = fun(solution: Solution): Score {
		val sums = solution.partitionSums(domain, cfg.numbers)
		return (sums[0] - sums[1]).absoluteValue
	}
	return p
}