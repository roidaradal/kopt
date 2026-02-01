package fn

import discrete.Solution
import discrete.Value
import discrete.Variable

fun Solution.asPartition(values: List<Value>): List<List<Variable>> {
	val groups = values.associateWith { mutableListOf<Variable>() }
	for((variable, value) in this.map) {
		groups[value]?.add(variable)
	}
	return values.map { groups[it] ?: emptyList() }
}

fun <T> Solution.partitionStrings(values: List<Value>, items: List<T>): List<List<String>> {
	return asPartition(values).map { group -> group.mapList(items).map { it.toString()} }
}

fun Solution.partitionSums(values: List<Value>, items: List<Double>): List<Double> {
	return asPartition(values).map { group -> group.mapList(items).sum() }
}