package fn

fun MutableMap<String, Int>.updateCounter(items: List<String>) {
	items.forEach { increment(it) }
}

fun <T> MutableMap<T, Int>.increment(key: T) {
	this[key] = (this[key] ?: 0) + 1
}

fun <T> List<Int>.mapList(values: List<T>): List<T> {
	return try {
		this.map{ values[it] }
	}catch(_: IndexOutOfBoundsException){
		emptyList()
	}
}

fun <T> List<T>.isAllUnique(): Boolean = size == toSet().size

fun <T> List<T>.wrapBraces(): String = "{ " + this.joinToString(separator = ", ") + " }"

fun List<String>.mirroredItems(separator: String = " "): String {
	if (isEmpty()) return ""
	return if (first() > last()) {
		reversed().joinToString(separator)
	} else {
		joinToString(separator)
	}
}

fun List<String>.sortedCycle(removeTail: Boolean): String {
	val limit = if (removeTail) this.size-1 else this.size
	val index = this.indexOf(this.min())
	val sequence = this.subList(index, limit) + this.subList(0, index)
	return sequence.joinToString(" ")
}

fun sortedPartitionGroups(groups: List<List<String>>): List<String> = groups.map { it.sorted().wrapBraces() }
