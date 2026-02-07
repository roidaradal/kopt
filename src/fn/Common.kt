package fn

fun MutableMap<String, Int>.updateCounter(items: List<String>) {
	for (item in items) {
		this[item] = (this[item] ?: 0) + 1
	}
}

fun <T> List<Int>.mapList(values: List<T>): List<T> {
	return try {
		this.map{ values[it] }
	}catch(e: IndexOutOfBoundsException){
		emptyList()
	}
}

fun <T> List<T>.wrapBraces(): String = "{ " + this.joinToString(separator = ", ") + " }"

fun List<String>.mirroredSequence(): String {
	if (isEmpty()) return ""
	return if (first() > last()) {
		reversed().joinToString(" ")
	} else {
		joinToString(" ")
	}
}

fun sortedPartitionGroups(groups: List<List<String>>): List<String> = groups.map { it.sorted().wrapBraces() }
