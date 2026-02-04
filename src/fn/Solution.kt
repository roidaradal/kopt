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

fun Solution.asSubset(): List<Variable> = this.map.filter{ (_, value) -> value == 1 }.map { (variable, _) -> variable}

fun <T> Solution.partitionStrings(values: List<Value>, items: List<T>): List<List<String>> {
	return this.asPartition(values).map { group -> group.mapList(items).map { it.toString() } }
}

fun <T: Number>Solution.partitionSums(values: List<Value>, items: List<T>): List<Double> {
	return this.asPartition(values).map { group -> group.mapList(items).sumOf { it.toDouble() } }
}
