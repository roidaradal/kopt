package data

data class Warehouse(
	val stores: List<String> = emptyList(),
	val warehouses: List<String> = emptyList(),
	val capacity: List<Int> = emptyList(),
	val warehouseCost: List<Double> = emptyList(),
	val storeCost: List<List<Double>> = emptyList(),
) {
	override fun toString(): String {
		return "Stores: $stores\nWarehouse: $warehouses\nCapacity: $capacity\nCost: $warehouseCost\nStoreCost: $storeCost"
	}

	companion object {
		fun new(name: String): Warehouse? {
			val data = load(name) ?: return null
			val storeCost = mutableListOf<List<Double>>()
			for(line in data["storeCost"].parseList()) {
				storeCost.add(line.matrixRow(true))
			}
			return Warehouse(
				stores = data["stores"].toStringList(),
				warehouses = data["warehouses"].toStringList(),
				capacity = data["capacity"].toIntList(),
				warehouseCost = data["warehouseCost"].toDoubleList(),
				storeCost =  storeCost,
			)
		}
	}
}

data class NurseSchedule(
	val nurses: List<String> = emptyList(),
	val days: List<String> = emptyList(),
	val shifts: List<String> = emptyList(),
	val shiftMin: List<Int> = emptyList(),
	val shiftMax: List<Int> = emptyList(),
	val maxConsecutiveShift: Int = 0,
	val maxTotalShift: Int = 0,
	val dailyLimit: Int= 0,
	val preferShifts: Map<String, List<String>> = emptyMap(),
	val preferDays: Map<String, List<String>> = emptyMap(),
) {
	override fun toString(): String {
		return "Nurses: $nurses\nDays: $days\nShifts: $shifts"
	}

	fun nursePrefersShift(nurse: String, shift: String): Boolean {
		val pref = preferShifts[nurse] ?: return false
		return pref.contains(shift)
	}

	fun nursePrefersDays(nurse: String, day: String): Boolean {
		val pref = preferDays[nurse] ?: return false
		return pref.contains(day)
	}

	companion object {
		fun new(name: String): NurseSchedule? {
			val data = load(name) ?: return null
			val preferShifts = mutableMapOf<String, List<String>>()
			for((nurse, value) in data["preferShifts"].parseMap()) {
				preferShifts[nurse] = value.toStringList()
			}
			val preferDays = mutableMapOf<String, List<String>>()
			for((nurse, value) in data["preferDays"].parseMap()) {
				preferDays[nurse] = value.toStringList()
			}
			return NurseSchedule(
				nurses = data["nurses"].toStringList(),
				days = data["days"].toStringList(),
				shifts = data["shifts"].toStringList(),
				shiftMin = data["shiftMin"].toIntList(),
				shiftMax = data["shiftMax"].toIntList(),
				maxConsecutiveShift = data["maxConsecutiveShift"].parseInt(),
				maxTotalShift = data["maxTotalShift"].parseInt(),
				dailyLimit = data["dailyLimit"].parseInt(),
				preferShifts = preferShifts,
				preferDays = preferDays,
			)
		}
	}
}
