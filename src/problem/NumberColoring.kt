package problem

import data.graphNumbers
import data.graphVertices
import data.newName
import discrete.Problem
import discrete.Score
import discrete.Solution
import fn.Constraint
import fn.mapList

fun newNumberColoring(variant: String, n: Int): Problem? {
	val name = newName(NumberColoring, variant, n)
	return when (variant) {
		"sum" -> sumColoring(name)
		else -> null
	}
}

fun sumColoring(name: String): Problem? {
	val (p, cfg) = newGraphColoringProblem(name, ::graphVertices, ::graphNumbers)
	if(p == null || cfg == null || cfg.numbers.isEmpty()) return null

	p.description += "\nNumbers: ${cfg.numbers}"
	p.addUniversalConstraint(Constraint.properVertexColoring(cfg.graph))
	p.objectiveFn = fun(solution: Solution): Score {
		return solution.map.values.toList().mapList(cfg.numbers).sum().toDouble()
	}
	return p
}