package fn

import data.GraphPath
import data.Task
import discrete.ObjectiveFn
import discrete.Score
import discrete.Solution
import discrete.Variable
import kotlin.math.max

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

		fun scheduleMakespan(tasks: List<Task>): ObjectiveFn {
			return fun(solution: Solution): Score {
				var makespan = 0
				for ((x, start) in solution.map) {
					val end = start + tasks[x].duration
					makespan = max(makespan, end)
				}
				return makespan.toDouble()
			}
		}
	}
}
