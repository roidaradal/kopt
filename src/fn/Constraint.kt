package fn

import data.Graph
import discrete.ConstraintFn
import discrete.Solution

class Constraint {
	companion object {
		fun allUnique(solution: Solution): Boolean {
			val values = solution.values
			return values.size == values.toSet().size
		}
		fun properVertexColoring(graph: Graph): ConstraintFn {
			return fun(solution: Solution): Boolean {
				val color = solution.map
				return graph.edges.all {edge ->
					val x1 = graph.indexOf(edge.vertex1)
					val x2 = graph.indexOf(edge.vertex2)
					return color[x1] != color[x2]
				}
			}
		}

	}
}