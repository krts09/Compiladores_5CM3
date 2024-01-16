from typing import List
from Tokentype import TokenType
from Token import Token


class Parser:
    def __init__(self, tokens: List[Token]):
        self.tokens = tokens
        self.errors = False
        self.preanalysis = None
        self.i = 0

    def parse(self):
        from Interprete import Interprete

        self.preanalysis = self.tokens[self.i]
        self.PROGRAM()

        if not self.errors and not self.preanalysis.type == TokenType.EOF:
            msg = f"Token inesperado: {self.preanalysis.type}"
            Interprete.error(self.preanalysis.line, msg)
        elif not self.errors and self.preanalysis.type == TokenType.EOF:
            #print("Expresion valida")
            pass

    def PROGRAM(self):
        if (
            self.preanalysis.type == TokenType.CLASS
            or self.preanalysis.type == TokenType.FUN
            or self.preanalysis.type == TokenType.VAR
            or self.preanalysis.type == TokenType.NEGATION
            or self.preanalysis.type == TokenType.LESS
            or self.preanalysis.type == TokenType.TRUE
            or self.preanalysis.type == TokenType.FALSE
            or self.preanalysis.type == TokenType.NULL
            or self.preanalysis.type == TokenType.THIS
            or self.preanalysis.type == TokenType.NUMBER
            or self.preanalysis.type == TokenType.STRING
            or self.preanalysis.type == TokenType.IDENTIFIER
            or self.preanalysis.type == TokenType.PARENT_OPEN
            or self.preanalysis.type == TokenType.SUPER
            or self.preanalysis.type == TokenType.FOR
            or self.preanalysis.type == TokenType.IF
            or self.preanalysis.type == TokenType.PRINT
            or self.preanalysis.type == TokenType.RETURN
            or self.preanalysis.type == TokenType.WHILE
            or self.preanalysis.type == TokenType.BRACKET_OPEN
            # or self.preanalysis.type == TokenType.GREAT
        ):
            self.DECLARATION()

    def DECLARATION(self):
        if self.errors:
            return
        if self.preanalysis.type == TokenType.CLASS:
            self.CLASS_DECL()
            self.DECLARATION()
        elif self.preanalysis.type == TokenType.FUN:
            self.FUN_DECL()
            self.DECLARATION()
        elif self.preanalysis.type == TokenType.VAR:
            self.VAR_DECL()
            self.DECLARATION()
        elif (
            self.preanalysis.type == TokenType.NEGATION
            or self.preanalysis.type == TokenType.LESS
            or self.preanalysis.type == TokenType.TRUE
            or self.preanalysis.type == TokenType.FALSE
            or self.preanalysis.type == TokenType.NULL
            or self.preanalysis.type == TokenType.THIS
            or self.preanalysis.type == TokenType.NUMBER
            or self.preanalysis.type == TokenType.STRING
            or self.preanalysis.type == TokenType.IDENTIFIER
            or self.preanalysis.type == TokenType.PARENT_OPEN
            or self.preanalysis.type == TokenType.SUPER
            or self.preanalysis.type == TokenType.FOR
            or self.preanalysis.type == TokenType.IF
            or self.preanalysis.type == TokenType.PRINT
            or self.preanalysis.type == TokenType.RETURN
            or self.preanalysis.type == TokenType.WHILE
            or self.preanalysis.type == TokenType.BRACKET_OPEN
            or self.preanalysis.type == TokenType.GREAT
        ):
            self.STATEMENT()
            self.DECLARATION()

    def CLASS_DECL(self):
        from Interprete import Interprete

        if self.errors:
            return
        if self.preanalysis.type == TokenType.CLASS:
            self.matchToken(TokenType.CLASS)
            self.matchToken(TokenType.IDENTIFIER)
            self.CLASS_INHER()
            self.matchToken(TokenType.BRACKET_OPEN)
            self.FUNCTIONS()
            self.matchToken(TokenType.BRACKET_CLOSE)
        else:
            self.errors = True
            msg = f"No se esperaba el token: {self.preanalysis.type}"
            Interprete.error(self.preanalysis.line, msg)

    def CLASS_INHER(self):
        from Interprete import Interprete

        if self.errors:
            return
        if self.preanalysis.type == TokenType.LESS_THAN:
            self.matchToken(TokenType.LESS_THAN)
            self.matchToken(TokenType.IDENTIFIER)

    def FUN_DECL(self):
        from Interprete import Interprete

        if self.errors:
            return
        if self.preanalysis.type == TokenType.FUN:
            self.matchToken(TokenType.FUN)
            self.FUNCTION()
        else:
            self.errors = True
            msg = f"No se esperaba el token: {self.preanalysis.type}"
            Interprete.error(self.preanalysis.line, msg)

    def VAR_DECL(self):
        from Interprete import Interprete

        if self.errors:
            return
        if self.preanalysis.type == TokenType.VAR:
            self.matchToken(TokenType.VAR)
            self.matchToken(TokenType.IDENTIFIER)
            self.VAR_INIT()
            self.matchToken(TokenType.SEMICOLON)
            self.jump_op()
        else:
            self.errors = True
            msg = f"No se esperaba el token: {self.preanalysis.type}"
            Interprete.error(self.preanalysis.line, msg)

    def VAR_INIT(self):
        from Interprete import Interprete

        if self.errors:
            return
        if self.preanalysis.type == TokenType.ASIGNATION:
            self.matchToken(TokenType.ASIGNATION)
            self.EXPRESSION()

    def STATEMENT(self):
        from Interprete import Interprete

        if self.errors:
            return
        if self.preanalysis.type in [
            TokenType.NEGATION,
            TokenType.LESS,
            TokenType.GREAT,
            TokenType.TRUE,
            TokenType.FALSE,
            TokenType.NULL,
            TokenType.THIS,
            TokenType.NUMBER,
            TokenType.STRING,
            TokenType.IDENTIFIER,
            TokenType.PARENT_OPEN,
            TokenType.SUPER,
        ]:
            self.EXPR_STMT()
        elif self.preanalysis.type == TokenType.FOR:
            self.FOR_STMT()
        elif self.preanalysis.type == TokenType.IF:
            self.IF_STMT()
        elif self.preanalysis.type == TokenType.PRINT:
            self.PRINT_STMT()
        elif self.preanalysis.type == TokenType.RETURN:
            self.RETURN_STMT()
        elif self.preanalysis.type == TokenType.WHILE:
            self.WHILE_STMT()
        elif self.preanalysis.type == TokenType.BRACKET_OPEN:
            self.BLOCK()
        else:
            self.errors = True
            msg = f"No se esperaba el token: {self.preanalysis.type}"
            Interprete.error(self.preanalysis.line, msg)

    def EXPR_STMT(self):
        from Interprete import Interprete

        if self.errors:
            return
        if self.preanalysis.type in [
            TokenType.NEGATION,
            TokenType.LESS,
            TokenType.GREAT,
            TokenType.TRUE,
            TokenType.FALSE,
            TokenType.NULL,
            TokenType.THIS,
            TokenType.NUMBER,
            TokenType.STRING,
            TokenType.IDENTIFIER,
            TokenType.PARENT_OPEN,
            TokenType.SUPER,
        ]:
            self.EXPRESSION()
            self.matchToken(TokenType.SEMICOLON)
        else:
            self.errors = True
            msg = f"No se esperaba el token: {self.preanalysis.type}"
            Interprete.error(self.preanalysis.line, msg)

    def FOR_STMT(self):
        from Interprete import Interprete

        if self.errors:
            return
        if self.preanalysis.type == TokenType.FOR:
            self.matchToken(TokenType.FOR)
            self.matchToken(TokenType.PARENT_OPEN)
            self.FOR_STMT_1()
            self.FOR_STMT_2()
            self.FOR_STMT_3()
            self.matchToken(TokenType.PARENT_CLOSE)
            self.STATEMENT()
        else:
            self.errors = True
            msg = f"No se esperaba el token: {self.preanalysis.type}"
            Interprete.error(self.preanalysis.line, msg)

    def FOR_STMT_1(self):
        from Interprete import Interprete

        if self.errors:
            return
        if self.preanalysis.type == TokenType.VAR:
            self.VAR_DECL()
        elif self.preanalysis.type in [
            TokenType.NEGATION,
            TokenType.LESS,
            TokenType.GREAT,
            TokenType.TRUE,
            TokenType.FALSE,
            TokenType.NULL,
            TokenType.THIS,
            TokenType.NUMBER,
            TokenType.STRING,
            TokenType.IDENTIFIER,
            TokenType.PARENT_OPEN,
            TokenType.SUPER,
        ]:
            self.EXPR_STMT()
        elif self.preanalysis.type == TokenType.SEMICOLON:
            self.matchToken(TokenType.SEMICOLON)
        else:
            self.errors = True
            msg = f"No se esperaba el token: {self.preanalysis.type}"
            Interprete.error(self.preanalysis.line, msg)

    def FOR_STMT_2(self):
        from Interprete import Interprete

        if self.errors:
            return
        if self.preanalysis.type in [
            TokenType.NEGATION,
            TokenType.LESS,
            TokenType.GREAT,
            TokenType.TRUE,
            TokenType.FALSE,
            TokenType.NULL,
            TokenType.THIS,
            TokenType.NUMBER,
            TokenType.STRING,
            TokenType.IDENTIFIER,
            TokenType.PARENT_OPEN,
            TokenType.SUPER,
        ]:
            self.EXPRESSION()
            self.matchToken(TokenType.SEMICOLON)
        elif self.preanalysis.type == TokenType.SEMICOLON:
            self.matchToken(TokenType.SEMICOLON)
        else:
            self.errors = True
            msg = f"No se esperaba el token: {self.preanalysis.type}"
            Interprete.error(self.preanalysis.line, msg)

    def FOR_STMT_3(self):
        from Interprete import Interprete

        if self.errors:
            return
        if self.preanalysis.type in [
            TokenType.NEGATION,
            TokenType.LESS,
            TokenType.GREAT,
            TokenType.TRUE,
            TokenType.FALSE,
            TokenType.NULL,
            TokenType.THIS,
            TokenType.NUMBER,
            TokenType.STRING,
            TokenType.IDENTIFIER,
            TokenType.PARENT_OPEN,
            TokenType.SUPER,
        ]:
            self.EXPRESSION()

    def IF_STMT(self):
        from Interprete import Interprete

        if self.errors:
            return
        if self.preanalysis.type == TokenType.IF:
            self.matchToken(TokenType.IF)
            self.matchToken(TokenType.PARENT_OPEN)
            self.EXPRESSION()
            self.matchToken(TokenType.PARENT_CLOSE)
            self.STATEMENT()
            self.ELSE_STATEMENT()
        else:
            self.errors = True
            msg = f"No se esperaba el token: {self.preanalysis.type}"
            Interprete.error(self.preanalysis.line, msg)

    def ELSE_STATEMENT(self):
        from Interprete import Interprete

        if self.errors:
            return
        if self.preanalysis.type == TokenType.ELSE:
            self.matchToken(TokenType.ELSE)
            self.STATEMENT()

    def PRINT_STMT(self):
        from Interprete import Interprete

        if self.errors:
            return
        if self.preanalysis.type == TokenType.PRINT:
            self.matchToken(TokenType.PRINT)
            self.EXPRESSION()
            self.matchToken(TokenType.SEMICOLON)
        else:
            self.errors = True
            msg = f"No se esperaba el token: {self.preanalysis.type}"
            Interprete.error(self.preanalysis.line, msg)

    def RETURN_STMT(self):
        from Interprete import Interprete

        if self.errors:
            return
        if self.preanalysis.type == TokenType.RETURN:
            self.matchToken(TokenType.RETURN)
            self.RETURN_EXP_OPC()
            self.matchToken(TokenType.SEMICOLON)
        else:
            self.errors = True
            msg = f"No se esperaba el token: {self.preanalysis.type}"
            Interprete.error(self.preanalysis.line, msg)

    def RETURN_EXP_OPC(self):
        from Interprete import Interprete

        if self.errors:
            return
        if self.preanalysis.type in [
            TokenType.NEGATION,
            TokenType.LESS,
            TokenType.GREAT,
            TokenType.TRUE,
            TokenType.FALSE,
            TokenType.NULL,
            TokenType.THIS,
            TokenType.NUMBER,
            TokenType.STRING,
            TokenType.IDENTIFIER,
            TokenType.PARENT_OPEN,
            TokenType.SUPER,
        ]:
            self.EXPRESSION()

    def WHILE_STMT(self):
        from Interprete import Interprete

        if self.errors:
            return
        if self.preanalysis.type == TokenType.WHILE:
            self.matchToken(TokenType.WHILE)
            self.matchToken(TokenType.PARENT_OPEN)
            self.EXPRESSION()
            self.matchToken(TokenType.PARENT_CLOSE)
            self.STATEMENT()
        else:
            self.errors = True
            msg = f"No se esperaba el token: {self.preanalysis.type}"
            Interprete.error(self.preanalysis.line, msg)

    def BLOCK(self):
        from Interprete import Interprete

        if self.errors:
            return
        if self.preanalysis.type == TokenType.BRACKET_OPEN:
            self.matchToken(TokenType.BRACKET_OPEN)
            self.BLOCK_DECL()
            self.matchToken(TokenType.BRACKET_CLOSE)
        else:
            self.errors = True
            msg = f"No se esperaba el token: {self.preanalysis.type}"
            Interprete.error(self.preanalysis.line, msg)

    def BLOCK_DECL(self):
        from Interprete import Interprete

        if self.errors:
            return
        if self.preanalysis.type in [
            TokenType.CLASS,
            TokenType.FUN,
            TokenType.VAR,
            TokenType.NEGATION,
            TokenType.LESS,
            TokenType.GREAT,
            TokenType.TRUE,
            TokenType.FALSE,
            TokenType.NULL,
            TokenType.THIS,
            TokenType.NUMBER,
            TokenType.STRING,
            TokenType.IDENTIFIER,
            TokenType.PARENT_OPEN,
            TokenType.SUPER,
            TokenType.FOR,
            TokenType.IF,
            TokenType.PRINT,
            TokenType.RETURN,
            TokenType.WHILE,
            TokenType.BRACKET_OPEN,
        ]:
            self.DECLARATION()
            self.BLOCK_DECL()

    def EXPRESSION(self):
        from Interprete import Interprete

        if self.errors:
            return
        if self.preanalysis.type in [
            TokenType.NEGATION,
            TokenType.LESS,
            TokenType.TRUE,
            TokenType.FALSE,
            TokenType.NULL,
            TokenType.THIS,
            TokenType.NUMBER,
            TokenType.STRING,
            TokenType.IDENTIFIER,
            TokenType.PARENT_OPEN,
            TokenType.SUPER,
            TokenType.GREAT,
        ]:
            self.ASSIGNMENT()
        else:
            self.errors = True
            msg = f"No se esperaba el token: {self.preanalysis.type}"
            Interprete.error(self.preanalysis.line, msg)

    def ASSIGNMENT(self):
        from Interprete import Interprete

        if self.errors:
            return
        if self.preanalysis.type in [
            TokenType.NEGATION,
            TokenType.LESS,
            TokenType.TRUE,
            TokenType.FALSE,
            TokenType.NULL,
            TokenType.THIS,
            TokenType.NUMBER,
            TokenType.STRING,
            TokenType.IDENTIFIER,
            TokenType.PARENT_OPEN,
            TokenType.SUPER,
            TokenType.GREAT,
        ]:
            self.LOGIC_OR()
            self.ASSIGNMENT_OPC()
        else:
            self.errors = True
            msg = f"No se esperaba el token: {self.preanalysis.type}"
            Interprete.error(self.preanalysis.line, msg)

    def ASSIGNMENT_OPC(self):
        from Interprete import Interprete

        if self.errors:
            return
        if self.preanalysis.type == TokenType.ASIGNATION:
            self.matchToken(TokenType.ASIGNATION)
            self.EXPRESSION()

    def LOGIC_OR(self):
        from Interprete import Interprete

        if self.errors:
            return
        if self.preanalysis.type in [
            TokenType.NEGATION,
            TokenType.LESS,
            TokenType.TRUE,
            TokenType.FALSE,
            TokenType.NULL,
            TokenType.THIS,
            TokenType.NUMBER,
            TokenType.STRING,
            TokenType.IDENTIFIER,
            TokenType.PARENT_OPEN,
            TokenType.SUPER,
            TokenType.GREAT,
        ]:
            self.LOGIC_AND()
            self.LOGIC_OR_2()
        else:
            self.errors = True
            msg = f"No se esperaba el token: {self.preanalysis.type}"
            Interprete.error(self.preanalysis.line, msg)

    def LOGIC_OR_2(self):
        from Interprete import Interprete

        if self.errors:
            return
        if self.preanalysis.type == TokenType.OR:
            self.matchToken(TokenType.OR)
            self.LOGIC_AND()
            self.LOGIC_OR_2()

    def LOGIC_AND(self):
        from Interprete import Interprete

        if self.errors:
            return
        if self.preanalysis.type in [
            TokenType.NEGATION,
            TokenType.LESS,
            TokenType.TRUE,
            TokenType.FALSE,
            TokenType.NULL,
            TokenType.THIS,
            TokenType.NUMBER,
            TokenType.STRING,
            TokenType.IDENTIFIER,
            TokenType.PARENT_OPEN,
            TokenType.SUPER,
            TokenType.GREAT,
        ]:
            self.EQUALITY()
            self.LOGIC_AND_2()
        else:
            self.errors = True
            msg = f"No se esperaba el token: {self.preanalysis.type}"
            Interprete.error(self.preanalysis.line, msg)

    def LOGIC_AND_2(self):
        from Interprete import Interprete

        if self.errors:
            return
        if self.preanalysis.type == TokenType.AND:
            self.matchToken(TokenType.AND)
            self.EQUALITY()
            self.LOGIC_AND_2()

    def EQUALITY(self):
        from Interprete import Interprete

        if self.errors:
            return
        if self.preanalysis.type in [
            TokenType.NEGATION,
            TokenType.LESS,
            TokenType.TRUE,
            TokenType.FALSE,
            TokenType.NULL,
            TokenType.THIS,
            TokenType.NUMBER,
            TokenType.STRING,
            TokenType.IDENTIFIER,
            TokenType.PARENT_OPEN,
            TokenType.SUPER,
            TokenType.GREAT,
        ]:
            self.COMPARISON()
            self.EQUALITY_2()
        else:
            self.errors = True
            msg = f"No se esperaba el token: {self.preanalysis.type}"
            Interprete.error(self.preanalysis.line, msg)

    def EQUALITY_2(self):
        from Interprete import Interprete

        if self.errors:
            return
        if self.preanalysis.type == TokenType.DIFERENT:
            self.matchToken(TokenType.DIFERENT)
            self.COMPARISON()
            self.EQUALITY_2()
        elif self.preanalysis.type == TokenType.EQUAL:
            self.matchToken(TokenType.EQUAL)
            self.COMPARISON()
            self.EQUALITY_2()

    def COMPARISON(self):
        from Interprete import Interprete

        if self.errors:
            return
        if self.preanalysis.type in [
            TokenType.NEGATION,
            TokenType.LESS,
            TokenType.TRUE,
            TokenType.FALSE,
            TokenType.NULL,
            TokenType.THIS,
            TokenType.NUMBER,
            TokenType.STRING,
            TokenType.IDENTIFIER,
            TokenType.PARENT_OPEN,
            TokenType.SUPER,
            TokenType.GREAT,
        ]:
            self.TERM()
            self.COMPARISON_2()
        else:
            self.errors = True
            msg = f"No se esperaba el token: {self.preanalysis.type}"
            Interprete.error(self.preanalysis.line, msg)

    def COMPARISON_2(self):
        from Interprete import Interprete

        if self.errors:
            return
        if self.preanalysis.type == TokenType.GREAT_THAN:
            self.matchToken(TokenType.GREAT_THAN)
            self.TERM()
            self.COMPARISON_2()
        elif self.preanalysis.type == TokenType.GREAT_EQUAL:
            self.matchToken(TokenType.GREAT_EQUAL)
            self.TERM()
            self.COMPARISON_2()
        elif self.preanalysis.type == TokenType.LESS_THAN:
            self.matchToken(TokenType.LESS_THAN)
            self.TERM()
            self.COMPARISON_2()
        elif self.preanalysis.type == TokenType.LESS_EQUAL:
            self.matchToken(TokenType.LESS_EQUAL)
            self.TERM()
            self.COMPARISON_2()

    def TERM(self):
        from Interprete import Interprete

        if self.errors:
            return
        if self.preanalysis.type in [
            TokenType.NEGATION,
            TokenType.LESS,
            TokenType.TRUE,
            TokenType.FALSE,
            TokenType.NULL,
            TokenType.THIS,
            TokenType.NUMBER,
            TokenType.STRING,
            TokenType.IDENTIFIER,
            TokenType.PARENT_OPEN,
            TokenType.SUPER,
            TokenType.GREAT,
        ]:
            self.FACTOR()
            self.TERM_2()
        else:
            self.errors = True
            msg = f"No se esperaba el token: {self.preanalysis.type}"
            Interprete.error(self.preanalysis.line, msg)

    def TERM_2(self):
        from Interprete import Interprete

        if self.errors:
            return
        if self.preanalysis.type == TokenType.LESS:
            self.matchToken(TokenType.LESS)
            self.FACTOR()
            self.TERM_2()
        elif self.preanalysis.type == TokenType.ADD:
            self.matchToken(TokenType.ADD)
            self.FACTOR()
            self.TERM_2()
        elif self.preanalysis.type == TokenType.SUB:
            self.matchToken(TokenType.SUB)
            self.FACTOR()
            self.TERM_2()
        elif self.preanalysis.type == TokenType.GREAT:
            self.matchToken(TokenType.GREAT)
            self.FACTOR()
            self.TERM_2()

    def FACTOR(self):
        from Interprete import Interprete

        if self.errors:
            return
        if self.preanalysis.type in [
            TokenType.NEGATION,
            TokenType.LESS,
            TokenType.TRUE,
            TokenType.FALSE,
            TokenType.NULL,
            TokenType.THIS,
            TokenType.NUMBER,
            TokenType.STRING,
            TokenType.IDENTIFIER,
            TokenType.PARENT_OPEN,
            TokenType.SUPER,
            TokenType.GREAT,
        ]:
            self.UNARY()
            self.FACTOR_2()
        else:
            self.errors = True
            msg = f"No se esperaba el token: {self.preanalysis.type}"
            Interprete.error(self.preanalysis.line, msg)

    def FACTOR_2(self):
        from Interprete import Interprete

        if self.errors:
            return
        if self.preanalysis.type == TokenType.DIAG:
            self.matchToken(TokenType.DIAG)
            self.UNARY()
            self.FACTOR_2()
        elif self.preanalysis.type == TokenType.MULT:
            self.matchToken(TokenType.MULT)
            self.UNARY()
            self.FACTOR_2()

    def UNARY(self):
        from Interprete import Interprete

        if self.errors:
            return
        if self.preanalysis.type == TokenType.NEGATION:
            self.matchToken(TokenType.NEGATION)
            self.UNARY()
        elif self.preanalysis.type == TokenType.LESS:
            self.matchToken(TokenType.LESS)
            self.UNARY()
        elif self.preanalysis.type == TokenType.GREAT:
            self.matchToken(TokenType.GREAT)
            self.UNARY()
        elif self.preanalysis.type in [
            TokenType.TRUE,
            TokenType.FALSE,
            TokenType.NULL,
            TokenType.THIS,
            TokenType.NUMBER,
            TokenType.STRING,
            TokenType.IDENTIFIER,
            TokenType.PARENT_OPEN,
            TokenType.SUPER,
        ]:
            self.CALL()
        else:
            self.errors = True
            msg = f"No se esperaba el token: {self.preanalysis.type}"
            Interprete.error(self.preanalysis.line, msg)

    def CALL(self):
        from Interprete import Interprete

        if self.errors:
            return
        if self.preanalysis.type in [
            TokenType.TRUE,
            TokenType.FALSE,
            TokenType.NULL,
            TokenType.THIS,
            TokenType.NUMBER,
            TokenType.STRING,
            TokenType.IDENTIFIER,
            TokenType.PARENT_OPEN,
            TokenType.SUPER,
        ]:
            self.PRIMARY()
            self.CALL_2()
        else:
            self.errors = True
            msg = f"No se esperaba el token: {self.preanalysis.type}"
            Interprete.error(self.preanalysis.line, msg)

    def CALL_2(self):
        from Interprete import Interprete

        if self.errors:
            return
        if self.preanalysis.type == TokenType.PARENT_OPEN:
            self.matchToken(TokenType.PARENT_OPEN)
            self.ARGUMENTS_OPC()
            self.matchToken(TokenType.PARENT_CLOSE)
            self.CALL_2()
        elif self.preanalysis.type == TokenType.DOT:
            self.matchToken(TokenType.DOT)
            self.matchToken(TokenType.IDENTIFIER)
            self.CALL_2()

    def PRIMARY(self):
        from Interprete import Interprete

        if self.errors:
            return
        if self.preanalysis.type == TokenType.TRUE:
            self.matchToken(TokenType.TRUE)
        elif self.preanalysis.type == TokenType.FALSE:
            self.matchToken(TokenType.FALSE)
        elif self.preanalysis.type == TokenType.NULL:
            self.matchToken(TokenType.NULL)
        elif self.preanalysis.type == TokenType.THIS:
            self.matchToken(TokenType.THIS)
        elif self.preanalysis.type == TokenType.NUMBER:
            self.matchToken(TokenType.NUMBER)
        elif self.preanalysis.type == TokenType.STRING:
            self.matchToken(TokenType.STRING)
        elif self.preanalysis.type == TokenType.IDENTIFIER:
            self.matchToken(TokenType.IDENTIFIER)
        elif self.preanalysis.type == TokenType.PARENT_OPEN:
            self.matchToken(TokenType.PARENT_OPEN)
            self.EXPRESSION()
            self.matchToken(TokenType.PARENT_CLOSE)
        elif self.preanalysis.type == TokenType.SUPER:
            self.matchToken(TokenType.SUPER)
            self.matchToken(TokenType.DOT)
            self.matchToken(TokenType.IDENTIFIER)
        else:
            self.errors = True
            msg = f"No se esperaba el token: {self.preanalysis.type}"
            Interprete.error(self.preanalysis.line, msg)

    def FUNCTION(self):
        from Interprete import Interprete

        if self.errors:
            return
        if self.preanalysis.type == TokenType.IDENTIFIER:
            self.matchToken(TokenType.IDENTIFIER)
            self.matchToken(TokenType.PARENT_OPEN)
            self.PARAMETERS_OPC()
            self.matchToken(TokenType.PARENT_CLOSE)
            self.jump_op()
            self.BLOCK()
        else:
            self.errors = True
            msg = f"No se esperaba el token: {self.preanalysis.type}"
            Interprete.error(self.preanalysis.line, msg)

    def FUNCTIONS(self):
        from Interprete import Interprete

        if self.errors:
            return
        if self.preanalysis.type == TokenType.IDENTIFIER:
            self.FUNCTION()
            self.FUNCTIONS()

    def PARAMETERS_OPC(self):
        from Interprete import Interprete

        if self.errors:
            return
        if self.preanalysis.type == TokenType.IDENTIFIER:
            self.PARAMETERS()

    def jump_op(self):
        from Interprete import Interprete

        if self.errors:
            return
        if self.preanalysis.type == TokenType.jump:
            self.jump_par()

    def jump_par(self):
        from Interprete import Interprete

        if self.errors:
            return
        if self.preanalysis.type == TokenType.jump:
            self.matchToken(TokenType.jump)
        else:
            self.errors = True
            msg = f"No se esperaba el token: {self.preanalysis.type}"
            Interprete.error(self.preanalysis.line, msg)

    def PARAMETERS(self):
        from Interprete import Interprete

        if self.errors:
            return
        if self.preanalysis.type == TokenType.IDENTIFIER:
            self.matchToken(TokenType.IDENTIFIER)
            self.PARAMETERS_2()
        else:
            self.errors = True
            msg = f"No se esperaba el token: {self.preanalysis.type}"
            Interprete.error(self.preanalysis.line, msg)

    def PARAMETERS_2(self):
        from Interprete import Interprete

        if self.errors:
            return
        if self.preanalysis.type == TokenType.COMMA:
            self.matchToken(TokenType.COMMA)
            self.matchToken(TokenType.IDENTIFIER)
            self.PARAMETERS_2()

    def ARGUMENTS_OPC(self):
        from Interprete import Interprete

        if self.errors:
            return
        if self.preanalysis.type in [
            TokenType.NEGATION,
            TokenType.LESS,
            TokenType.TRUE,
            TokenType.FALSE,
            TokenType.NULL,
            TokenType.THIS,
            TokenType.NUMBER,
            TokenType.STRING,
            TokenType.IDENTIFIER,
            TokenType.PARENT_OPEN,
            TokenType.SUPER,
            TokenType.GREAT,
        ]:
            self.ARGUMENTS()

    def ARGUMENTS(self):
        from Interprete import Interprete

        if self.errors:
            return
        if self.preanalysis.type in [
            TokenType.NEGATION,
            TokenType.LESS,
            TokenType.GREAT,
            TokenType.TRUE,
            TokenType.FALSE,
            TokenType.NULL,
            TokenType.THIS,
            TokenType.NUMBER,
            TokenType.STRING,
            TokenType.IDENTIFIER,
            TokenType.PARENT_OPEN,
            TokenType.SUPER,
        ]:
            self.EXPRESSION()
            self.ARGUMENTS_2()
        else:
            self.errors = True
            msg = f"No se esperaba el token: {self.preanalysis.type}"
            Interprete.error(self.preanalysis.line, msg)

    def ARGUMENTS_2(self):
        from Interprete import Interprete

        if self.errors:
            return
        if self.preanalysis.type == TokenType.COMMA:
            self.matchToken(TokenType.COMMA)
            self.EXPRESSION()
            self.ARGUMENTS_2()

    def matchToken(self, t):
        from Interprete import Interprete

        if self.errors:
            return
        if self.preanalysis.type == t:
            self.i += 1
            self.preanalysis = self.tokens[self.i]
        else:
            self.errors = True
            msg = f"Se esperaba el token: {t}"
            Interprete.error(self.preanalysis.line, msg)
