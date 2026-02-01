package problem

import discrete.Problem

const val BinCover = "bin_cover"
const val BinPacking = "bin_packing"

val Creator: Map<String, (String, Int) -> Problem?>  = mapOf(
	BinCover to ::newBinCover,
	BinPacking to ::newBinPacking,
)

fun newName(problem: String, variant: String, n: Int) = "$problem.$variant.$n"