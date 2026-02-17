package problem

import data.NurseSchedule
import data.newName
import discrete.Domain
import discrete.Goal
import discrete.Problem
import discrete.ProblemType
import discrete.Score
import discrete.Solution
import discrete.Variables
import fn.groupByValue
import fn.increment
import fn.maxConsecutive

fun newNurseScheduling(variant: String, n: Int): Problem? {
	val name = newName(NurseScheduling, variant, n)
	return when (variant) {
		"basic" -> nurseScheduling(name)
		else -> null
	}
}

fun nurseScheduling(name: String): Problem? {
	val cfg = NurseSchedule.new(name) ?: return null
	val numDays = cfg.days.size
	val numShifts = cfg.shifts.size

	var slotIdx = 0
	val dayOf = mutableMapOf<Int, Int>()
	val shiftOf = mutableMapOf<Int, Int>()
	for (dayIdx in 0 until numDays) {
		for (shiftIdx in 0 until numShifts) {
			repeat(cfg.shiftMin[shiftIdx]) {
				dayOf[slotIdx] = dayIdx
				shiftOf[slotIdx] = shiftIdx
				slotIdx += 1
			}
		}
	}

	val p = Problem(
		name,
		description = cfg.toString(),
		type = ProblemType.ASSIGNMENT,
		goal = Goal.MINIMIZE,
		variables = Variables.index(slotIdx),
	)
	val domain = Domain.from(cfg.nurses)
	p.addVariableDomains(domain)

	val groupSlotSched = fun(solution: Solution): Map<IntPair, Set<Int>> {
		val sched = mutableMapOf<IntPair, MutableSet<Int>>()
		for (dayIdx in 0 until numDays) {
			for (shiftIdx in 0 until numShifts) {
				val key = IntPair(dayIdx, shiftIdx)
				sched[key] = mutableSetOf()
			}
		}
		for((slot, nurse) in solution.map) {
			val dayIdx = dayOf[slot] ?: continue
			val shiftIdx = shiftOf[slot] ?: continue
			val key = IntPair(dayIdx, shiftIdx)
			sched[key]?.add(nurse)
		}
		return sched
	}

	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		val sched = groupSlotSched(solution)
		for(dayIdx in 0 until numDays) {
			for(shiftIdx in 0 until numShifts) {
				val key = IntPair(dayIdx, shiftIdx)
				val nurseCount = sched[key]?.size ?: 0
				if (nurseCount < cfg.shiftMin[shiftIdx] || nurseCount > cfg.shiftMax[shiftIdx]) return false
			}
		}
		return true
	})

	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		val shiftCount = mutableMapOf<Int, Int>()
		for((_, nurse) in solution.map) {
			shiftCount.increment(nurse)
		}
		return shiftCount.values.all { it <= cfg.maxTotalShift }
	})

	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		val dailySched = mutableMapOf<IntPair, Int>()
		for((slot, nurse) in solution.map) {
			val dayIdx = dayOf[slot] ?: return false
			dailySched.increment(IntPair(nurse, dayIdx))
		}
		return dailySched.values.all { it <= cfg.dailyLimit }
	})

	p.addUniversalConstraint(fun(solution: Solution): Boolean {
		val nurseSlots = solution.groupByValue()
		return domain.all { nurse -> maxConsecutive(nurseSlots[nurse] ?: emptyList(), cfg.maxConsecutiveShift) }
	})

	p.objectiveFn = fun(solution: Solution): Score {
		var penalty = 0.0
		for((slot, nurseIdx) in solution.map) {
			val nurse = cfg.nurses[nurseIdx]
			val shiftIdx = shiftOf[slot] ?: continue
			val dayIdx = dayOf[slot] ?: continue
			val shift = cfg.shifts[shiftIdx]
			val day = cfg.days[dayIdx]
			if (!cfg.nursePrefersShift(nurse, shift)) penalty += 1
			if (!cfg.nursePrefersDays(nurse, day)) penalty += 1
		}
		return penalty
	}

	val nurseSched = fun(solution: Solution): String {
		val sched = groupSlotSched(solution)
		val out = mutableListOf<String>()
		for (dayIdx in 0 until numDays) {
			for(shiftIdx in 0 until numShifts) {
				val key = IntPair(dayIdx, shiftIdx)
				val nurses = (sched[key]?.toList() ?: emptyList()).map { cfg.nurses[it] }.sorted().joinToString(", ")
				out.add("${cfg.days[dayIdx]}_${cfg.shifts[shiftIdx]} = $nurses")
			}
		}
		return out.joinToString(" | ")
	}
	p.solutionCoreFn = nurseSched
	p.solutionStringFn = nurseSched
	return p
}