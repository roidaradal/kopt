package fn

fun <T> List<Int>.mapList(values: List<T>): List<T> {
	return try {
		this.map{ values[it] }
	}catch(e: IndexOutOfBoundsException){
		emptyList()
	}
}

fun <T> List<T>.wrapBraces(): String = "{ " + this.joinToString(separator = ", ") + " }"

fun sortedPartitionGroups(groups: List<List<String>>): List<String> = groups.map { group -> group.sorted().wrapBraces() }