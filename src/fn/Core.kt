package fn

import discrete.Problem
import discrete.Solution
import discrete.SolutionCoreFn
import discrete.Value

class CoreFn {
	companion object {
		fun <T> sortedPartition(values: List<Value>, items: List<T>): SolutionCoreFn {
			return fun(solution: Solution): String {
				val groups = solution.partitionStrings(values, items).filter { it.isNotEmpty() }
				return sortedPartitionGroups(groups).sorted().joinToString("/")
			}
		}

		fun <T> mirroredSequence(items: List<T>): SolutionCoreFn {
			return fun(solution: Solution): String = solution.sequenceStrings(items).mirroredSequence()
		}

		fun <T> mirroredValues(p: Problem, items: List<T>?): SolutionCoreFn {
			return fun(solution: Solution): String = solution.valueStrings(p, items).mirroredSequence()
		}

		fun lookupValueOrder(p: Problem): SolutionCoreFn {
			return fun(solution: Solution): String {
				val values = solution.tuple(p)
				val core = mutableListOf<String>()
				val lookup = mutableMapOf<Value, String>()
				var order = 0
				for(value in values) {
					if(!lookup.containsKey(value)) {
						lookup[value] = order.toString()
						order += 1
					}
					lookup[value]?.let { core.add(it) }
				}
				return core.joinToString("")
			}
		}
	}
}
