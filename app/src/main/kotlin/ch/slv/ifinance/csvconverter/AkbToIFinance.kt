package ch.slv.ifinance.csvconverter

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.help
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.boolean
import com.github.ajalt.clikt.parameters.types.file
import java.io.File
import java.io.StringReader

class AkbToIFinance : CliktCommand(help = "Convert <input> AKB CSV to <output> iFinance CSV.") {
	val verbose: Boolean by option("-v", "--verbose").boolean().default(false).help("Be more verbose")
	val input: File by argument("<input>").file(mustExist = true).help("Input file")
	val output: File by argument("<output>").file().help("Output file")

	val filterInput = """
			Warenbezug und Dienstleistungen ; AKB Debit;
			TWINT-Zahlung ;,;""".trimIndent()
	val akbFilter = StringReader(filterInput)
	override fun run() {
		CsvRecordConverter(input.reader(), akbFilter, output.writer(), verbose).convert()
	}
}

fun main(args: Array<String>) = AkbToIFinance().main(args)
