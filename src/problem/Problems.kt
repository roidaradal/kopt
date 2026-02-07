package problem

import discrete.Problem

const val BinCover = "bin_cover"
const val BinPacking = "bin_packing"
const val Interval = "interval"
const val Knapsack = "knapsack"
const val MaxCoverage = "max_coverage"
const val NumberColoring = "number_coloring"
const val NumberPartition = "number_partition"
const val Satisfaction = "satisfaction"
const val SetCover = "set_cover"
const val SetPacking = "set_packing"
const val SetSplitting = "set_splitting"
const val Subsequence = "subsequence"
const val SubsetSum = "subset_sum"
const val VertexColoring = "vertex_coloring"
const val VertexCover = "vertex_cover"

val Creator: Map<String, (String, Int) -> Problem?>  = mapOf(
	BinCover to ::newBinCover,
	BinPacking to ::newBinPacking,
	Interval to ::newInterval,
	Knapsack to ::newKnapsack,
	MaxCoverage to ::newMaxCoverage,
	NumberColoring to ::newNumberColoring,
	NumberPartition to ::newNumberPartition,
	Satisfaction to ::newSatisfaction,
	SetCover to ::newSetCover,
	SetPacking to ::newSetPacking,
	SetSplitting to ::newSetSplitting,
	Subsequence to ::newSubsequence,
	SubsetSum to ::newSubsetSum,
	VertexColoring to ::newVertexColoring,
	VertexCover to ::newVertexCover,
)
