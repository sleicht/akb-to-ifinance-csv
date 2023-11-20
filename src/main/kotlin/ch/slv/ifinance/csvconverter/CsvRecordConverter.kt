package ch.slv.ifinance.csvconverter

import java.io.Reader
import java.io.Writer
import org.supercsv.io.CsvListReader
import org.supercsv.prefs.CsvPreference
import org.slf4j.LoggerFactory
import org.supercsv.io.CsvListWriter
import org.supercsv.prefs.CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE
import org.supercsv.prefs.CsvPreference.STANDARD_PREFERENCE

class CsvRecordConverter(private val reader: Reader, private val writer: Writer, private val verbose: Boolean) {
	private val log = LoggerFactory.getLogger(javaClass)
	private val writerCsvPreferences = CsvPreference.Builder('"', ','.code, System.lineSeparator()).build()
	private val csvPref = EXCEL_NORTH_EUROPE_PREFERENCE

	fun convert() {
		log.debug("Starting conversion.")
		val csvReader = CsvListReader(reader, STANDARD_PREFERENCE)
		val csvWriter = CsvListWriter(writer, writerCsvPreferences)
		val inEntries = readCsv(csvReader)
		log.debug("Read {} Akb CSV entries.", inEntries.size)
		val akbRecords = inEntries.mapNotNull { AkbRecordUtil.fromCsvRecord(it) }
		val iFinanceRecords = akbRecords.map { toIFinanceRecord(it) }
		val outEntries = iFinanceRecords.map { IFinanceRecordUtil.toCsvRecord(it) }
		log.debug("Writing {} iFinance entries.", outEntries.size)
		writeCsv(csvWriter, outEntries)
		log.debug("Completed conversion.")
	}

	private fun readCsv(csvReader: CsvListReader): List<List<String>> {
		fun readLine(): List<String>? = csvReader.read()?.toList()
		return generateSequence { readLine() }.toList()
	}

	private fun writeCsv(csvWriter: CsvListWriter, entries: List<List<String>>) {
		try {
			for (entry in entries) {
				csvWriter.write(entry.toMutableList())
			}
		} finally {
			try {
				csvWriter.close()
			} catch (e: Exception) {
				log.warn("Unable to close the CSV writer.", e)
			}
		}
	}

	private fun toIFinanceRecord(akbRecord: AkbRecord): IFinanceRecord {
		val signedAmount = when (akbRecord.sign) {
			"D" -> akbRecord.amount.negate()
			else -> akbRecord.amount
		}
		return IFinanceRecord(
			date = akbRecord.interestDate,
			amount = signedAmount,
			beneficiary = akbRecord.targetAccountNumber,
			title = akbRecord.targetTitle,
			description = """
${akbRecord.description1.orEmpty()}
${akbRecord.description2.orEmpty()}
${akbRecord.description3.orEmpty()}
${akbRecord.description4.orEmpty()}
            """.trim()
		)
	}
}


