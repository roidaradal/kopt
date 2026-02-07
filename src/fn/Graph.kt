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
	val q = mutableListOf<Vertex>(start)
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