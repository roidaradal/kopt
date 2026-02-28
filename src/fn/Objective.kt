package fn

import data.GraphPath
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

		fun sumWeightedSubset(keys: List<String>, weight: Map<String, Double>): ObjectiveFn {
			return fun(solution: Solution): Score {
				val subsets = solution.asSubset().map { keys[it] }
				return subsets.sumOf { subset -> weight[subset] ?: 0.0 }
			}
		}

		fun pathCost(cfg: GraphPath): ObjectiveFn {
			return fun(solution: Solution): Score {
				return solution.pathDistances(cfg).sum()
			}
		}
	}
}
