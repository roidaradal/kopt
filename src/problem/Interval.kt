package problem

import data.Intervals
import data.newName
import discrete.Domain
import discrete.Goal
import discrete.Problem
import discrete.ProblemType
import discrete.Variables
import discrete.Solution
import fn.ScoreFn
import fn.StringFn
import fn.asSubset

fun newInterval(variant: String, n: Int): Problem? {
	val name = newName(Interval, variant, n)
	return when (variant) {
		"basic" -> activitySelection(name)
		"weighted" -> weightedActivitySelection(name)
		else -> null
	}
}

fun newActivitySelectionProblem(name: String): Pair<Problem?, Intervals?> {
	val cfg = Intervals.new(name) ?: return Pair(null, null)
	val p = Problem(
		name,
		description = cfg.toString(),
		type = ProblemType.SUBSET,
		goal = Goal.MAXIMIZE,
		variables = Variables.from(cfg.activities),
		solutionStringFn = StringFn.subset(cfg.activities),
	)
	p.addVariableDomains(Domain.boolean())
	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		val selected = solution.asSubset()
		if (selected.size <= 1) return true
		val activities = selected.sortedBy { cfg.start[it] } // sort by start
		for(i in 0 until activities.size-1) {
			val (curr, next) = listOf(activities[i], activities[i + 1])
			if (cfg.end[curr] > cfg.start[next]) return false
		}
		return true
	})
	return Pair(p, cfg)
}

fun activitySelection(name: String): Problem? {
	val (p, _) = newActivitySelectionProblem(name)
	if (p == null) return null

	p.objectiveFn = ScoreFn::subsetSize
	return p
}

fun weightedActivitySelection(name: String): Problem? {
	val (p, cfg) = newActivitySelectionProblem(name)
	if (p == null || cfg == null) return null
	if (cfg.activities.size != cfg.weight.size) return null

	p.description += "\nWeight: ${cfg.weight}"
	p.objectiveFn = ScoreFn.sumWeightedValues(p.variables, cfg.weight)
	return p
}