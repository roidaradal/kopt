package problem

import discrete.Problem

const val BinCover = "bin_cover"
const val BinPacking = "bin_packing"
const val CarPainting = "car_painting"
const val CarSequencing = "car_sequencing"
const val Clique = "clique"
const val CliqueCover = "clique_cover"
const val DominatingSet = "dominating_set"
const val EdgeColoring = "edge_coloring"
const val EdgeCover = "edge_cover"
const val GraphMatching = "graph_matching"
const val GraphPartition = "graph_partition"
const val GraphTour = "graph_tour"
const val IndependentSet = "independent_set"
const val InducedPath = "induced_path"
const val Interval = "interval"
const val KCenter = "k_center"
const val KCut = "k_cut"
const val Knapsack = "knapsack"
const val MaxCoverage = "max_coverage"
const val NumberColoring = "number_coloring"
const val NumberPartition = "number_partition"
const val Satisfaction = "satisfaction"
const val SetCover = "set_cover"
const val SetPacking = "set_packing"
const val SetSplitting = "set_splitting"
const val SpanningTree = "spanning_tree"
const val SteinerTree = "steiner_tree"
const val Subsequence = "subsequence"
const val SubsetSum = "subset_sum"
const val TravelingPurchaser = "traveling_purchaser"
const val TravelingSalesman = "traveling_salesman"
const val VertexColoring = "vertex_coloring"
const val VertexCover = "vertex_cover"

val Creator: Map<String, (String, Int) -> Problem?>  = mapOf(
	BinCover to ::newBinCover,
	BinPacking to ::newBinPacking,
	CarPainting to ::newCarPainting,
	CarSequencing to ::newCarSequencing,
	Clique to ::newClique,
	CliqueCover to ::newCliqueCover,
	DominatingSet to ::newDominatingSet,
	EdgeColoring to ::newEdgeColoring,
	EdgeCover to ::newEdgeCover,
	GraphMatching to ::newGraphMatching,
	GraphPartition to ::newGraphPartition,
	GraphTour to ::newGraphTour,
	IndependentSet to ::newIndependentSet,
	InducedPath to ::newInducedPath,
	Interval to ::newInterval,
	KCenter to ::newKCenter,
	KCut to ::newKCut,
	Knapsack to ::newKnapsack,
	MaxCoverage to ::newMaxCoverage,
	NumberColoring to ::newNumberColoring,
	NumberPartition to ::newNumberPartition,
	Satisfaction to ::newSatisfaction,
	SetCover to ::newSetCover,
	SetPacking to ::newSetPacking,
	SetSplitting to ::newSetSplitting,
	SpanningTree to ::newSpanningTree,
	SteinerTree to ::newSteinerTree,
	Subsequence to ::newSubsequence,
	SubsetSum to ::newSubsetSum,
	TravelingPurchaser to ::newTravelingPurchaser,
	TravelingSalesman to ::newTravelingSalesman,
	VertexColoring to ::newVertexColoring,
	VertexCover to ::newVertexCover,
)
