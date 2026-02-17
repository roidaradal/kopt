package fn

import data.Graph
import data.GraphPath
import data.Vertex
import discrete.Problem
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

fun Solution.asGraphPath(cfg: GraphPath): List<Int> {
	val length = this.values.max() + 1
	val path = MutableList(length) { 0 }
	for((variable, idx) in this.map) {
		if(idx < 0) continue
		path[idx] = cfg.originalIndex[variable] ?: continue
	}
	path.addFirst(cfg.start)
	path.addLast(cfg.end)
	return path
}

fun Solution.asPathOrder(): List<Variable> {
	val length = values.max() + 1
	val path = MutableList(length) { -1 }
	for((idx, order) in this.map) {
		if(order < 0) continue
		path[order] = idx
	}
	return path
}

fun <T> Solution.partitionStrings(values: List<Value>, items: List<T>): List<List<String>> {
	return this.asPartition(values).map { group -> group.mapList(items).map { it.toString() } }
}

fun <T: Number> Solution.partitionSums(values: List<Value>, items: List<T>): List<Double> {
	return this.asPartition(values).map { group -> group.mapList(items).sumOf { it.toDouble() } }
}

fun Solution.asSequence(): List<Variable> {
	val sequence = MutableList<Variable>(this.map.size) { 0 }
	for((variable, idx) in this.map) {
		sequence[idx] = variable
	}
	return sequence
}

fun <T> Solution.sequenceStrings(items: List<T>): List<String> {
	return this.asSequence().map { items[it].toString() }
}

fun <T> Solution.valueStrings(p: Problem, items: List<T>?): List<String> {
	return p.variables.map(fun(x: Variable): String {
		val value = this.map[x] ?: return ""
		return if (items == null) {
			value.toString()
		} else {
			items[value].toString()
		}
	})
}

fun Solution.tallyValues(values: List<Value>): Map<Value, Int> {
	val count: MutableMap<Value, Int> = values.associateWith { 0 }.toMutableMap()
	for((_, value) in this.map) {
		if (!count.containsKey(value)) continue
		count.increment(value)
	}
	return count
}

fun Solution.groupByValue(): Map<Value, List<Variable>> {
	val groups = mutableMapOf<Value, List<Variable>>()
	for((k, v) in this.map) {
		groups[v] = (groups[v] ?: listOf()) + listOf(k)
	}
	return groups
}

fun Solution.spannedVertices(graph: Graph): Set<Vertex>? {
	val edges = asSubset().mapList(graph.edges)
	if (edges.isEmpty()) return null
	val activeEdges = edges.toSet()
	val start = edges[0].vertex1
	return graph.bfsTraversal(start, activeEdges).toSet()
}

fun Solution.pathDistances(cfg: GraphPath): List<Double> {
	val distances = mutableListOf<Double>()
	val path = asGraphPath(cfg)
	var prev = path[0]
	for(i in 1 until path.size) {
		val curr = path[i]
		distances.add(cfg.distance[prev][curr])
		prev = curr
	}
	return distances
}