import problem.*

fun main() {
    println("Welcome to kopt")
    val problem = Creator[BinPacking]?.invoke("basic", 1)
    println(problem)
}