package data

data class GraphCfg(
	val graph: Graph,
){
	override fun toString(): String = graph.toString()

	companion object {
		fun undirected(name: String): GraphCfg? = new(name, false)
		fun directed(name: String): GraphCfg? = new(name, true)
		private fun new(name: String, isDirected: Boolean): GraphCfg? {
			val data = load(name) ?: return null
			return GraphCfg(
				graph = Graph.new(data["vertices"], data["edges"], isDirected),
			)
		}
	}
}

data class GraphColoring(
	val graph: Graph,
	val colors: List<String> = emptyList(),
) {
	override fun toString(): String = graph.toString()

	companion object {
		fun new(name: String): GraphColoring? {
			val data = load(name) ?: return null
			return GraphColoring(
				graph = Graph.undirected(data["vertices"], data["edges"]),
				colors = data["colors"].toStringList(),
			)
		}
	}
}

typealias GraphVariablesFn = (Graph) -> List<String>
typealias GraphColorsFn<T> = (GraphColoring) -> List<T>

fun graphVertices(graph: Graph): List<String> = graph.vertices

fun graphEdges(graph: Graph): List<String> = graph.edges.map { it.toString() }

fun graphColors(cfg: GraphColoring): List<String> = cfg.colors