package discrete

typealias Variable = Int
typealias Value = Int

class Variables {
	companion object {
		fun <T> from(items: List<T>): List<Variable> = (0 until items.size).toList()
		fun range(first: Int, last: Int): List<Variable> = (first..last).toList()
	}
}

class Domain {
	companion object {
		fun <T> from(items: List<T>): List<Value> = (0 until items.size).toList()
		fun boolean(): List<Value> = listOf(1, 0)
		fun range(first: Int, last: Int): List<Value> = (first..last).toList()
		fun index(numItems: Int): List<Value> = (0 until numItems).toList()
		fun path(numItems: Int): List<Value> = index(numItems) + listOf(-1)
	}
}