package fn

import data.Graph
import data.GraphPath
import data.JobShop
import data.Numbers
import data.Task
import data.TimeRange
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
		fun increasingSubsequence(cfg: Numbers): ConstraintFn {
			return fun(solution: Solution): Boolean {
				val subset = solution.asSubset()
				if (subset.size <= 1) return true
				val subsequence = subset.sorted().mapList(cfg.numbers)
				for ( i in 0 until subset.size-1) {
					if (subsequence[i] >= subsequence[i+1]) return false
				}
				return true
			}
		}
		fun noMachineOverlap(cfg: JobShop, tasks: List<Task>): ConstraintFn {
			return noOverlap(tasks, cfg.machines, fun(task: Task): String {
				return task.machine
			})
		}
	}
}

fun noOverlap(tasks: List<Task>, keys: List<String>, keyFn: (Task) -> String): ConstraintFn {
	return fun(solution: Solution): Boolean {
		val groupSched = keys.associateWith { listOf<TimeRange>() }.toMutableMap()
		for((x, start) in solution.map) {
			val task = tasks[x]
			val sched = TimeRange(start, start + task.duration)
			val key = keyFn(task)
			groupSched[key] = (groupSched.getOrDefault(key, listOf()) + sched)
		}
		for ((_, scheds) in groupSched) {
			val sortedScheds = scheds.sortedBy { it.first }
			for(i in 0 until sortedScheds.size-1) {
				val curr = sortedScheds[i]
				val (start1, end1) = curr
				val start2 = sortedScheds[i+1].first
				if (start2 <= start1 || start2 < end1) return false
			}
		}
		return true
	}
}