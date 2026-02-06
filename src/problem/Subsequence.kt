package problem

import data.Numbers
import data.newName
import discrete.Goal
import discrete.Problem
import discrete.Solution
import fn.asSubset
import fn.mapList
import fn.scoreSubsetSize

fun newSubsequence(variant: String, n: Int): Problem? {
	val name = newName(Subsequence, variant, n)
	return when (variant) {
		"increasing" -> longestIncreasingSubsequence(name)
		"alternating" -> longestAlternatingSubsequence(name)
		else -> null
	}
}

fun newLongestSubsequenceProblem(name: String): Pair<Problem?, Numbers?> {
	val (p, cfg) = newNumbersSubsetProblem(name)
	if (p == null || cfg == null) return Pair(null, null)

	p.goal = Goal.MAXIMIZE
	p.objectiveFn = ::scoreSubsetSize
	return Pair(p, cfg)
}

fun longestIncreasingSubsequence(name: String): Problem? {
	val (p, cfg) = newLongestSubsequenceProblem(name)
	if (p == null || cfg == null) return null

	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		val subset = solution.asSubset()
		if (subset.size <= 1) return true
		val subsequence = subset.sorted().mapList(cfg.numbers)
		for ( i in 0 until subset.size-1) {
			if (subsequence[i] >= subsequence[i+1]) return false
		}
		return true
	})
	return p
}

fun longestAlternatingSubsequence(name: String): Problem? {
	val (p, cfg) = newLongestSubsequenceProblem(name)
	if(p == null || cfg == null) return null

	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		val subset = solution.asSubset()
		if (subset.size <= 1) return true
		val subsequence = subset.sorted().mapList(cfg.numbers)
		var down = true
		for (i in 0 until subset.size-1) {
			if (down && subsequence[i] <= subsequence[i+1]) {
				return false
			} else if (!down && subsequence[i] >= subsequence[i+1]) {
				return false
			}
			down = !down
		}
		return true
	})
	return p
}