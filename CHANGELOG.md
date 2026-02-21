## v0.1.44 - Generalized Assignment 
* **Commit**: 2026-02-21 17:11
* Add `general`, `weapon` to Assignment 
* data.Weapons
## v0.1.43 - Quadratic Assignment 
* **Commit**: 2026-02-20 21:06
* Add `quadratic`, `quadratic_bottleneck` to Assignment 
* data.QuadraticAssignment
## v0.1.42 - Assignment 
* **Commit**: 2026-02-19 22:11
* problem.newAssignment
* data.AssignmentCfg 
* StringFn.Assignment
## v0.1.41 - Flow Shop Scheduling 
* **Commit**: 2026-02-18 23:03
* problem.newFlowShopScheduling 
* data.FlowShop
## v0.1.40 - Nurse Scheduling 
* **Commit**: 2026-02-17 21:34
* problem.newNurseScheduling
* data.NurseSchedule 
* fn.MaxConsecutive
## v0.1.39 - Scene Allocation
* **Commit**: 2026-02-16 20:00
* Add `scene` to Allocation
## v0.1.38 - Graph Path 
* **Commit**: 2026-02-15 12:56
* problem.newGraphPath
* data.GraphPath.new
* Constraint.simplePath 
* StringFn.graphPath
* ScoreFn.pathCost
* fn.PathDistances
## v0.1.37 - Allocation 
* **Commit**: 2026-02-15 11:34
* problem.newAllocation
* data.Resource
## v0.1.36 - Warehouse Location 
* **Commit**: 2026-02-14 08:45
* problem.newWarehouseLocation 
* data.Warehouse
## v0.1.35 - Car Sequencing
* **Commit**: 2026-02-13 06:25
* problem.newCarSequencing 
* data.CarSequence
## v0.1.34 - Car Painting 
* **Commit**: 2026-02-12 21:12
* problem.newCarPainting
* data.Cars
* fn.CountColorChanges
## v0.1.33 - Traveling Purchaser 
* **Commit**: 2026-02-11 21:12
* problem.newTravelingPurchaser
## v0.1.32 - Traveling Salesman 
* **Commit**: 2026-02-10 21:35
* problem.newTravelingSalesman
* data.GraphPath 
## v0.1.31 - Steiner Tree
* **Commit**: 2026-02-09 22:23
* problem.newSteinerTree
## v0.1.30 - Spanning Tree
* **Commit**: 2026-02-09 21:45
* problem.newSpanningTree
* Constraint.allVerticesCovered
* Constraint.spanningTree
## v0.1.29 - K-Center 
* **Commit**: 2026-02-08 21:21
* problem.newKCenter
## v0.1.28 - Graph Tour 
* **Commit**: 2026-02-08 15:18
* problem.newGraphTour
* StringFn.eulerianPath
* CoreFn.sortedCycle
* Graph.isEulerianPath
* Graph.isHamiltonianPath
## v0.1.27 - Induced Path 
* **Commit**: 2026-02-08 08:33
* problem.newInducedPath
## v0.1.26 - Independent Set 
* **Commit**: 2026-02-08 08:02
* problem.newIndependentSet
## v0.1.25 - Graph Partition 
* **Commit**: 2026-02-08 07:44 
* problem.newGraphPartition
## v0.1.24 - Graph Matching
* **Commit**: 2026-02-08 07:30
* problem.newGraphMatching
## v0.1.23 - Edge Cover 
* **Commit**: 2026-02-07 20:44
* problem.newEdgeCover
## v0.1.22 - Edge Coloring 
* **Commit**: 2026-02-07 20:31
* problem.newEdgeColoring
## v0.1.21 - Dominating Set 
* **Commit**: 2026-02-07 20:05
* problem.newDominatingSet
## v0.1.20 - Clique Cover 
* **Commit**: 2026-02-07 16:50
* problem.newCliqueCover
## v0.1.19 - Clique 
* **Commit**: 2026-02-07 16:29
* problem.newClique
## v0.1.18 - K-Cut 
* **Commit**: 2026-02-07 16:13
* problem.newKCut
## v0.1.17 - Number Coloring 
* **Commit**: 2026-02-07 14:58
* problem.newNumberColoring
## v0.1.16 - Vertex Coloring 
* **Commit**: 2026-02-07 14:49
* problem.newVertexColoring 
* data.GraphColoring
* ConstraintFn.properVertexColoring 
* CoreFn.lookupValueOrder
## v0.1.15 - Vertex Cover 
* **Commit**: 2026-02-07 12:29
* problem.newVertexCover
* data.Graph, data.Edge
* Add `topological_sort` to Satisfaction
## v0.1.14 - Set Splitting 
* **Commit**: 2026-02-07 09:29
* problem.newSetSplitting
## v0.1.13 - Set Packing
* **Commit**: 2026-02-07 08:57
* problem.newSetPacking
## v0.1.12 - Set Cover
* **Commit** 2026-02-07 08:45
* problem.newSetCover
* Add `exact_cover` to Satisfaction
## v0.1.11 - Satisfaction
* **Commit**: 2026-02-07 07:49
* problem.newSatisfaction
* CoreFn.mirroredSequence
* CoreFn.mirroredValues
* StringFn.sequence
* StringFn.values
* ConstraintFn.allUnique
## v0.1.10 - Subsequence
* **Commit**: 2026-02-06 18:27
* problem.newSubsequence
## v0.1.9 - Subset Sum 
* **Commit**: 2026-02-05 20:50
* problem.newSubsetSum
## v0.1.8 - Number Partition 
* **Commit**: 2026-02-04 21:59
* data.Numbers
* problem.newNumberPartition
## v0.1.7 - Max Coverage 
* **Commit**: 2026-02-03 20:54
* data.Subsets
* problem.newMaxCoverage
## v0.1.6 - Knapsack 
* **Commit**: 2026-02-03 20:32
* data.KnapsackCfg
* problem.newKnapsack
## v0.1.5 - Interval
* **Commit**: 2026-02-02 21:18
* data.Intervals
* fn.Solution.asSubset 
* fn.stringSubset
* fn.scoreSubsetSize
* fn.scoreSumWeightedValues
* problem.newInterval
## v0.1.4 - Bin Packing 
* **Commit**: 2026-02-01 23:04
* problem.newBinPacking
## v0.1.3 - Bin Cover 
* **Commit**: 2026-02-01 22:56
* data.Bins
* discrete.Variables
* fn.Solution.asPartition
* fn.Solution.partitionStrings, partitionSums
* fn.coreSortedPartition
* fn.scoreCountUniqueValues
* fn.stringPartition
* problem.newBinCover
## v0.1.2 - Read Problem Data 
* **Commit**: 2026-02-01 17:27
* data.load function
## v0.1.1 - Problem Solution and Constraints
* **Commit**: 2026-02-01 15:59
* Update Problem constructor
* class: Constraint, Solution
* types: ConstraintFn, Penalty, Score
* types: ObjectiveFn, SolutionCoreFn, SolutionStringFn, SolutionDisplayFn
## v0.1.0 - Discrete Optimization Problem
* **Commit**: 2026-02-01 14:57
* class: Problem
* types: Variable, Value 
* enums: Goal, ProblemType 