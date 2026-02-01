package discrete

class Problem(val name: String) {
    var variables: List<Variable> = listOf()
    var domain: Map<Variable, List<Value>> = mapOf()
    var type: ProblemType = ProblemType.None
    var goal: Goal = Goal.None

    override fun toString() = "<Problem: $name>"

    val isSatisfaction: Boolean
        get() = goal == Goal.Satisfy

    val isOptimization: Boolean
        get() = goal == Goal.Minimize || goal == Goal.Maximize

}

enum class Goal {
    None,
    Maximize,
    Minimize,
    Satisfy,
}

enum class ProblemType {
    None,
    Assignment,
    Partition,
    Sequence,
    Subset,
    Path,
}