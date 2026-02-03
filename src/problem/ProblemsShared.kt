package problem

import data.Bins
import discrete.Problem
import discrete.ProblemType
import discrete.Variables
import fn.coreSortedPartition
import fn.scoreCountUniqueValues
import fn.stringPartition

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