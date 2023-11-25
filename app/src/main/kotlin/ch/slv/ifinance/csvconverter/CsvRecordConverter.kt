package ch.slv.ifinance.csvconverter

import org.slf4j.LoggerFactory
import org.supercsv.io.CsvListReader
import org.supercsv.io.CsvListWriter
import org.supercsv.prefs.CsvPreference
import org.supercsv.prefs.CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE
import java.io.Reader
import java.io.StringReader
import java.io.Writer
import java.math.BigDecimal
import java.time.format.DateTimeFormatter

class CsvRecordConverter(
	private val akbReader: Reader,
	private var filterReader: Reader?,
	private val iFinanceWriter: Writer,
	private val verbose: Boolean
) {
	private val log = LoggerFactory.getLogger(javaClass)
	private val writerCsvPreferences = CsvPreference.Builder('"', ','.code, System.lineSeparator()).build()
	private val filterInput = """
			Warenbezug und Dienstleistungen ([\S ]+) AKB Debit;(Warenbezug und Dienstleistungen [\S ]+) AKB Debit;
			TWINT-Zahlung ([\S ]+),;(TWINT-Zahlung [\S ]+),;;
			" Zahlungseingang \/ Ref\.-Nr\. \d+ ([\S ]+) Referenz:"; Referenz: ([\S ]+) ;
			" Belastung e-banking / Ref.-Nr. \d+ ([\S ]+) Mitteilung: ";Mitteilung: ([\S ]+) ;
			" Belastung e-banking \/ Ref.-Nr. \d+ ([\S ]+) Ursprünglicher"; (Belastung e-banking \/ Ref.-Nr. \d+ [\S ]+) Ursprünglicher;
			" Übertrag von ([\S]+)";Ref.-Nr. \d+ ([\S ]+) ;
			""".trimIndent()

	fun convert() {
		if (filterReader == null) {
			filterReader = StringReader(filterInput)
		}

		log.debug("Starting conversion.")
		val akbCsvReader = CsvListReader(akbReader, EXCEL_NORTH_EUROPE_PREFERENCE)
		val filterCsvReader = CsvListReader(filterReader, EXCEL_NORTH_EUROPE_PREFERENCE)
		val iFinanceCsvWriter = CsvListWriter(iFinanceWriter, writerCsvPreferences)
		val akbEntries = readCsvToList(akbCsvReader)
		log.debug("Read {} AKB CSV entries.", akbEntries.size)
		val filterEntries = readCsvToPair(filterCsvReader)
		log.debug("Read {} filter CSV entries.", filterEntries.size)
		val akbRecords = akbEntries.mapNotNull { AkbRecordUtil.fromCsvRecord(it, filterEntries) }
		val iFinanceRecords = akbRecords.map { toIFinanceRecord(it) }
		val outEntries = iFinanceRecords.map { IFinanceRecordUtil.toCsvRecord(it) }
		log.debug("Writing {} iFinance entries.", outEntries.size)
		writeCsv(iFinanceCsvWriter, outEntries)
		log.debug("Completed conversion.")
	}

	private fun readCsvToList(csvReader: CsvListReader): List<List<String>> {
		fun readLine(): List<String>? = csvReader.read()?.toList()
		return generateSequence { readLine() }.toList()
	}

	private fun readCsvToPair(csvReader: CsvListReader): List<AkbRecordFilter> {
		fun readLine(): List<String>? = csvReader.read()?.toList()
		return generateSequence {
			val line = readLine()
			line?.let { AkbRecordFilter(it[0].toRegex(), it[1].toRegex()) }
		}.toList()
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
		val signedAmount = if (akbRecord.debit > BigDecimal.ZERO)
			akbRecord.debit.negate() else
			akbRecord.credit

		return IFinanceRecord(
			date = akbRecord.interestDate.format(DateTimeFormatter.BASIC_ISO_DATE),
			amount = signedAmount,
			beneficiary = akbRecord.beneficiary.orEmpty(),
			title = akbRecord.description.orEmpty().trim(),
			description = akbRecord.comment.orEmpty().trim()
		)
	}
}


