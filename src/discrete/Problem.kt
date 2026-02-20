package discrete

class Problem(
    val name: String,
    val variables: List<Variable> = emptyList(),
    var type: ProblemType = ProblemType.NONE,
    var goal: Goal = Goal.NONE,
    var objectiveFn: ObjectiveFn? = null,
    var solutionCoreFn: SolutionCoreFn? = null,
    var solutionStringFn: SolutionStringFn? = null,
//    var solutionDisplayFn: SolutionDisplayFn? = null,
    var description: String? = null,
) {
    val domain: MutableMap<Variable, List<Value>> = mutableMapOf()
    private val constraints: MutableList<Constraint> = mutableListOf()
    private var _uniformDomain: List<Value>? = null

    val uniformDomain: List<Value> get() = _uniformDomain ?: emptyList()

    override fun toString(): String = listOf("<Problem: $name>", description ?: "").joinToString("\n")

    val isSatisfaction: Boolean
        get() = goal == Goal.SATISFY

    val isOptimization: Boolean
        get() = goal == Goal.MINIMIZE || goal == Goal.MAXIMIZE

    fun addVariableDomains(values: List<Value>) {
        variables.forEach { variable -> domain[variable] = values.toList() }
        _uniformDomain = values
    }

    fun addUniversalConstraint(test: ConstraintFn) {
        val penalty = if (goal == Goal.MAXIMIZE) -HardPenalty else HardPenalty
        addGlobalConstraint(test, penalty, variables)
    }

    private fun addGlobalConstraint(test: ConstraintFn, penalty: Penalty, variables: List<Variable>) {
        constraints.add(Constraint(test, penalty, variables))
    }

//    fun isSatisfied(solution: Solution) = constraints.all { it.isSatisfied(solution) }
//
//    fun computeScore(solution: Solution) {
//        val score = objectiveFn?.invoke(solution)
//        if (score != null) solution.score = score
//    }
}

enum class Goal {
    NONE,
    MAXIMIZE,
    MINIMIZE,
    SATISFY,
}

enum class ProblemType {
    NONE,
    PARTITION,
    SUBSET,
    SEQUENCE,
    ASSIGNMENT,
    PATH,
}