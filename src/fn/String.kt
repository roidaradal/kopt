package fn

import discrete.Solution
import discrete.SolutionStringFn
import discrete.Value

fun <T> stringPartition(values: List<Value>, items: List<T>): SolutionStringFn {
	return fun (solution: Solution): String {
		val groups = solution.partitionStrings(values, items)
		return sortedPartitionGroups(groups).joinToString(" ")
	}
}

fun <T: Comparable<T>> stringSubset(items: List<T>): SolutionStringFn {
	return fun(solution: Solution): String {
		val subset = solution.asSubset().mapList(items).sorted()
		return subset.map { it.toString() }.wrapBraces()
	}
}