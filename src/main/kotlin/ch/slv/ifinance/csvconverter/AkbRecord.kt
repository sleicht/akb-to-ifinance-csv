package ch.slv.ifinance.csvconverter

import org.slf4j.LoggerFactory
import java.math.BigDecimal

data class AkbRecord(
	val accountNumber: String,
	val currency: String,
	val interestDate: String,
	val sign: String,
	val amount: BigDecimal,
	val targetAccountNumber: String,
	val targetTitle: String,
	val transactionDate: String,
	val code: String,
	val other1: String?, // Unsure what this field is for
	val description1: String?,
	val description2: String?,
	val description3: String?,
	val description4: String?,
	val other2: String?, // Unsure what this field is for
	val other3: String? // Unsure what this field is for
)

object AkbRecordUtil {
	private val log = LoggerFactory.getLogger(javaClass)

	fun fromCsvRecord(data: List<String>): AkbRecord? {
		return try {
			val entry = AkbRecord(
				accountNumber = data[0],
				currency = data[1],
				interestDate = data[2],
				sign = data[3],
				amount = BigDecimal(data[4]),
				targetAccountNumber = data[5],
				targetTitle = data[6],
				transactionDate = data[7],
				code = data[8],
				other1 = data.getOrNull(9),
				description1 = data.getOrNull(10),
				description2 = data.getOrNull(11),
				description3 = data.getOrNull(12),
				description4 = data.getOrNull(13),
				other2 = data.getOrNull(14),
				other3 = data.getOrNull(15)
			)
			entry
		} catch (e: Exception) {
			log.warn("Unable to parse data into a AkbEntry: " + data.toList(), e)
			null
		}
	}
}


