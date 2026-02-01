package problem

import discrete.Problem

const val BinCover = "bin_cover"

val Creator: Map<String, (String, Int) -> Problem?>  = mapOf(
	BinCover to ::newBinCover,
)

fun newName(problem: String, variant: String, n: Int) = "$problem.$variant.$n"