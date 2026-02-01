package discrete

typealias Penalty = Double

const val Inf = Double.POSITIVE_INFINITY
const val HardPenalty: Penalty = Inf

typealias ConstraintFn = (Solution) -> Boolean

class Constraint(
	val test: ConstraintFn,
	val penalty: Penalty,
	val variables: List<Variable>
){
	fun isSatisfied(solution: Solution) = test(solution)
	fun computePenalty(solution: Solution): Penalty = if (isSatisfied(solution)) 0.0 else penalty
}