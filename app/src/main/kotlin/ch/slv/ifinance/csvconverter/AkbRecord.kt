package ch.slv.ifinance.csvconverter

import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.math.BigDecimal.ZERO
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class AkbRecord(
	val transactionDate: LocalDate,
	val interestDate: LocalDate,
	val description: String?,
	val beneficiary: String?,
	val debit: BigDecimal,
	val credit: BigDecimal,
	val balance: BigDecimal
)

object AkbRecordUtil {
	private val log = LoggerFactory.getLogger(javaClass)

	fun fromCsvRecord(data: List<String>, filter: List<Pair<Regex, String>>): AkbRecord? {
		return try {
			val debit = data.getOrNull(3);
			val credit = data.getOrNull(4);
			val entry = AkbRecord(
				transactionDate = LocalDate.parse(data[0], DateTimeFormatter.ofPattern("dd.MM.yyyy")),
				interestDate = LocalDate.parse(data[1], DateTimeFormatter.ofPattern("dd.MM.yyyy")),
				description = data[2],
				beneficiary = AkbRecordFilterUtil.filter(data[2], filter),
				debit = if (debit == null) ZERO else BigDecimal(debit),
				credit = if (credit == null) ZERO else BigDecimal(credit),
				balance = BigDecimal(data[5].replace("'", ""))
			)
			entry
		} catch (e: Exception) {
			log.warn("Unable to parse data into a AkbRecord: " + data.toList(), e)
			null
		}
	}
}


