package data

data class GraphCfg(
	val graph: Graph,
	val k: Int = 0,
	val edgeWeight: List<Double> = emptyList(),
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
) {
	override fun toString(): String = graph.toString()

	companion object {
		fun new(name: String): GraphPartition? {
			val data = load(name) ?: return null
			return GraphPartition(
				graph = Graph.undirected(data["vertices"], data["edges"]),
				numPartitions = data["numPartitions"].parseInt(),
			)
		}
	}
}

typealias GraphVariablesFn = (Graph) -> List<String>
typealias GraphColorsFn<T> = (GraphColoring) -> List<T>

fun graphVertices(graph: Graph): List<String> = graph.vertices

fun graphEdges(graph: Graph): List<String> = graph.edges.map { it.toString() }

fun graphColors(cfg: GraphColoring): List<String> = cfg.colors

fun graphNumbers(cfg: GraphColoring): List<Int> = cfg.numbers