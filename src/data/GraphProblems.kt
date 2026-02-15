package data

data class GraphCfg(
	val graph: Graph,
	val k: Int = 0,
	val edgeWeight: List<Double> = emptyList(),
	val edgeColor: List<String> = emptyList(),
	val vertexColor: List<String> = emptyList(),
	val terminals: List<String> = emptyList(),
){
	override fun toString(): String = graph.toString()

	companion object {
		fun undirected(name: String): GraphCfg? = new(name, false)
		fun directed(name: String): GraphCfg? = new(name, true)
		private fun new(name: String, isDirected: Boolean): GraphCfg? {
			val data = load(name) ?: return null
			return GraphCfg(
				graph = Graph.new(data["vertices"], data["edges"], isDirected),
				k = data["k"].parseInt(),
				edgeWeight = data["edgeWeight"].toDoubleList(),
				edgeColor = data["edgeColor"].toStringList(),
				vertexColor = data["vertexColor"].toStringList(),
				terminals = data["terminals"].toStringList(),
			)
		}
	}
}

data class GraphColoring(
	val graph: Graph,
	val colors: List<String> = emptyList(),
	val numbers: List<Int> = emptyList(),
) {
	override fun toString(): String = graph.toString()

	companion object {
		fun new(name: String): GraphColoring? {
			val data = load(name) ?: return null
			return GraphColoring(
				graph = Graph.undirected(data["vertices"], data["edges"]),
				colors = data["colors"].toStringList(),
				numbers = data["numbers"].toIntList(),
			)
		}
	}
}

data class GraphPartition(
	val graph: Graph,
	val numPartitions: Int = 0,
	val minSize: Int = 0,
	val edgeWeight: List<Double> = emptyList(),
) {
	override fun toString(): String = graph.toString()

	companion object {
		fun new(name: String): GraphPartition? {
			val data = load(name) ?: return null
			return GraphPartition(
				graph = Graph.undirected(data["vertices"], data["edges"]),
				numPartitions = data["numPartitions"].parseInt(),
				minSize = data["minSize"].parseInt(),
				edgeWeight = data["edgeWeight"].toDoubleList(),
			)
		}
	}
}

data class GraphPath(
	val start: Int = 0,
	val end: Int = 0,
	val originalIndex: Map<Int, Int> = emptyMap(),
	val vertices: List<Vertex> = emptyList(),
	val between: List<Vertex> = emptyList(),
	val distance: List<List<Double>> = emptyList(),
	val items: List<String> = emptyList(),
	val cost: List<List<Double>> = emptyList(),
	val fromOrigin: List<Double> = emptyList(),
	val toOrigin: List<Double> = emptyList(),
) {
	override fun toString(): String = "Vertices: $vertices\nDistance: $distance"

	companion object {
		fun new(name: String) : GraphPath? {
			val data = load(name) ?: return null
			val vertices = data["vertices"].toStringList()
			val startVertex = data["start"] ?: return null
			val endVertex = data["end"] ?: return null
			val startIndex = vertices.indexOf(startVertex)
			val endIndex = vertices.indexOf(endVertex)
			if (startIndex < 0 || endIndex < 0) return null
			val distanceMatrix = mutableListOf<List<Double>>()
			for(line in data["distance"].parseList()) {
				distanceMatrix.add(line.matrixRow(true))
			}
			val originalIndex = mutableMapOf<Int, Int>()
			val between = mutableListOf<Vertex>()
			var betweenIdx = 0
			for((i, vertex) in vertices.withIndex()) {
				if(i == startIndex || i == endIndex) continue
				between.add(vertex)
				originalIndex[betweenIdx] = i
				betweenIdx += 1
			}
			return GraphPath(
				start = startIndex,
				end = endIndex,
				vertices = vertices,
				between = between,
				distance = distanceMatrix,
				originalIndex = originalIndex,
			)
		}

		fun tour(name: String): GraphPath? {
			val data = load(name) ?: return null
			val distanceMatrix = mutableListOf<List<Double>>()
			for(line in data["distance"].parseList()) {
				distanceMatrix.add(line.matrixRow(true))
			}
			val costMatrix = mutableListOf<List<Double>>()
			for(line in data["cost"].parseList()) {
				costMatrix.add(line.matrixRow(true))
			}
			return GraphPath(
				vertices = data["vertices"].toStringList(),
				items = data["items"].toStringList(),
				distance = distanceMatrix,
				cost = costMatrix,
				fromOrigin = data["fromOrigin"].toDoubleList(),
				toOrigin = data["toOrigin"].toDoubleList(),
			)
		}
	}
}

typealias GraphVariablesFn = (Graph) -> List<String>
typealias GraphSpanFn = (GraphCfg) -> List<String>
typealias GraphColorsFn<T> = (GraphColoring) -> List<T>

fun graphVertices(graph: Graph): List<String> = graph.vertices

fun graphEdges(graph: Graph): List<String> = graph.edges.map { it.toString() }

fun graphColors(cfg: GraphColoring): List<String> = cfg.colors

fun graphNumbers(cfg: GraphColoring): List<Int> = cfg.numbers

fun spanVertices(cfg: GraphCfg): List<String> = cfg.graph.vertices

fun spanTerminals(cfg: GraphCfg): List<String> = cfg.terminals