package discrete

typealias Score = Double
typealias ObjectiveFn = (Solution) -> Score
typealias SolutionCoreFn = (Solution) -> String
typealias SolutionStringFn = (Solution) -> String
typealias SolutionDisplayFn = (Solution) -> String

class Solution {
	var score: Score = 0.0
	var isPartial: Boolean = true
	val map: MutableMap<Variable, Value> = mutableMapOf()
	private val variableOrder: MutableList<Variable> = mutableListOf()
}