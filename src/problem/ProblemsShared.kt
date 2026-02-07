package problem

import data.Bins
import data.GraphCfg
import data.GraphVariablesFn
import data.Numbers
import data.Subsets
import discrete.Domain
import discrete.Goal
import discrete.Problem
import discrete.ProblemType
import discrete.Variables
import fn.CoreFn
import fn.ScoreFn
import fn.StringFn

typealias IntPair = Pair<Int, Int>

fun newBinPartitionProblem(name: String): Pair<Problem?, Bins?> {
	val cfg = Bins.new(name) ?: return Pair(null, null)
	val p = Problem(
		name,
		description = cfg.toString(),
		type = ProblemType.PARTITION,
		variables = Variables.from(cfg.weight),
		objectiveFn = ScoreFn::countUniqueValues,
		solutionCoreFn = CoreFn.sortedPartition(cfg.bins, cfg.weight),
		solutionStringFn = StringFn.partition(cfg.bins, cfg.weight),
	)
	p.addVariableDomains(cfg.bins)
	return Pair(p, cfg)
}

fun newSubsetsProblem(name: String): Pair<Problem?, Subsets?> {
	val cfg = Subsets.new(name) ?: return Pair(null, null)
	val p = Problem(
		name,
		description = cfg.toString(),
		type = ProblemType.SUBSET,
		variables = Variables.from(cfg.names),
		objectiveFn = ScoreFn::subsetSize,
		solutionStringFn = StringFn.subset(cfg.names),
	)
	p.addVariableDomains(Domain.boolean())
	return Pair(p, cfg)
}

fun newNumbersSubsetProblem(name: String): Pair<Problem?, Numbers?> {
	val cfg = Numbers.new(name) ?: return Pair(null, null)
	val p = Problem(
		name,
		description = cfg.toString(),
		type = ProblemType.SUBSET,
		variables = Variables.from(cfg.numbers),
		solutionStringFn = StringFn.subset(cfg.numbers),
	)
	p.addVariableDomains(Domain.boolean())
	return Pair(p, cfg)
}

fun newGraphSubsetProblem(name: String, variablesFn: GraphVariablesFn): Pair<Problem?, GraphCfg?> {
	val cfg = GraphCfg.undirected(name) ?: return Pair(null, null)
	val variables = variablesFn(cfg.graph)
	val p = Problem(
		name,
		description = cfg.toString(),
		type = ProblemType.SUBSET,
		variables = Variables.from(variables),
		objectiveFn = ScoreFn::subsetSize,
		solutionStringFn = StringFn.subset(variables),
	)
	p.addVariableDomains(Domain.boolean())
	return Pair(p, cfg)
}

fun newGraphCoverProblem(name: String, variablesFn: GraphVariablesFn): Pair<Problem?, GraphCfg?> {
	val (p, cfg) = newGraphSubsetProblem(name, variablesFn)
	if(p == null || cfg == null) return Pair(null, null)
	p.goal = Goal.MINIMIZE
	return Pair(p, cfg)
}