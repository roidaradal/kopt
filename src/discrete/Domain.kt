package discrete

typealias Variable = Int
typealias Value = Int

class Variables {
	companion object {
		fun <T> from(items: List<T>): List<Variable> = (0 until items.size).toList()
	}
}

class Domain {
	companion object {
		fun boolean(): List<Value> = listOf(1, 0)
	}
}