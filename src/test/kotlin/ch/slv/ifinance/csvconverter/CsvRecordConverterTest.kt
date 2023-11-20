package ch.slv.ifinance.csvconverter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.StringReader
import java.io.StringWriter

class CsvRecordConverterTest {
	@Test
	fun `output an empty file if the input is empty`() {
		val reader = StringReader("")
		val writer = StringWriter()
		CsvRecordConverter(reader, writer, false).convert()
		assertEquals("", writer.toString().trim())
	}

	@Test
	fun `give empty output if the records are unparsable`() {
		val reader = StringReader("some,bogus,input")
		val writer = StringWriter()
		CsvRecordConverter(reader, writer, false).convert()
		assertEquals("", writer.toString().trim())
	}

	@Test
	fun `give correctly escape any quotes in the CSV output`() {
		val reader = StringReader("\"0123456789\",\"EUR\",20110501,\"D\",348.14,\"987654321\",\"AH Kudelstaart\",20110502,\"ba\",\"\",\"some<,weird \"\"quote\"\"\",\"\",\"\",\"\",\"\",\"\"")
		val writer = StringWriter()
		CsvRecordConverter(reader, writer, false).convert()
		assertEquals("20110501,-348.14,987654321,AH Kudelstaart,\"some<,weird \"\"quote\"\"\"", writer.toString().trim())
	}

	@Test
	fun `output multiple records correctly`() {
		val input = """
            |"1","EUR",20110501,"D",348.14,"3","Transaction title1",20110502,"ba","","description1","","","","",""
            |"2","EUR",20110502,"C",300.00,"4","Transaction title2",20110503,"ba","","description2","","","","",""
            |"unparseable"
            |
            |above is empty""".trimMargin()
		val reader = StringReader(input)
		val writer = StringWriter()
		CsvRecordConverter(reader, writer, false).convert()
		assertEquals("""
                |20110501,-348.14,3,Transaction title1,description1
                |20110502,300.00,4,Transaction title2,description2
                |""".trimMargin().trim(), writer.toString().trim())
	}

	@Test
	fun `give a correct CSV if the input is parseable`() {
		val reader = StringReader("\"0123456789\",\"EUR\",20110501,\"D\",348.14,\"987654321\",\"AH Kudelstaart\",20110502,\"ba\",\"\",\"Pin-transactie...\",\"\",\"\",\"\",\"\",\"\"")
		val writer = StringWriter()
		CsvRecordConverter(reader, writer, false).convert()
		assertEquals("20110501,-348.14,987654321,AH Kudelstaart,Pin-transactie...", writer.toString().trim())
	}

	@Test
	fun `give concatenate the different 'description' fields`() {
		val reader = StringReader("\"0123456789\",\"EUR\",20110501,\"D\",348.14,\"987654321\",\"AH Kudelstaart\",20110502,\"ba\",\"\",\"desc1\",\"desc2\",\"desc3\",\"desc4\",\"\",\"\"")
		val writer = StringWriter()
		CsvRecordConverter(reader, writer, false).convert()
		assertEquals("""20110501,-348.14,987654321,AH Kudelstaart,"desc1
                |desc2
                |desc3
                |desc4"""".trimMargin(), writer.toString().trim())
	}

	@Test
	fun `give concatenate the different 'description' fields, but trim the newlines`() {
		val reader = StringReader("\"0123456789\",\"EUR\",20110501,\"D\",348.14,\"987654321\",\"AH Kudelstaart\",20110502,\"ba\",\"\",\"\",\"desc2\",\"\",\"\",\"\",\"\"")
		val writer = StringWriter()
		CsvRecordConverter(reader, writer, false).convert()
		assertEquals("20110501,-348.14,987654321,AH Kudelstaart,desc2", writer.toString().trim())
	}

	@Test
	fun `give concatenate the different 'description' fields, but enclosing newlines shouldn't be trimmed`() {
		val reader = StringReader("\"0123456789\",\"EUR\",20110501,\"D\",348.14,\"987654321\",\"AH Kudelstaart\",20110502,\"ba\",\"\",\"desc1\",\"\",\"\",\"desc4\",\"\",\"\"")
		val writer = StringWriter()
		CsvRecordConverter(reader, writer, false).convert()
		assertEquals("""20110501,-348.14,987654321,AH Kudelstaart,"desc1
                |
                |
                |desc4"""".trimMargin(), writer.toString().trim())
	}

	@Test
	fun `not output any quotes when outputting the empty string`() {
		val reader = StringReader("\"0123456789\",\"EUR\",20110501,\"D\",348.14,\"987654321\",\"AH Kudelstaart\",20110502,\"ba\",\"\",\"\",\"\",\"\",\"\",\"\",\"\"")
		val writer = StringWriter()
		CsvRecordConverter(reader, writer, false).convert()
		assertEquals("20110501,-348.14,987654321,AH Kudelstaart,", writer.toString().trim())
	}
}


