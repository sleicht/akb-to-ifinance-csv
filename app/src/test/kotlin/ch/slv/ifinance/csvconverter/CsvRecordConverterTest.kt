package ch.slv.ifinance.csvconverter

import net.datafaker.Faker
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.StringReader
import java.io.StringWriter
import java.util.*

class CsvRecordConverterTest {

	@Test
	fun `output an empty file if the input is empty`() {
		val reader = StringReader("")
		val filter = StringReader("TWINT-Zahlung ;,")
		val writer = StringWriter()
		CsvRecordConverter(reader, filter, writer, false).convert()
		assertEquals("", writer.toString().trim())
	}

	@Test
	fun `give empty output if the records are unparsable`() {
		val reader = StringReader("some,bogus,input")
		val filter = StringReader("TWINT-Zahlung ;,")
		val writer = StringWriter()
		CsvRecordConverter(reader, filter, writer, false).convert()
		assertEquals("", writer.toString().trim())
	}

	@Test
	fun `give correctly escape any quotes in the CSV output`() {
		val reader = StringReader("20.11.2023;19.11.2023;some<,weird \"\"\"quote\"\"\";22.90;;1'222.00;")
		val filter = StringReader("TWINT-Zahlung ;,")
		val writer = StringWriter()
		CsvRecordConverter(reader, filter, writer, false).convert()
		assertEquals("20231119,-22.90,,,\"some<,weird \"\"quote\"\"\"", writer.toString().trim())
	}

	@Test
	fun `output multiple records correctly`() {
		val input = """
			|Buchung;Valuta;Buchungstext;Belastung;Gutschrift;Saldo CHF;
			|20.11.2023;19.11.2023;TWINT-Zahlung FIRMA AG, ZURICH +41781231212 19.11.2023 12:11;22.90;;1'222.00;
			|20.11.2023;19.11.2023;TWINT-Zahlung DARPA AG, ZURICH +41781231212 19.11.2023 12:11;;22.90;1'222.00;
			|17.11.2023;15.11.2023;Warenbezug und Dienstleistungen FIRMA AG AKB Debit Mastercard Kartennummer 1234 4321 0987 7890 15.11.2023 08:21;4.00;;1'000.01;
            |"unparseable"
            |
            |above is empty""".trimMargin()
		val reader = StringReader(input)
		val writer = StringWriter()
		CsvRecordConverter(reader, null, writer, false).convert()
		assertEquals(
			"""
			|
				|20231119,-22.90,FIRMA AG,,"TWINT-Zahlung FIRMA AG, ZURICH +41781231212 19.11.2023 12:11"
				|20231119,22.90,DARPA AG,,"TWINT-Zahlung DARPA AG, ZURICH +41781231212 19.11.2023 12:11"
				|20231115,-4.00,FIRMA AG,,Warenbezug und Dienstleistungen FIRMA AG AKB Debit Mastercard Kartennummer 1234 4321 0987 7890 15.11.2023 08:21
                |""".trimMargin().trim(), writer.toString().trim()
		)
	}

	@Test
	fun `output multiple ingoing and ebanking records correctly`() {
		val faker = Faker(Locale.of("de", "CH"))

		val creditor1 = "${faker.company().name()} ${faker.address().fullAddress()}"
		val creditor2 = "${faker.company().name()} ${faker.address().fullAddress()}"
		val debitor = "${faker.name().name()} ${faker.address().fullAddress()}".uppercase()
		val input = """
			|Buchung;Valuta;Buchungstext;Belastung;Gutschrift;Saldo CHF;
			|24.11.2023;24.11.2023;" Zahlungseingang / Ref.-Nr. 1234567890 $creditor1 Referenz: Lohnabrech. 11.2023 von 24.11.2023 ";;1'600.20;12'165.66;
			|24.11.2023;24.11.2023;" Belastung e-banking / Ref.-Nr. 1234567890 $debitor Mitteilung: FRANKLY A ";588.00;;10'456.46;
			|24.11.2023;24.11.2023;" Zahlungseingang / Ref.-Nr. 1234567890 $creditor2 Referenz: Lohnzahlung ";;10'000.00';10'000.00;
			|
            |above is empty""".trimMargin()
		val reader = StringReader(input)
		val writer = StringWriter()
		CsvRecordConverter(reader, null, writer, false).convert()
		assertEquals(
			"""
			|
				|20231124,1600.20,"$creditor1",Lohnabrech. 11.2023 von 24.11.2023,"Zahlungseingang / Ref.-Nr. 1234567890 $creditor1 Referenz: Lohnabrech. 11.2023 von 24.11.2023"
				|20231124,-588.00,"$debitor",FRANKLY A,"Belastung e-banking / Ref.-Nr. 1234567890 $debitor Mitteilung: FRANKLY A"
				|20231124,10000.00,"$creditor2",Lohnzahlung,"Zahlungseingang / Ref.-Nr. 1234567890 $creditor2 Referenz: Lohnzahlung"
                |""".trimMargin().trim(), writer.toString().trim()
		)
	}

	@Test
	fun `give a correct CSV of generic card payment if the input is parseable`() {
		val reader =
			StringReader("17.11.2023;15.11.2023;Warenbezug und Dienstleistungen FIRMA AG AKB Debit Mastercard Kartennummer 1234 4321 0987 7890 15.11.2023 08:21;4.00;;1'000.01;")
		val filter = StringReader("Warenbezug und Dienstleistungen ; AKB Debit")
		val writer = StringWriter()
		CsvRecordConverter(reader, filter, writer, false).convert()
		assertEquals(
			"20231115,-4.00,FIRMA AG,,Warenbezug und Dienstleistungen FIRMA AG AKB Debit Mastercard Kartennummer 1234 4321 0987 7890 15.11.2023 08:21",
			writer.toString().trim()
		)
	}

	@Test
	fun `give a correct CSV of twint payment if the input is parseable`() {
		val reader =
			StringReader("20.11.2023;19.11.2023;TWINT-Zahlung FIRMA AG, ZURICH +41781231212 19.11.2023 12:11;22.90;;1'222.00;")
		val filter = StringReader("TWINT-Zahlung ([\\w ]+);(TWINT-Zahlung [\\w ]+);")
		val writer = StringWriter()
		CsvRecordConverter(reader, filter, writer, false).convert()
		assertEquals(
			"20231119,-22.90,FIRMA AG,TWINT-Zahlung FIRMA AG,\"TWINT-Zahlung FIRMA AG, ZURICH +41781231212 19.11.2023 12:11\"",
			writer.toString().trim()
		)
	}

	@Test
	fun `not output any quotes when outputting the empty string`() {
		val reader = StringReader("20.11.2023;19.11.2023;;22.90;;1'222.00;")
		val filter = StringReader("TWINT-Zahlung ;,")
		val writer = StringWriter()
		CsvRecordConverter(reader, filter, writer, false).convert()
		assertEquals("20231119,-22.90,,,", writer.toString().trim())
	}
}


