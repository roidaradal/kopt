package data

import java.nio.file.Paths
import kotlin.io.path.readLines

private enum class Mode {
	Outside, Inside, List, Map
}

typealias StringPair = Pair<String, String>
typealias StringMap = Map<String, String>

const val commentStart = '#'
const val nameSeparator = '.'
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
		var mode = Mode.Outside
		val group: MutableList<String> = mutableListOf()
		val data: MutableMap<String, String> = mutableMapOf()
		for (line in lines) {
			val endsWithBrace = line.endsWith('{')
			val endsWithBracket = line.endsWith('[')
			val isEntry = line.contains(entrySeparator)
			when (mode) {
				Mode.Outside if endsWithBrace -> {
					testCase = line.trimEnd('{').trim()
					mode = Mode.Inside
				}
				Mode.Inside if line == "}" -> {
					cacheData[Pair(problemName, testCase)] = data.toMap()
					data.clear()
					mode = Mode.Outside
				}
				Mode.Inside if isEntry && endsWithBrace -> {
					currentKey = line.split(entrySeparator)[0].trim()
					mode = Mode.Map
				}
				Mode.Inside if isEntry && endsWithBracket -> {
					currentKey = line.split(entrySeparator)[0].trim()
					mode = Mode.List
				}
				Mode.Inside if isEntry -> {
					val (key, value) = line.split(entrySeparator, limit = 2).map { it.trim() }
					data[key] = value
				}
				Mode.List if line == "]" -> {
					data[currentKey] = group.joinToString(listSeparator)
					group.clear()
					mode = Mode.Inside
				}
				Mode.Map if line == "}" -> {
					data[currentKey] = group.joinToString(listSeparator)
					group.clear()
					mode = Mode.Inside
				}
				Mode.List, Mode.Map -> {
					group.add(line)
				}
				else -> continue
			}
		}
	}
	return cacheData[mainKey]
}

fun newName(problem: String, variant: String, n: Int): String = "$problem$nameSeparator$variant$nameSeparator$n"

fun String?.parseInt(): Int = this?.toIntOrNull() ?: 0
fun String?.parseDouble(): Double = this?.toDoubleOrNull() ?: 0.0

fun String?.toStringList(): List<String> {
	return if (this.isNullOrBlank()) {
		emptyList()
	} else {
		this.trim().split("\\s+".toRegex())
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
//fun String.parseMap(): StringMap {
//	return this.parseList().associate {
//		val (key, value) = it.split(entrySeparator, limit = 2).map(String::trim)
//		Pair(key, value)
//	}
//}