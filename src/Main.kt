import problem.*

fun main() {
    println("Welcome to kopt")
    val problem = Creator[BinCover]?.invoke("basic", 1)
    println(problem)
}