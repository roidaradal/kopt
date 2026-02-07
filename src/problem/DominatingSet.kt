package problem

import data.Vertex
import data.graphEdges
import data.graphVertices
import data.newName
import discrete.Problem
import discrete.Solution
import fn.asSubset
import fn.mapList

fun newDominatingSet(variant: String, n: Int): Problem? {
	val name = newName(DominatingSet, variant, n)
	return when (variant) {
		"basic" -> dominatingSet(name)
		"edge" -> edgeDominatingSet(name)
		"efficient" -> efficientDominatingSet(name)
		else -> null
	}
}

fun dominatingSet(name: String): Problem? {
	val (p, cfg) = newGraphCoverProblem(name, ::graphVertices)
	if (p == null || cfg == null) {
		return null
	}
	val graph = cfg.graph

	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		val selectedVertices = solution.asSubset().mapList(graph.vertices)
		if (selectedVertices.isEmpty()) return false
		val vertexSet = selectedVertices.toSet()
		return graph.vertices.all { vertex ->
			val adjacent = graph.neighborsOf(vertex).toSet() + setOf(vertex)
			vertexSet.intersect(adjacent).isNotEmpty()
		}
	})
	return p
}

 fun edgeDominatingSet(name: String): Problem? {
	 val (p, cfg) = newGraphCoverProblem(name, ::graphEdges)
	 if (p == null || cfg == null) {
		 return null
	 }
	 val graph = cfg.graph

	 p.addUniversalConstraint(fun(solution: Solution): Boolean {
		 val selectedEdges = solution.asSubset().mapList(graph.edges)
		 val covered = mutableSetOf<Vertex>()
		 for((v1, v2) in selectedEdges) {
			 covered.add(v1)
			 covered.add(v2)
		 }
		 return graph.edges.all { (v1, v2) -> covered.contains(v1) || covered.contains(v2) }
	 })
	 return p
 }

fun efficientDominatingSet(name: String): Problem? {
	val (p, cfg) = newGraphCoverProblem(name, ::graphVertices)
	if (p == null || cfg == null) {
		return null
	}
	val graph = cfg.graph

	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		val selectedVertices = solution.asSubset().mapList(graph.vertices)
		if (selectedVertices.isEmpty()) return false
		val vertexSet = selectedVertices.toSet()
		return graph.vertices.all { vertex ->
			val adjacent = graph.neighborsOf(vertex).toSet() + setOf(vertex)
			vertexSet.intersect(adjacent).size == 1
		}
	})
	return p
}