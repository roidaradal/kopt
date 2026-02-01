package fn

import discrete.Solution
import discrete.SolutionStringFn
import discrete.Value

fun <T> stringPartition(values: List<Value>, items: List<T>): SolutionStringFn {
	fun fn(solution: Solution): String {
		val groups = solution.partitionStrings(values, items)
		return sortedPartitionGroups(groups).joinToString(" ")
	}
	return ::fn
}