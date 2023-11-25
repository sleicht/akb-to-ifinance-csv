package ch.slv.ifinance.csvconverter

import org.slf4j.LoggerFactory
import kotlin.jvm.optionals.getOrNull

data class AkbRecordFilter(
	val beneficiaryRegex: Regex,
	val descriptionRegex: Regex,
)

data class AkbRecordFiltereData(
	val beneficiary: String?,
	val description: String?,
)

object AkbRecordFilterUtil {
	private val log = LoggerFactory.getLogger(javaClass)
	fun filter(string: String?, filter: List<AkbRecordFilter>): AkbRecordFiltereData? {
		return string?.let {
			filter.stream()
				.filter { it.beneficiaryRegex.containsMatchIn(string) }
				.map {
					val (beneficiary) = it.beneficiaryRegex.find(string)!!.destructured
					val (description) = it.descriptionRegex.find(string)!!.destructured

					AkbRecordFiltereData(beneficiary, description)
				}
				.findFirst().getOrNull()
		}
	}
}


