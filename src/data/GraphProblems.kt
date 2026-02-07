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
			val g = if (isDirected) {
				Graph.directed(data["vertices"], data["edges"])
			} else {
				Graph.undirected(data["vertices"], data["edges"])
			}
			return GraphCfg(
				graph = g,
			)
		}
	}
}

typealias GraphVariablesFn = (Graph) -> List<String>

fun graphVertices(graph: Graph): List<String> = graph.vertices

fun graphEdges(graph: Graph): List<String> = graph.edges.map { it.toString() }