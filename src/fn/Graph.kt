package fn

import data.Edge
import data.Graph
import data.Vertex

fun Graph.isClique(vertices: List<Vertex>): Boolean {
	val vertexSet = vertices.toSet()
	for(vertex in vertices) {
		val adjacent = neighborsOf(vertex) + setOf(vertex)
		if((vertexSet - adjacent).isNotEmpty()) return false
	}
	return true
}

fun Graph.bfsTraversal(start: Vertex, activeEdges: Set<Edge>?): List<Vertex> {
	val q = mutableListOf(start)
	val visited = mutableSetOf<Vertex>()
	while (q.isNotEmpty()) {
		val current = q.removeFirst()
		if (visited.contains(current)) continue
		visited.add(current)
		for(neighbor in activeNeighbors(current, activeEdges)) {
			if(visited.contains(neighbor)) continue
			q.add(neighbor)
		}
	}
	return visited.toList()
}

fun Graph.connectedComponents(activeEdges: Set<Edge>?): List<List<Vertex>> {
	val covered = vertices.associateWith { false }.toMutableMap()
	val components = mutableListOf<List<Vertex>>()
	for(vertex in vertices) {
		if(covered[vertex] ?: false) continue
		val component = bfsTraversal(vertex, activeEdges)
		components.add(component)
		component.forEach { covered[it] = true }
	}
	return components
}

fun Graph.isEulerianPath(edges: List<Edge>): Pair<Boolean, Pair<Vertex, Vertex>> {
	val failResult = Pair(false, Pair("", ""))
	if (edges.size < 2) return failResult
	val visitCount = this.edges.associateWith { 0 }.toMutableMap()
	val (a1, b1) = edges[0]
	val (a2, b2) = edges[1]
	var (head, tail) = when {
		a1 == a2 -> Pair(b1, b2)
		a1 == b2 -> Pair(b1, a2)
		b1 == a2 -> Pair(a1, b2)
		b1 == b2 -> Pair(a1, a2)
		else -> return failResult
	}
	visitCount.increment(edges[0])
	visitCount.increment(edges[1])
	for(edge in edges.subList(2, edges.size)) {
		visitCount.increment(edge)
		val (a, b) = edge
		tail = when (tail) {
			a -> b
			b -> a
			else -> return failResult
		}
	}
	val ok = visitCount.values.all { it == 1 }
	val pair = Pair(head, tail)
	return Pair(ok, pair)
}

fun Graph.isHamiltonianPath(vertices: List<Vertex>): Boolean {
	if (vertices.isEmpty()) return false
	val visitCount = this.vertices.associateWith { 0 }.toMutableMap()
	for ((i, curr) in vertices.withIndex()) {
		visitCount.increment(curr)
		if (i == vertices.lastIndex) break
		val next = vertices[i+1]
		if (!this.neighborsOf(curr).contains(next)) return false
	}
	return visitCount.values.all { it == 1 }
}