package problem

import data.newName
import discrete.Domain
import discrete.Goal
import discrete.Problem
import discrete.ProblemType
import discrete.Solution
import discrete.Variables
import fn.ConstraintFn
import fn.CoreFn
import fn.StringFn
import fn.asSubset
import fn.tallyValues
import kotlin.math.absoluteValue

fun newSatisfaction(variant: String, n: Int): Problem? {
	val name = newName(Satisfaction, variant, n)
	return when (variant) {
		"exact_cover" -> exactCover(name)
		"langford" -> langfordPair(name, n)
		"magic_series" -> magicSeries(name, n)
		"n_queens" -> nQueens(name, n)
		else -> null
	}
}

fun exactCover(name: String): Problem? {
	val (p, cfg) = newSubsetsProblem(name)
	if (p == null || cfg == null) return null

	p.goal = Goal.SATISFY
	p.objectiveFn = null
	p.addUniversalConstraint(fun(solution: Solution): Boolean{
		val count = cfg.universal.associateWith { 0 }.toMutableMap()
		for(x in solution.asSubset()) {
			for(item in cfg.subsets[x]) {
				count[item] = (count[item] ?: 0) + 1
			}
		}
		return count.values.all { it == 1 }
	})
	return p
}


fun langfordPair(name: String, n: Int): Problem {
	val numPositions = n * 2
	val numbers = mutableListOf<Int>()
	for(i in 1..n) {
		numbers.add(i)
		numbers.add(i)
	}
	val p = Problem(
		name,
		description = "N: $n",
		type = ProblemType.SEQUENCE,
		goal = Goal.SATISFY,
		variables = Variables.from(numbers),
		solutionCoreFn = CoreFn.mirroredSequence(numbers),
		solutionStringFn = StringFn.sequence(numbers),
	)
	p.addVariableDomains(Domain.index(numPositions))
	p.addUniversalConstraint(ConstraintFn::allUnique)
	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		val index = solution.map
		for(x in 0 until numPositions step 2) {
			val n = (x / 2) + 1
			val idx1 = index[x] ?: return false
			val idx2 = index[x+1] ?: return false
			val gap = (idx2 - idx1).absoluteValue - 1
			if (gap != n) return false
		}
		return true
	})
	return p
}

fun magicSeries(name: String, n: Int): Problem {
	val domain = Domain.range(0, n)
	val p = Problem(
		name,
		description = "N: $n",
		type = ProblemType.ASSIGNMENT,
		goal = Goal.SATISFY,
		variables = Variables.range(0, n),
	)
	p.addVariableDomains(domain)
	p.solutionStringFn = StringFn.values<Int>(p, null)

	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		val value = solution.map
		val count = solution.tallyValues(domain)
		return p.variables.all { value[it] == count[it] }
	})
	return p
}

fun nQueens(name: String, n: Int): Problem {
	val p = Problem(
		name,
		description = "N: $n",
		type = ProblemType.SEQUENCE,
		goal = Goal.SATISFY,
		variables = Variables.range(1, n),
	)
	p.addVariableDomains(Domain.range(1, n))
	p.addUniversalConstraint(ConstraintFn::allUnique)
	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		val row = solution.map
		val occupied = mutableSetOf<IntPair>()
		for(x in p.variables) {
			val y = row[x] ?: return false
			occupied.add(IntPair(y, x))
		}
		for(x in p.variables) {
			val y = row[x] ?: return false
			if (hasDiagonalConflict(IntPair(y,x), occupied, n)) return false
		}
		return true
	})
	p.solutionCoreFn = CoreFn.mirroredValues<Int>(p, null)
	p.solutionStringFn = StringFn.values<Int>(p, null)
	return p
}

fun hasDiagonalConflict(coords: IntPair, occupied: Set<IntPair>, n: Int): Boolean {
	val (row, col) = coords
	// Upper left
	var y: Int = row-1
	var x: Int = col-1
	while (y >= 1 && x >= 1) {
		if (occupied.contains(IntPair(y, x))) return true
		y--; x--
	}
	// Upper right
	y = row-1; x = col+1
	while (y >= 1 && x <= n) {
		if (occupied.contains(IntPair(y, x))) return true
		y--; x++
	}
	// Bottom left
	y = row+1; x = col-1
	while (y <= n && x >= 1) {
		if (occupied.contains(IntPair(y, x))) return true
		y++; x--
	}
	// Bottom right
	y = row+1; x = col+1
	while (y <= n && x <= n) {
		if (occupied.contains(IntPair(y, x))) return true
		y++; x++
	}
	return false
}