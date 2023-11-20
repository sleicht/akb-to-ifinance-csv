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

class App : CliktCommand(help = "Convert <input> AKB CSV to <output> iFinance CSV.") {
	val verbose: Boolean by option("-v", "--verbose").boolean().default(false).help("Be more verbose")
	val input: File by argument("<input>").file(mustExist = true).help("Input file")
	val output: File by argument("<output>").file().help("Output file")

	override fun run() {
		CsvRecordConverter(input.reader(), output.writer(), verbose).convert()
	}
}

fun main(args: Array<String>) = App().main(args)
