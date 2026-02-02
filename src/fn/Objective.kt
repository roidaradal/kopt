package fn

import discrete.ObjectiveFn
import discrete.Score
import discrete.Solution
import discrete.Variable

fun scoreCountUniqueValues(solution: Solution): Score = solution.values.toSet().size.toDouble()

fun scoreSubsetSize(solution: Solution): Score = solution.values.sum().toDouble()

fun scoreSumWeightedValues(variables: List<Variable>, weight: List<Double>): ObjectiveFn {
	return fun(solution: Solution): Score {
		val count = solution.map
		return variables.sumOf { (count[it] ?: 0) * weight[it] }
	}
}