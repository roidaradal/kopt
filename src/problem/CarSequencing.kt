package problem

import data.CarSequence
import data.newName
import discrete.Domain
import discrete.Goal
import discrete.Problem
import discrete.ProblemType
import discrete.Solution
import discrete.Variables
import fn.Constraint
import fn.CoreFn
import fn.StringFn
import fn.asSequence
import kotlin.math.min

fun newCarSequencing(variant: String, n: Int): Problem? {
	val name = newName(CarSequencing, variant, n)
	return when (variant) {
		"basic" -> carSequencing(name)
		else -> null
	}
}

fun carSequencing(name: String): Problem? {
	val cfg = CarSequence.new(name) ?: return null
	if (cfg.optionMax.size != cfg.optionWindow.size) return null
	val numCars = cfg.cars.size
	val numOptions = cfg.optionMax.size

	val p = Problem(
		name,
		description =  cfg.toString(),
		type = ProblemType.SEQUENCE,
		goal = Goal.SATISFY,
		variables = Variables.from(cfg.cars),
	)
	p.addVariableDomains(Domain.index(numCars))

	p.addUniversalConstraint(Constraint::allUnique)
	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		val optionSequence = (0 until numOptions).map { MutableList(numCars) { false } }
		for ((seqIdx, x) in solution.asSequence().withIndex()) {
			val carOptions = cfg.carOptions[cfg.cars[x]] ?: emptyList()
			for((optionIdx, flag) in carOptions.withIndex()) {
				optionSequence[optionIdx][seqIdx] = flag
			}
		}
		for ((optionIdx, maxCount) in cfg.optionMax.withIndex()) {
			val windowSize = cfg.optionWindow[optionIdx]
			for(i in 0 until numCars) {
				val limit = min(numCars, i+windowSize)
				val window = optionSequence[optionIdx].subList(i, limit)
				if (window.count { it } > maxCount) return false
			}
		}
		return true
	})

	p.solutionCoreFn = CoreFn.mirroredSequence(cfg.cars)
	p.solutionStringFn = StringFn.sequence(cfg.cars)
	return p
}

























