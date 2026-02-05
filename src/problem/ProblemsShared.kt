package problem

import data.Bins
import data.Numbers
import data.Subsets
import discrete.Domain
import discrete.Problem
import discrete.ProblemType
import discrete.Variables
import fn.coreSortedPartition
import fn.scoreCountUniqueValues
import fn.scoreSubsetSize
import fn.stringPartition
import fn.stringSubset

fun newBinPartitionProblem(name: String): Pair<Problem?, Bins?> {
	val cfg = Bins.new(name) ?: return Pair(null, null)
	val description = "Bins: ${cfg.bins}\nCapacity: ${cfg.capacity}\nWeight: ${cfg.weight}"
	val p = Problem(
		name,
		description = description,
		type = ProblemType.Partition,
		variables = Variables.from(cfg.weight),
		objectiveFn = ::scoreCountUniqueValues,
		solutionCoreFn = coreSortedPartition(cfg.bins, cfg.weight),
		solutionStringFn = stringPartition(cfg.bins, cfg.weight),
	)
	p.addVariableDomains(cfg.bins)
	return Pair(p, cfg)
}

fun newSubsetsProblem(name: String): Pair<Problem?, Subsets?> {
	val cfg = Subsets.new(name) ?: return Pair(null, null)
	val description = "Universal: ${cfg.universal}\nNames: ${cfg.names}\nSubsets: ${cfg.subsets}"
	val p = Problem(
		name,
		description = description,
		type = ProblemType.Subset,
		variables = Variables.from(cfg.names),
		objectiveFn = ::scoreSubsetSize,
		solutionStringFn = stringSubset(cfg.names),
	)
	p.addVariableDomains(Domain.boolean())
	return Pair(p, cfg)
}

fun newNumbersSubsetProblem(name: String): Pair<Problem?, Numbers?> {
	val cfg = Numbers.new(name) ?: return Pair(null, null)
	val description = "Numbers: ${cfg.numbers}"
	val p = Problem(
		name,
		description = description,
		type = ProblemType.Subset,
		variables = Variables.from(cfg.numbers),
		solutionStringFn = stringSubset(cfg.numbers),
	)
	p.addVariableDomains(Domain.boolean())
	return Pair(p, cfg)
}
