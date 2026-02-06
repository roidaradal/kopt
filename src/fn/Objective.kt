package fn

import discrete.ObjectiveFn
import discrete.Score
import discrete.Solution
import discrete.Variable

class ScoreFn {
	companion object {
		fun countUniqueValues(solution: Solution): Score = solution.values.toSet().size.toDouble()

		fun subsetSize(solution: Solution): Score = solution.values.sum().toDouble()

		fun sumWeightedValues(variables: List<Variable>, weight: List<Double>): ObjectiveFn {
			return fun(solution: Solution): Score {
				val count = solution.map
				return variables.sumOf { (count[it] ?: 0) * weight[it] }
			}
		}
	}
}
