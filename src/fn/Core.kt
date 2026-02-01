package fn

import discrete.Solution
import discrete.SolutionCoreFn
import discrete.Value

fun <T> coreSortedPartition(values: List<Value>, items: List<T>): SolutionCoreFn {
	fun fn(solution: Solution): String {
		val groups = solution.partitionStrings(values, items).filter { it.isNotEmpty() }
		return sortedPartitionGroups(groups).sorted().joinToString("/")
	}
	return ::fn
}