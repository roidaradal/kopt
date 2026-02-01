package fn

import discrete.Score
import discrete.Solution

fun scoreCountUniqueValues(solution: Solution): Score = solution.values.toSet().size.toDouble()
