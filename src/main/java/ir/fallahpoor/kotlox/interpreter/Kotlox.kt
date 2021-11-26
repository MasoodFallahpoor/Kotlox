package ir.fallahpoor.kotlox.interpreter

import ir.fallahpoor.kotlox.interpreter.parser.Parser
import ir.fallahpoor.kotlox.interpreter.parser.Tokens
import ir.fallahpoor.kotlox.interpreter.scanner.Scanner
import ir.fallahpoor.kotlox.interpreter.scanner.Token
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess

class Lox(private val commandLineArgs: Array<String>) {

    private val errorReporter = ErrorReporter()

    private object ErrorCode {
        // Lox is used with an incorrect number of arguments
        const val WRONG_USAGE = 64

        // There is a syntax error in some way.
        const val WRONG_SYNTAX = 65
    }

    fun run() {
        when (commandLineArgs.size) {
            0 -> runPrompt()
            1 -> runFile(commandLineArgs[0])
            else -> {
                println("Usage: kotlox [script]")
                exitProcess(ErrorCode.WRONG_USAGE)
            }
        }
    }

    @Throws(IOException::class)
    private fun runPrompt() {
        val input = InputStreamReader(System.`in`)
        val reader = BufferedReader(input)
        while (true) {
            print("> ")
            // The only case that readLine returns null is when user presses Ctrl+C to exit the interactive mode.
            // That's the time to break out of the loop.
            val line = reader.readLine() ?: break
            run(line)
            errorReporter.hadError = false
        }
    }

    @Throws(IOException::class)
    private fun runFile(path: String) {
        val source = String(Files.readAllBytes(Paths.get(path)))
        run(source)
        if (errorReporter.hadError) {
            exitProcess(ErrorCode.WRONG_SYNTAX)
        }
    }

    private fun run(source: String) {
        val scanner = Scanner(source, errorReporter)
        val tokens: List<Token> = scanner.scanTokens()
        val parser = Parser(Tokens(tokens), errorReporter)
        val expression: Expr? = parser.parse()
        if (expression != null) {
            println(AstPrinter().print(expression))
        } else {
            // Stop if there was a syntax error.
            if (errorReporter.hadError) {
                return
            }
        }
    }

}

fun main(args: Array<String>) {
    Lox(args).run()
}