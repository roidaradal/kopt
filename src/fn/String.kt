package fn

import discrete.Problem
import discrete.Solution
import discrete.SolutionStringFn
import discrete.Value

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
	}
}



