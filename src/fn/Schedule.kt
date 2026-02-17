package fn

fun maxConsecutive(slots: List<Int>, limit: Int): Boolean {
	if (slots.isEmpty()) return true
	val sortedSlots = slots.sorted()
	var group = mutableListOf(sortedSlots[0])
	var prev = sortedSlots[0]
	for(slot in sortedSlots.subList(1, sortedSlots.size)) {
		if (prev+1 == slot) {
			group.add(slot)
		} else {
			if (group.size > limit) return false
			group = mutableListOf(slot)
		}
		prev = slot
	}
	return group.size <= limit
}
