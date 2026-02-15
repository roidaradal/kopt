package fn

import data.Graph
import data.GraphPath
import data.Vertex
import discrete.ConstraintFn
import discrete.Inf
import discrete.Solution

class Constraint {
	companion object {
		fun allUnique(solution: Solution): Boolean {
			val values = solution.values
			return values.isAllUnique()
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
		fun isRainbowColored(colors: List<String>): ConstraintFn {
			return fun(solution: Solution): Boolean {
				return solution.asSubset().mapList(colors).isAllUnique()
			}
		}
		fun allVerticesCovered(graph: Graph, vertices: List<Vertex>): ConstraintFn {
			return fun(solution: Solution): Boolean {
				val covered = vertices.associateWith { false }.toMutableMap()
				for (x in solution.asSubset()) {
					val (v1, v2) = graph.edges[x]
					covered[v1] = true
					covered[v2] = true
				}
				return covered.values.all { it }
			}
		}
		fun spanningTree(graph: Graph, vertices: List<Vertex>): ConstraintFn {
			return fun(solution: Solution): Boolean {
				val reachable = solution.spannedVertices(graph) ?: return false
				return (vertices.toSet() - reachable).isEmpty()
			}
		}
		fun simplePath(cfg: GraphPath): ConstraintFn {
			return fun(solution: Solution): Boolean {
				val path = solution.asGraphPath(cfg)
				var prev = path[0]
				val visited = mutableSetOf(prev)
				for(i in 1 until path.size) {
					val curr = path[i]
					if (visited.contains(curr)) return false
					if (cfg.distance[prev][curr] == Inf) return false
					visited.add(curr)
					prev = curr
				}
				return true
			}
		}
	}
}