package problem

import discrete.Problem

const val BinCover = "bin_cover"
const val BinPacking = "bin_packing"
const val Interval = "interval"
const val Knapsack = "knapsack"

val Creator: Map<String, (String, Int) -> Problem?>  = mapOf(
	BinCover to ::newBinCover,
	BinPacking to ::newBinPacking,
	Interval to ::newInterval,
	Knapsack to ::newKnapsack,
)
