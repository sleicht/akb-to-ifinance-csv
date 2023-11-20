package ch.slv.ifinance.csvconverter

import java.math.BigDecimal

data class IFinanceRecord(
	val date: String,
	val amount: BigDecimal,
	val beneficiary: String,
	val title: String,
	val description: String
)

object IFinanceRecordUtil {
	fun toCsvRecord(iFinanceRecord: IFinanceRecord): List<String> {
		return listOf(
			iFinanceRecord.date,
			iFinanceRecord.amount.toString(),
			iFinanceRecord.beneficiary,
			iFinanceRecord.title,
			iFinanceRecord.description
		)
	}
}


