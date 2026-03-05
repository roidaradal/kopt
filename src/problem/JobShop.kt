package problem

import data.ShopSchedule
import data.Task
import data.newName
import discrete.Domain
import discrete.Goal
import discrete.Problem
import discrete.ProblemType
import discrete.Solution
import discrete.Value
import discrete.Variable
import fn.Constraint
import fn.ScoreFn
import fn.StringFn

fun newJobShopScheduling(variant: String, n: Int): Problem? {
	val name = newName(JobShopScheduling, variant, n)
	return when (variant) {
		"basic" -> jobShopScheduling(name)
		else -> null
	}
}

fun jobShopScheduling(name: String): Problem? {
	val cfg = ShopSchedule.new(name) ?: return null

	var variable = 0
	val jobTasks = mutableMapOf<Int, List<Variable>>()
	val allTasks  = mutableListOf<Task>()
	val variables = mutableListOf<Variable>()
	val domain = mutableMapOf<Variable, List<Value>>()
	for((jobID, job) in cfg.jobs.withIndex()) {
		val taskVariables = mutableListOf<Variable>()
		val tasks = cfg.jobTasks[job] ?: emptyList()
		for((taskID, task) in tasks.withIndex()) {
			val first = tasks.subList(0, taskID).sumOf { it.duration }
			val after = tasks.subList(taskID+1, tasks.size).sumOf { it.duration }
			val last = cfg.maxMakespan - after - task.duration

			variables.add(variable)
			domain[variable] = Domain.range(first, last)
			taskVariables.add(variable)
			allTasks.add(task)
			variable += 1
		}
		jobTasks[jobID] = taskVariables
	}

	val p = Problem(
		name,
		description = cfg.toString(),
		type = ProblemType.ASSIGNMENT,
		goal = Goal.MINIMIZE,
		variables = variables,
		objectiveFn = ScoreFn.scheduleMakespan(allTasks),
		solutionStringFn =  StringFn.shopSchedule(allTasks, cfg.machines),
	)
	p.domain.putAll(domain)

	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		for((_, variables) in jobTasks) {
			for (i in 0 until variables.size-1) {
				val curr = variables[i]
				val start1 = solution.map[curr] ?: return false
				val start2 = solution.map[variables[i+1]] ?: return false
				val end1 = start1 + allTasks[curr].duration
				if (start2 <= start1 || start2 < end1) return false
			}
		}
		return true
	})

	p.addUniversalConstraint(Constraint.noMachineOverlap(cfg, allTasks))

	return p
}