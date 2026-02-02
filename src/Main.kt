import problem.*

fun main() {
    println("Welcome to kopt")
    val problem = Creator[Interval]?.invoke("weighted", 1)
    println(problem)
}