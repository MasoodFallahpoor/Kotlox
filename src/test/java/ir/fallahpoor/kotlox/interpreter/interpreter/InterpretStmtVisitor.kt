package ir.fallahpoor.kotlox.interpreter.interpreter

import ir.fallahpoor.kotlox.interpreter.Environment
import ir.fallahpoor.kotlox.interpreter.Printer
import ir.fallahpoor.kotlox.interpreter.antlr.LoxBaseVisitor
import ir.fallahpoor.kotlox.interpreter.antlr.LoxParser

class InterpretStmtVisitor(
    private val interpretExprVisitor: InterpretExprVisitor,
    private var environment: Environment,
    private val printer: Printer
) : LoxBaseVisitor<Unit>() {

    // Evaluates rule: program -> declaration* EOF
    override fun visitProgram(ctx: LoxParser.ProgramContext) {
        if (ctx.declaration().isNotEmpty()) {
            for (i in 0..ctx.declaration().lastIndex) {
                visitDeclaration(ctx.declaration(i))
            }
        }
    }

    // Evaluates rule: declaration -> varDecl | statement
    override fun visitDeclaration(ctx: LoxParser.DeclarationContext) {
        if (ctx.varDecl() != null) {
            visitVarDecl(ctx.varDecl())
        } else if (ctx.statement() != null) {
            visitStatement(ctx.statement())
        } else {
            throw RuntimeException()
        }
    }

    // Evaluates rule: varDecl -> "var" IDENTIFIER ("=" expression)? ";"
    override fun visitVarDecl(ctx: LoxParser.VarDeclContext) {
        val value: Any? = if (ctx.expression() != null) {
            interpretExprVisitor.visitExpression(ctx.expression())
        } else {
            null
        }
        environment.define(ctx.IDENTIFIER().symbol.text, value)
    }

    // Evaluates rule: statement -> exprStmt | printStmt | block
    override fun visitStatement(ctx: LoxParser.StatementContext) {
        if (ctx.exprStmt() != null) {
            visitExprStmt(ctx.exprStmt())
        } else if (ctx.printStmt() != null) {
            visitPrintStmt(ctx.printStmt())
        } else if (ctx.block() != null) {
            visitBlock(ctx.block())
        } else
            throw RuntimeException()
    }

    // Evaluates rule: printStmt -> "print" expression ";"
    override fun visitPrintStmt(ctx: LoxParser.PrintStmtContext) {
        val value: Any? = interpretExprVisitor.visitExpression(ctx.expression())
        printer.println(stringify(value))
    }

    // Evaluates rule: exprStmt -> expression ";"
    override fun visitExprStmt(ctx: LoxParser.ExprStmtContext) {
        interpretExprVisitor.visitExpression(ctx.expression())
    }

    // Evaluates rule: block -> "{" declaration* "}"
    override fun visitBlock(ctx: LoxParser.BlockContext) {
        if (ctx.declaration().isNullOrEmpty()) {
            return
        }

        val newEnvironment = Environment(environment)
        val previousEnvironment = environment
        try {
            interpretExprVisitor.environment = newEnvironment
            environment = newEnvironment
            ctx.declaration().forEach {
                visitDeclaration(it)
            }
        } finally {
            interpretExprVisitor.environment = previousEnvironment
            environment = previousEnvironment
        }
    }

    private fun stringify(any: Any?): String =
        when (any) {
            null -> "nil"
            is Double -> {
                var text = any.toString()
                if (text.endsWith(".0")) {
                    text = text.substring(0, text.length - 2)
                }
                text
            }
            else -> any.toString()
        }

}