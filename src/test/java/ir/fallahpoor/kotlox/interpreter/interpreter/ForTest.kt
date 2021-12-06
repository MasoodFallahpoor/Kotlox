package ir.fallahpoor.kotlox.interpreter.interpreter

import com.google.common.truth.Truth
import ir.fallahpoor.kotlox.interpreter.ErrorReporter
import ir.fallahpoor.kotlox.interpreter.Stmt
import ir.fallahpoor.kotlox.interpreter.TestPrinter
import ir.fallahpoor.kotlox.interpreter.parser.Parser
import ir.fallahpoor.kotlox.interpreter.parser.Tokens
import ir.fallahpoor.kotlox.interpreter.scanner.Scanner
import ir.fallahpoor.kotlox.interpreter.scanner.Token
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

// TODO add tests for cases when Interpreter catches a runtime error.

@RunWith(MockitoJUnitRunner::class)
class ForTest {

    @Mock
    private lateinit var errorReporter: ErrorReporter

    @Test
    fun testSingleExpressionBody() {

        // Given
        val source =
            """for (var c = 0; c < 3;) print c = c + 1;
             """

        // When
        val actualPrinter = TestPrinter()
        interpretSource(source, actualPrinter)

        // Then
        val expectedOutput = listOf("1", "2", "3")
        Truth.assertThat(actualPrinter.output).isEqualTo(expectedOutput)
        Mockito.verifyNoInteractions(errorReporter)
    }

    @Test
    fun testBlockBody() {

        // Given
        val source =
            """for (var a = 0; a < 3; a = a + 1) {
                   print a;
               }
             """

        // When
        val actualPrinter = TestPrinter()
        interpretSource(source, actualPrinter)

        // Then
        val expectedOutput = listOf("0", "1", "2")
        Truth.assertThat(actualPrinter.output).isEqualTo(expectedOutput)
        Mockito.verifyNoInteractions(errorReporter)
    }

    // TODO uncomment the following line when support for "functions" is added
//    @Test
//    fun testNoClauses() {
//
//        // Given
//        val source =
//            """fun foo() {
//                   for (;;) return "done";
//               }
//               print foo();
//             """
//
//        // When
//        val actualPrinter = TestPrinter()
//        interpretSource(source, actualPrinter)
//
//        // Then
//        val expectedOutput = listOf("done")
//        Truth.assertThat(actualPrinter.output).isEqualTo(expectedOutput)
//        Mockito.verifyNoInteractions(errorReporter)
//    }

    @Test
    fun testNoInitializer() {

        // Given
        val source =
            """var i = 0;
               for (; i < 2; i = i + 1) print i;
             """

        // When
        val actualPrinter = TestPrinter()
        interpretSource(source, actualPrinter)

        // Then
        val expectedOutput = listOf("0", "1")
        Truth.assertThat(actualPrinter.output).isEqualTo(expectedOutput)
        Mockito.verifyNoInteractions(errorReporter)
    }

    // TODO uncomment the following line when support for "functions" is added
//    @Test
//    fun testNoCondition() {
//
//        // Given
//        val source =
//            """fun bar() {
//                   for (var i = 0;; i = i + 1) {
//                       print i;
//                       if (i >= 2) return;
//                    }
//               }
//               bar();
//             """
//
//        // When
//        val actualPrinter = TestPrinter()
//        interpretSource(source, actualPrinter)
//
//        // Then
//        val expectedOutput = listOf("0", "1", "2")
//        Truth.assertThat(actualPrinter.output).isEqualTo(expectedOutput)
//        Mockito.verifyNoInteractions(errorReporter)
//    }

    @Test
    fun testNoIncrement() {

        // Given
        val source =
            """for (var i = 0; i < 2;) {
                   print i;
                   i = i + 1;
               }
             """

        // When
        val actualPrinter = TestPrinter()
        interpretSource(source, actualPrinter)

        // Then
        val expectedOutput = listOf("0", "1")
        Truth.assertThat(actualPrinter.output).isEqualTo(expectedOutput)
        Mockito.verifyNoInteractions(errorReporter)

    }

    private fun interpretSource(source: String, printer: TestPrinter) {
        val statements: List<Stmt> = parseSource(source)
        if (statements.isNotEmpty()) {
            val interpreter = Interpreter(errorReporter, printer)
            interpreter.interpret(statements)
        }
    }

    private fun parseSource(source: String): List<Stmt> {
        val scanner = Scanner(source.trimIndent(), errorReporter)
        val tokens: List<Token> = scanner.scanTokens()
        val parser = Parser(Tokens(tokens), errorReporter)
        return parser.parse()
    }

}