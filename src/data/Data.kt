package data

import java.nio.file.Paths
import kotlin.io.path.readLines

private enum class Mode {
	Outside, Inside, List, Map
}

private typealias StringPair = Pair<String, String>
private typealias StringMap = Map<String, String>

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
		var group: MutableList<String> = mutableListOf()
		var data: MutableMap<String, String> = mutableMapOf()
		var mode = Mode.Outside
		for (line in lines) {
			val braceEnd = line.endsWith('{')
			val bracketEnd = line.endsWith('[')
			val isEntry = line.contains(entrySeparator)
			when (mode) {
				Mode.Outside if braceEnd -> {
					testCase = line.trimEnd('{').trim()
					mode = Mode.Inside
				}
				Mode.Inside if line == "}" -> {
					cacheData[Pair(problemName, testCase)] = data
					data = mutableMapOf()
					mode = Mode.Outside
				}
				Mode.Inside if isEntry && braceEnd -> {
					currentKey = line.split(entrySeparator)[0].trim()
					mode = Mode.Map
				}
				Mode.Inside if isEntry && bracketEnd -> {
					currentKey = line.split(entrySeparator)[0].trim()
					mode = Mode.List
				}
				Mode.Inside if isEntry -> {
					val (key, value) = line.split(entrySeparator, limit = 2)
					data[key] = value
				}
				Mode.List if line == "]" -> {
					data[currentKey] = group.joinToString(listSeparator)
					group = mutableListOf()
					mode = Mode.Inside
				}
				Mode.Map if line == "}" -> {
					data[currentKey] = group.joinToString(listSeparator)
					group = mutableListOf()
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

fun String.parseInt() = this.toIntOrNull() ?: 0
fun String.parseDouble() = this.toDoubleOrNull() ?: 0.0

fun String.stringList(): List<String> = this.trim().split("\\s+".toRegex())
fun String.intList(): List<Int> = this.stringList().map { it.parseInt() }
fun String.doubleList(): List<Double> = this.stringList().map { it.parseDouble() }

fun String.parseList(): List<String> = this.trim().split(listSeparator).map { it.trim() }
fun String.parseMap(): StringMap {
	return this.parseList().associate {
		val (key, value) = it.split(entrySeparator, limit = 2)
		Pair(key, value)
	}
}