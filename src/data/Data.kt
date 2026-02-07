package data

import java.nio.file.Paths
import kotlin.io.path.readLines

private enum class Mode {
	OUTSIDE, INSIDE, LIST, MAP
}

typealias StringPair = Pair<String, String>
typealias StringMap = Map<String, String>

const val commentStart = '#'
const val nameSeparator = "."
const val entrySeparator = ':'
const val listSeparator = "|"

val cacheData = mutableMapOf<StringPair, StringMap>()

fun load(name: String): StringMap? {
	val (problemName, mainTestCase) = name.split(nameSeparator, limit = 2)
	val mainKey = StringPair(problemName, mainTestCase)

	if (!cacheData.containsKey(mainKey)) {
		// Load test case file
		val path = Paths.get("data", "$problemName.txt")
		val lines: List<String>
		try {
			lines = path.readLines().map { it.trim() }.filter { it.isNotEmpty() && !it.startsWith(commentStart) }
		} catch (e: Exception) {
			println("Exception: ${e.message}")
			return null
		}
		var (testCase, currentKey) = listOf("", "")
		var mode = Mode.OUTSIDE
		val group: MutableList<String> = mutableListOf()
		val data: MutableMap<String, String> = mutableMapOf()
		for (line in lines) {
			val endsWithBrace = line.endsWith('{')
			val endsWithBracket = line.endsWith('[')
			val isEntry = line.contains(entrySeparator)
			when (mode) {
				Mode.OUTSIDE if endsWithBrace -> {
					testCase = line.trimEnd('{').trim()
					mode = Mode.INSIDE
				}
				Mode.INSIDE if line == "}" -> {
					cacheData[Pair(problemName, testCase)] = data.toMap()
					data.clear()
					mode = Mode.OUTSIDE
				}
				Mode.INSIDE if isEntry && endsWithBrace -> {
					currentKey = line.split(entrySeparator)[0].trim()
					mode = Mode.MAP
				}
				Mode.INSIDE if isEntry && endsWithBracket -> {
					currentKey = line.split(entrySeparator)[0].trim()
					mode = Mode.LIST
				}
				Mode.INSIDE if isEntry -> {
					val (key, value) = line.split(entrySeparator, limit = 2).map { it.trim() }
					data[key] = value
				}
				Mode.LIST if line == "]" -> {
					data[currentKey] = group.joinToString(listSeparator)
					group.clear()
					mode = Mode.INSIDE
				}
				Mode.MAP if line == "}" -> {
					data[currentKey] = group.joinToString(listSeparator)
					group.clear()
					mode = Mode.INSIDE
				}
				Mode.LIST, Mode.MAP -> {
					group.add(line)
				}
				else -> continue
			}
		}
	}
	return cacheData[mainKey]
}

fun newName(problem: String, variant: String, n: Int): String = listOf(problem, variant, n.toString()).joinToString(nameSeparator)

fun String.spaceSplit(): List<String> = this.trim().split("\\s+".toRegex())

fun String?.parseInt(): Int = this?.toIntOrNull() ?: 0
fun String?.parseDouble(): Double = this?.toDoubleOrNull() ?: 0.0

fun String?.toStringList(): List<String> {
	return if (this.isNullOrBlank()) {
		emptyList()
	} else {
		this.spaceSplit()
	}
}
fun String?.toIntList(): List<Int> {
	return if (this.isNullOrBlank()) {
		emptyList()
	} else {
		this.toStringList().map { it.parseInt() }
	}
}
fun String?.toDoubleList(): List<Double> {
	return if (this.isNullOrBlank()) {
		emptyList()
	} else {
		this.toStringList().map { it.parseDouble() }
	}
}

fun String?.parseList(): List<String> {
	return if (this.isNullOrBlank()) {
		emptyList()
	} else {
		this.trim().split(listSeparator).map { it.trim() }
	}
}
fun String?.parseMap(): StringMap {
	return if(this.isNullOrBlank()) {
		emptyMap()
	}else {
		this.parseList().associate {
			val (key, value) = it.split(entrySeparator, limit = 2).map(String::trim)
			Pair(key, value)
		}
	}
}