package problem

import data.GraphCfg
import data.GraphVariablesFn
import data.graphEdges
import data.graphVertices
import data.newName
import data.spaceSplit
import discrete.Domain
import discrete.Goal
import discrete.Problem
import discrete.ProblemType
import discrete.Solution
import discrete.Variables
import fn.CoreFn
import fn.StringFn
import fn.asSequence
import fn.invalidSolution
import fn.isEulerianPath
import fn.isHamiltonianPath
import fn.mapList
import fn.mirroredItems
import fn.sortedCycle

fun newGraphTour(variant: String, n: Int): Problem? {
	val name = newName(GraphTour, variant, n)
	return when (variant) {
		"euler_path" -> eulerianPath(name)
		"euler_cycle" -> eulerianCycle(name)
		"hamilton_path" -> hamiltonianPath(name)
		"hamilton_cycle" -> hamiltonianCycle(name)
		else -> null
	}
}

fun newGraphTourProblem(name: String, variablesFn: GraphVariablesFn): Pair<Problem?, GraphCfg?> {
	val cfg = GraphCfg.undirected(name) ?: return Pair(null, null)
	val variables = variablesFn(cfg.graph)
	val p = Problem(
		name,
		description = cfg.toString(),
		type = ProblemType.SEQUENCE,
		goal = Goal.SATISFY,
		variables = Variables.from(variables),
	)
	p.addVariableDomains(Domain.index(variables.size))
	return Pair(p, cfg)
}

fun eulerianPath(name: String): Problem? {
	val (p, cfg) = newGraphTourProblem(name, ::graphEdges)
	if(p == null || cfg == null) return null
	val graph = cfg.graph

	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		val edgeSequence = solution.asSequence().mapList(graph.edges)
		val (isEulerianPath, _) = graph.isEulerianPath(edgeSequence)
		return isEulerianPath
	})

	val toEulerianPath = StringFn.eulerianPath(graph)
	p.solutionStringFn = toEulerianPath
	p.solutionCoreFn = fun(solution: Solution): String {
		val path = toEulerianPath(solution)
		if (path == invalidSolution) return path
		return path.spaceSplit().mirroredItems()
	}
	return p
}

fun eulerianCycle(name: String): Problem? {
	val (p, cfg) = newGraphTourProblem(name, ::graphEdges)
	if(p == null || cfg == null) return null
	val graph = cfg.graph

	p.addUniversalConstraint(fun(solution: Solution): Boolean{
		val edgeSequence = solution.asSequence().mapList(graph.edges)
		val (isEulerianPath, pair) = graph.isEulerianPath(edgeSequence)
		val (head, tail) = pair
		return isEulerianPath && head == tail
	})

	val toEulerianPath = StringFn.eulerianPath(graph)
	p.solutionStringFn = toEulerianPath
	p.solutionCoreFn = fun(solution: Solution): String {
		val path = toEulerianPath(solution)
		if (path == invalidSolution) return path
		return path.spaceSplit().sortedCycle(true)
	}
	return p
}

fun hamiltonianPath(name: String): Problem? {
	val (p, cfg) = newGraphTourProblem(name, ::graphVertices)
	if(p == null || cfg == null) return null
	val graph = cfg.graph

	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		val vertices = solution.asSequence().mapList(graph.vertices)
		return graph.isHamiltonianPath(vertices)
	})

	p.solutionCoreFn = CoreFn.mirroredSequence(graph.vertices)
	p.solutionStringFn = StringFn.sequence(graph.vertices)
	return p
}

fun hamiltonianCycle(name: String): Problem? {
	val (p, cfg) = newGraphTourProblem(name, ::graphVertices)
	if(p == null || cfg == null) return null
	val graph = cfg.graph

	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		val vertices = solution.asSequence().mapList(graph.vertices)
		if (!graph.isHamiltonianPath(vertices)) return false
		val (first, last) = listOf(vertices.first(), vertices.last())
		return graph.neighborsOf(last).contains(first)
	})

	p.solutionCoreFn = CoreFn.sortedCycle(graph.vertices)
	p.solutionStringFn = StringFn.sequence(graph.vertices)
	return p
}