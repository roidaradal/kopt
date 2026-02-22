package problem

import data.ItemAllocation
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
import fn.Constraint
import fn.ScoreFn
import fn.StringFn
import fn.asPartition
import kotlin.math.min

fun newAllocation(variant: String, n: Int): Problem? {
	val name = newName(Allocation, variant, n)
	return when (variant) {
		"resource" -> resourceAllocation(name)
		"scene" -> sceneAllocation(name)
		"fair_item" -> fairItemAllocation(name)
		"house" -> houseAllocation(name)
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

fun newItemAllocationProblem(name: String): Pair<Problem?, ItemAllocation?> {
	val cfg = ItemAllocation.new(name) ?: return Pair(null, null)
	val p = Problem(
		name,
		description = cfg.toString(),
		type = ProblemType.ASSIGNMENT,
		goal = Goal.MINIMIZE,
		variables = Variables.from(cfg.items),
	)
	p.addVariableDomains(Domain.from(cfg.persons))
	return Pair(p, cfg)
}

fun fairItemAllocation(name: String): Problem? {
	val (p, cfg) = newItemAllocationProblem(name)
	if (p == null || cfg == null) return null
	val domain = p.uniformDomain

	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		val partitions = solution.asPartition(domain).filter { it.isNotEmpty() }
		val minCount = min(cfg.items.size, cfg.persons.size)
		return partitions.size >= minCount
	})

	p.objectiveFn = fun(solution: Solution): Score {
		var envy = 0.0
		val partitions = solution.asPartition(domain)
		for((x1, p1) in cfg.persons.withIndex()) {
			val value1 = partitions[x1].sumOf { item ->
				val valueOf = cfg.value[p1] ?: return 0.0
				valueOf[item]
			}
			for(x2 in cfg.persons.indices) {
				if(x1 == x2) continue
				val value2 = partitions[x2].sumOf { item ->
					val valueOf = cfg.value[p1] ?: return 0.0
					valueOf[item]
				}
				if (value2 > value1) {
					envy += value2 - value1
				}
			}
		}
		return envy
	}

	p.solutionStringFn = StringFn.partition(domain, cfg.items)
	return p
}

fun houseAllocation(name: String): Problem? {
	val (p, cfg) = newItemAllocationProblem(name)
	if (p == null || cfg == null) return null

	p.addUniversalConstraint(Constraint::allUnique)

	p.objectiveFn = fun(solution: Solution): Score {
		val houseOf = solution.map.entries.associate { (k, v) -> v to k }
		var envy = 0.0
		for((x1, p1) in cfg.persons.withIndex()) {
			val house1 = houseOf[x1] ?: continue
			val value1 = cfg.value[p1]?.getOrNull(house1) ?: 0.0
			for(x2 in cfg.persons.indices) {
				if(x1 == x2) continue
				val house2 = houseOf[x2] ?: continue
				val value2 = cfg.value[p1]?.getOrNull(house2) ?: 0.0
				if (value2 > value1) {
					envy += value2 - value1
				}
			}
		}
		return envy
	}

	p.solutionStringFn = StringFn.map(p, cfg.items, cfg.persons)
	return p
}