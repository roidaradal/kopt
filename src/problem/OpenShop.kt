package problem

import data.ShopSchedule
import data.Task
import data.newName
import discrete.Domain
import discrete.Goal
import discrete.Problem
import discrete.ProblemType
import discrete.Variables
import fn.Constraint
import fn.ScoreFn
import fn.StringFn
import fn.noOverlap

fun newOpenShopScheduling(variant: String, n: Int): Problem? {
	val name = newName(OpenShopScheduling, variant, n)
	return when (variant) {
		"basic" -> openShopScheduling(name)
		else -> null
	}
}

fun openShopScheduling(name: String): Problem? {
	val cfg = ShopSchedule.new(name) ?: return null
	val tasks = cfg.getTasks()
	val p = Problem(
		name,
		description = cfg.toString(),
		type = ProblemType.ASSIGNMENT,
		goal = Goal.MINIMIZE,
		variables = Variables.from(tasks),
		objectiveFn = ScoreFn.scheduleMakespan(tasks),
		solutionStringFn =  StringFn.shopSchedule(tasks, cfg.machines),
	)
	p.addVariableDomains(Domain.range(0, cfg.maxMakespan))

	p.addUniversalConstraint(noOverlap(tasks, cfg.jobs, fun(task: Task): String {
		return task.job
	}))

	p.addUniversalConstraint(Constraint.noMachineOverlap(cfg, tasks))

	return p
}