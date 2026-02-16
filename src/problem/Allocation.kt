package problem

import data.Resource
import data.Scene
import data.newName
import discrete.Domain
import discrete.Goal
import discrete.Problem
import discrete.ProblemType
import discrete.Score
import discrete.Solution
import discrete.Variables
import fn.ScoreFn
import fn.StringFn
import fn.asPartition

fun newAllocation(variant: String, n: Int): Problem? {
	val name = newName(Allocation, variant, n)
	return when (variant) {
		"resource" -> resourceAllocation(name)
		"scene" -> sceneAllocation(name)
		else -> null
	}
}

fun resourceAllocation(name: String): Problem? {
	val cfg = Resource.new(name) ?: return null
	val p = Problem(
		name,
		description = cfg.toString(),
		type = ProblemType.ASSIGNMENT,
		goal = Goal.MAXIMIZE,
		variables = Variables.from(cfg.items),
	)
	for((i, variable) in p.variables.withIndex()) {
		p.domain[variable] = Domain.range(0, cfg.count[i])
	}

	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		val count = solution.map
		return p.variables.sumOf { (count[it] ?: 0) * cfg.cost[it] } <= cfg.budget
	})

	p.objectiveFn = ScoreFn.sumWeightedValues(p.variables, cfg.value)
	p.solutionStringFn = StringFn.values<Int>(p, null)
	return p
}

fun sceneAllocation(name: String): Problem? {
	val cfg = Scene.new(name) ?: return null
	val p = Problem(
		name,
		description = cfg.toString(),
		type = ProblemType.ASSIGNMENT,
		goal = Goal.MINIMIZE,
		variables = Variables.from(cfg.scenes),
	)
	val domain = Domain.index(cfg.numDays)
	p.addVariableDomains(domain)

	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		return solution.asPartition(domain).mapIndexed { day, scenes ->
			val numScenes = scenes.size
			cfg.dayMin[day] <= numScenes && numScenes <= cfg.dayMax[day]
		}.all { it }
	})

	p.objectiveFn = fun(solution: Solution): Score {
		var score: Score = 0.0
		for(scenes in solution.asPartition(domain)) {
			val actorsToday = mutableSetOf<String>()
			for(x in scenes) {
				val actors = cfg.sceneActors[cfg.scenes[x]] ?: continue
				actorsToday.addAll(actors)
			}
			score += actorsToday.toList().sumOf { cfg.dailyCost[it] ?: 0.0 }
		}
		return score
	}

	p.solutionStringFn = StringFn.partition(domain, cfg.scenes)
	return p
}