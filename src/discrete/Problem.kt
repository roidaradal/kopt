package discrete

class Problem(
    val name: String,
    val variables: List<Variable> = listOf(),
    val type: ProblemType = ProblemType.None,
    val goal: Goal = Goal.None,
    val objectiveFn: ObjectiveFn? = null,
    val solutionCoreFn: SolutionCoreFn? = null,
    val solutionStringFn: SolutionStringFn? = null,
    val solutionDisplayFn: SolutionDisplayFn? = null,
    ) {
    val domain: MutableMap<Variable, List<Value>> = mutableMapOf()
    val constraints: MutableList<Constraint> = mutableListOf()
    var uniformDomain: List<Value>? = null
        internal set

    override fun toString() = "<Problem: $name>"

    val isSatisfaction: Boolean
        get() = goal == Goal.Satisfy

    val isOptimization: Boolean
        get() = goal == Goal.Minimize || goal == Goal.Maximize

    fun addUniversalConstraint(test: ConstraintFn) {
        val penalty = if (goal == Goal.Maximize) -HardPenalty else HardPenalty
        addGlobalConstraint(test, penalty, variables)
    }

    fun addGlobalConstraint(test: ConstraintFn, penalty: Penalty, variables: List<Variable>) {
        constraints.add(Constraint(test, penalty, variables))
    }

    fun isSatisfied(solution: Solution) = constraints.all { it.isSatisfied(solution) }

    fun computeScore(solution: Solution) {
        if (objectiveFn == null) return
        solution.score = objectiveFn(solution)
    }
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