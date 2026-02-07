package data

typealias Vertex = String

data class Graph(
	val vertices: List<Vertex> = emptyList(),
) {
	private val _edges: MutableList<Edge> = mutableListOf()
	private val vertexIndex: MutableMap<Vertex, Int> = mutableMapOf()
	private val vertexEdges: MutableMap<Vertex, MutableList<Edge>> = mutableMapOf()
	private val vertexNeighbors: MutableMap<Vertex, MutableSet<Vertex>> = mutableMapOf()

	init {
		for((i, vertex) in vertices.withIndex()) {
			vertexIndex[vertex] = i
		}
	}

	val edges: List<Edge> get() = _edges

	fun indexOf(vertex: Vertex): Int = vertexIndex[vertex] ?: -1
	fun edgesOf(vertex: Vertex): List<Edge> = vertexEdges[vertex] ?: emptyList()
	fun neighborsOf(vertex: Vertex): Set<Vertex> = vertexNeighbors[vertex] ?: emptySet()

	fun addUndirectedEdge(v1: Vertex, v2: Vertex) = addEdge(v1, v2, false)
	fun addDirectedEdge(v1: Vertex, v2: Vertex) = addEdge(v1, v2, true)

	private fun addEdge(v1: Vertex, v2: Vertex, isDirected: Boolean) {
		vertexIndex[v1] ?: return
		vertexIndex[v2] ?: return
		val edge = Edge(v1, v2)
		_edges.add(edge)
		vertexEdges.getOrPut(v1) { mutableListOf() }.add(edge)
		vertexEdges.getOrPut(v2) { mutableListOf() }.add(edge)
		vertexNeighbors.getOrPut(v1) { mutableSetOf() }.add(v2)
		if(!isDirected) vertexNeighbors.getOrPut(v2) { mutableSetOf() }.add(v1)
	}

	override fun toString(): String = "Vertices: $vertices\nEdges: $edges"

	companion object {
		fun undirected(vertexString: String?, edgePairString: String?): Graph {
			val vertices = vertexString?.spaceSplit() ?: emptyList()
			val edgePairs = edgePairString?.spaceSplit() ?: emptyList()
			val g = Graph(vertices)
			for (edgePair in edgePairs) {
				val (v1, v2) = newUndirectedEdge(edgePair)
				g.addUndirectedEdge(v1, v2)
			}
			return g
		}
		fun directed(vertexString: String?, edgePairString: String?): Graph {
			val vertices = vertexString?.spaceSplit() ?: emptyList()
			val edgePairs = edgePairString?.spaceSplit() ?: emptyList()
			val g = Graph(vertices)
			for (edgePair in edgePairs) {
				val (v1, v2) = newDirectedEdge(edgePair)
				g.addDirectedEdge(v1, v2)
			}
			return g
		}
	}
}

data class Edge(
	val vertex1: Vertex,
	val vertex2: Vertex,
) {
	override fun toString(): String = "$vertex1-$vertex2"
}


fun newUndirectedEdge(edge: String): Edge {
	val parts = edge.split("-").map(String::trim)
	return Edge(parts[0], parts[1])
}

fun newDirectedEdge(edge: String): Edge {
	val parts = edge.split("->").map(String::trim)
	return Edge(parts[0], parts[1])
}