package fn

import discrete.Solution

class ConstraintFn {
	companion object {
		fun allUnique(solution: Solution): Boolean {
			val values = solution.values
			return values.size == values.toSet().size
		}
	}
}