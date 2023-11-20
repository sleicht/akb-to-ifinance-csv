package ch.slv.ifinance.csvconverter

import org.slf4j.LoggerFactory
import kotlin.jvm.optionals.getOrNull

data class AkbRecordFilter(
	val filterExp: String,
	val searchString: String
)

object AkbRecordFilterUtil {
	private val log = LoggerFactory.getLogger(javaClass)

	fun fromCsvRecord(data: List<String>): AkbRecordFilter? {
		return try {
			val entry = AkbRecordFilter(
				filterExp = data[0],
				searchString = data[1]
			)
			entry
		} catch (e: Exception) {
			log.warn("Unable to parse data into a AkbRecordFilter: " + data.toList(), e)
			null
		}
	}

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


