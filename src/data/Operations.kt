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