package fn

import data.Graph
import data.Vertex
import discrete.Problem
import discrete.Solution
import discrete.SolutionStringFn
import discrete.Value

const val invalidSolution = "Invalid solution"

class StringFn {
	companion object {
		fun <T> partition(values: List<Value>, items: List<T>): SolutionStringFn {
			return fun (solution: Solution): String {
				val groups = solution.partitionStrings(values, items)
				return sortedPartitionGroups(groups).joinToString(" ")
			}
		}

		fun <T: Comparable<T>> subset(items: List<T>): SolutionStringFn {
			return fun(solution: Solution): String {
				val subset = solution.asSubset().mapList(items).sorted()
				return subset.map { it.toString() }.wrapBraces()
			}
		}

		fun <T> sequence(items: List<T>): SolutionStringFn {
			return fun(solution: Solution): String {
				return solution.sequenceStrings(items).joinToString(" ")
			}
		}

		fun <T> values(p: Problem, items: List<T>?): SolutionStringFn {
			return fun(solution: Solution): String {
				return solution.valueStrings(p, items).joinToString(" ")
			}
		}

		fun eulerianPath(graph: Graph): SolutionStringFn {
			return fun(solution: Solution): String {
				val path = mutableListOf<Vertex>()
				val edgeSequence = solution.asSequence().mapList(graph.edges)
				if (edgeSequence.size < 2) return invalidSolution
				val (a1, b1) = edgeSequence[0]
				val (a2, b2) = edgeSequence[1]
				var tail = when {
					a1 == a2 -> {
						path.addAll(listOf(b1, a1))
						b2
					}
					a1 == b2 -> {
						path.addAll(listOf(b1, a1))
						a2
					}
					b1 == a2 -> {
						path.addAll(listOf(a1, b1))
						b2
					}
					b1 == b2 -> {
						path.addAll(listOf(a1, b1))
						a2
					}
					else -> return invalidSolution
				}
				for((a, b) in edgeSequence.subList(2, edgeSequence.size)) {
					path.add(tail)
					tail = when (tail) {
						a -> b
						b -> a
						else -> return invalidSolution
					}
				}
				path.add(tail)
				return path.joinToString(" ")
			}
		}
	}
}



