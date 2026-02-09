package problem

import data.Bins
import data.GraphCfg
import data.GraphColoring
import data.GraphColorsFn
import data.GraphPartition
import data.GraphSpanFn
import data.GraphVariablesFn
import data.Numbers
import data.Subsets
import data.graphEdges
import discrete.Domain
import discrete.Goal
import discrete.Problem
import discrete.ProblemType
import discrete.Variables
import fn.Constraint
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

fun newGraphPartitionProblem(name: String): Pair<Problem?, GraphPartition?> {
	val cfg = GraphPartition.new(name) ?: return Pair(null, null)
	val graph = cfg.graph
	val numPartitions = if (cfg.numPartitions == 0) graph.vertices.size else cfg.numPartitions
	val domain = Domain.range(1, numPartitions)
	val p = Problem(
		name,
		description = cfg.toString(),
		type = ProblemType.PARTITION,
		variables = Variables.from(graph.vertices),
		objectiveFn = ScoreFn::countUniqueValues,
		solutionCoreFn = CoreFn.sortedPartition(domain, graph.vertices),
		solutionStringFn = StringFn.partition(domain, graph.vertices),
	)
	p.addVariableDomains(domain)
	return Pair(p, cfg)
}

fun newGraphCoverProblem(name: String, variablesFn: GraphVariablesFn): Pair<Problem?, GraphCfg?> {
	val (p, cfg) = newGraphSubsetProblem(name, variablesFn)
	if(p == null || cfg == null) return Pair(null, null)
	p.goal = Goal.MINIMIZE
	return Pair(p, cfg)
}

fun <T> newGraphColoringProblem(name: String, variablesFn: GraphVariablesFn, domainFn: GraphColorsFn<T>): Pair<Problem?, GraphColoring?> {
	val cfg = GraphColoring.new(name) ?: return Pair(null, null)
	val domain = domainFn(cfg)
	val p = Problem(
		name,
		description = cfg.toString(),
		type = ProblemType.ASSIGNMENT,
		goal = Goal.MINIMIZE,
		variables = Variables.from(variablesFn(cfg.graph)),
	)
	p.addVariableDomains(Domain.from(domain))
	p.solutionStringFn = StringFn.values(p, domain)
	return Pair(p, cfg)
}

fun newSpanningTreeProblem(name: String, spanFn: GraphSpanFn): Pair<Problem?, GraphCfg?> {
	val (p, cfg) = newGraphSubsetProblem(name, ::graphEdges)
	if (p == null || cfg == null) {
		return Pair(null, null)
	}
	val graph = cfg.graph
	val vertices = spanFn(cfg)
	p.addUniversalConstraint(Constraint.allVerticesCovered(graph, vertices))
	p.addUniversalConstraint(Constraint.spanningTree(graph, vertices))
	p.goal = Goal.MINIMIZE
	return Pair(p, cfg)
}

fun edgeWeightedProblem(p: Problem?, cfg: GraphCfg?): Problem? {
	if(p == null || cfg == null) return null
	val graph = cfg.graph
	if(graph.edges.size != cfg.edgeWeight.size) return null
	p.description += "\nWeight: ${cfg.edgeWeight}"
	p.objectiveFn = ScoreFn.sumWeightedValues(p.variables, cfg.edgeWeight)
	return p
}

