package data

fun <T> List<T>.combinations(k: Int): List<List<T>> {
	if(k == 0) return listOf(emptyList())
	if(isEmpty()) return emptyList()

	val front = first()
	val rest = drop(1)

	val withFirst = rest.combinations(k-1).map { (it.reversed() + front).reversed() }
	val noFirst = rest.combinations(k)
	return withFirst + noFirst
}