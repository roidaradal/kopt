package problem

import data.Cars
import data.newName
import discrete.Domain
import discrete.Goal
import discrete.Problem
import discrete.ProblemType
import discrete.Score
import discrete.Solution
import discrete.Variables
import fn.Constraint
import fn.StringFn
import fn.asSequence
import fn.countColorChanges
import kotlin.math.max
import kotlin.math.min

fun newCarPainting(variant: String, n: Int): Problem? {
	val name = newName(CarPainting, variant, n)
	return when (variant) {
		"basic" -> carPainting(name)
		"binary" -> binaryPaintShop(name)
		else -> null
	}
}

fun carPainting(name: String): Problem? {
	val cfg = Cars.new(name) ?: return null
	if (cfg.carColors.isEmpty() || cfg.maxShift == 0) return null
	val numCars = cfg.carColors.size

	val p = Problem(
		name = name,
		description = "Cars: ${cfg.carColors}\nMaxShift: ${cfg.maxShift}",
		type = ProblemType.ASSIGNMENT,
		goal = Goal.MINIMIZE,
		variables = Variables.from(cfg.carColors),
	)
	val maxLimit = numCars-1
	for((idx, variable) in p.variables.withIndex()) {
		val first = max(0, idx-cfg.maxShift)
		val last = min(maxLimit, idx+cfg.maxShift)
		p.domain[variable] = Domain.range(first, last)
	}

	p.addUniversalConstraint(Constraint::allUnique)

	p.objectiveFn = fun(solution: Solution): Score {
		val colorSequence = MutableList(numCars) { "" }
		for ((i, x) in solution.asSequence().withIndex()) {
			colorSequence[i] = cfg.carColors[x]
		}
		return countColorChanges(colorSequence).toDouble()
	}

	p.solutionCoreFn = fun(solution: Solution): String {
		var prevColor = ""
		val groups = mutableListOf<List<Int>>()
		var group = mutableListOf<Int>()
		for ((i, x) in solution.asSequence().withIndex()) {
			val currColor = cfg.carColors[x]
			if (i > 0 && prevColor != currColor) {
				groups.add(group)
				group = mutableListOf(x)
			} else {
				group.add(x)
			}
			prevColor = currColor
		}
		groups.add(group)
		val output = groups.map { group -> group.sorted().joinToString(separator = " ") }
		return output.joinToString("|")
	}

	p.solutionStringFn = fun(solution: Solution): String {
		var prevColor = ""
		val output = mutableListOf<String>()
		for((i, x) in solution.asSequence().withIndex()) {
			val currColor = cfg.carColors[x]
			if (i > 0 && prevColor != currColor) {
				output.add("|")
			}
			output.add("$x:$currColor")
			prevColor  = currColor
		}
		return output.joinToString(" ")
	}

	return p
}

fun binaryPaintShop(name: String): Problem? {
	val cfg = Cars.new(name) ?: return null
	if(cfg.cars.isEmpty() || cfg.sequence.isEmpty()) return null

	val p = Problem(
		name,
		description = "Cars: ${cfg.cars}\nSequence: ${cfg.sequence}",
		type = ProblemType.ASSIGNMENT,
		goal = Goal.MINIMIZE,
		variables = Variables.from(cfg.cars),
	)
	p.addVariableDomains(Domain.boolean())
	p.domain[p.variables[0]] = listOf(0)

	p.objectiveFn = fun(solution: Solution): Score {
		val color = MutableList(cfg.cars.size) { 0 }
		for((x,c) in solution.map) {
			color[x] = c
		}
		val colorSequence = mutableListOf<Int>()
		for(car in cfg.sequence) {
			val x = cfg.cars.indexOf(car)
			colorSequence.add(color[x])
			color[x] = (color[x] + 1) % 2
		}
		return countColorChanges(colorSequence).toDouble()
	}

	p.solutionStringFn = StringFn.values<Int>(p, null)
	return p
}