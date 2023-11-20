package ch.slv.ifinance.csvconverter

import kotlin.jvm.optionals.getOrNull

object AkbRecordFilterUtil {
	fun filter(string: String?, filter: List<Pair<Regex, String>>): String? {
		return string?.let {
			filter.stream()
				.filter { it.first.containsMatchIn(string) }
				.map {
					val filtered = it.first.split(string).last()
					val searchIndex = filtered.indexOf(it.second)
					filtered.substring(0, searchIndex)
				}
				.findFirst().getOrNull()
		}
	}
}


