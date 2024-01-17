from Tokentype import TokenType
from Token import Token
from Nodo import Nodo
from Aritmetico import SolverAritmetico
import SymbolTable as ts
from Relacion import SolverRel
from Logic import SolverLogic
from Postfija import Postfix
import sys


class Arbol:
    def __init__(self, raiz: Nodo) -> None:
        self.raiz = raiz
        self.poshelp = Postfix([])

    def recorrer(self):

        for index, n in enumerate(self.raiz.hijos):
            t = n.value
            match t.type:
                case TokenType.ADD | TokenType.SUB | TokenType.MULT | TokenType.DIAG:
                    solver = SolverAritmetico()
                    res = solver.resolver(n)
                    print(f"{res}")
                case TokenType.VAR:
                    self.solverVar(n)

                case TokenType.IF:
                    self.resolverIf(n)
                case TokenType.WHILE:
                    self.solverWhile(n)
                case TokenType.FOR:
                    self.solverFor(n)
                    pass
                case TokenType.GREAT_EQUAL | TokenType.EQUAL | TokenType.GREAT | TokenType.LESS_EQUAL | TokenType.LESS_THAN | TokenType.DIFERENT:
                    solver = SolverRel()
                    res = solver.resolver(n)
                    print(f"{res}")
                case TokenType.AND | TokenType.OR:
                    
                    solver = SolverLogic()
                    res = solver.resolver(n)
                    print(f"{res}")
                case TokenType.PRINT:
                    res = self.solverPrint(n)
                    print(f"{res}")
                    pass
                case TokenType.ASIGNATION:
                    self.solverAsig(n)
                    pass

    def resolverIf(self, n: Nodo):
        if n.hijos is None:
            if n.value.type == TokenType.NUMBER or n.value.type == TokenType.STRING:
                return n.value.literal
            elif n.value.type == TokenType.IDENTIFIER:
                return ts.simbolos.obtener(n.value.lexeme)

        cond = n.hijos[0]
        rcond = self.checkCond(cond)

        if n.hijos[-1].value.type == TokenType.ELSE:
            el = True
            body = n.hijos[1:]
            ebody = n.hijos[-1].hijos
        else:
            body = n.hijos[1:]
            el = False

        if rcond:
            raiz = Nodo(Token(TokenType.NULL, "", "", None))
            raiz.insertar_hijos(body)
            araux = Arbol(raiz)
            araux.recorrer()
        else:
            if el:
                raiz = Nodo(Token(TokenType.NULL, "", "", None))
                raiz.insertar_hijos(ebody)
                araux = Arbol(raiz)
                araux.recorrer()
            else:
                pass

    def solverWhile(self, n: Nodo):

        cond = n.hijos[0]
        body = n.hijos[1:]

        while self.checkCond(cond):
            raiz = Nodo(Token(TokenType.NULL, "", "", None))
            raiz.insertar_hijos(body)
            araux = Arbol(raiz)
            araux.recorrer()

    def checkCond(self, cond: Nodo) -> bool:
        match cond.value.type:
            case TokenType.GREAT_EQUAL | TokenType.EQUAL | TokenType.GREAT | TokenType.LESS_EQUAL | TokenType.LESS_THAN | TokenType.DIFERENT:
                solver = SolverRel()
                rcond = solver.resolver(cond)
                return rcond
            case TokenType.AND | TokenType.OR:

                solver = SolverLogic()
                rcond = solver.resolver(cond)
                return rcond
            case TokenType.TRUE:
                rcond = True
                return rcond
            case TokenType.FALSE:
                rcond = False
                return rcond
            case TokenType.IDENTIFIER:
                if ts.simbolos.existeIdentificador(cond.value.lexeme):
                    if isinstance(ts.simbolos.obtener(cond.value.lexeme), bool):
                        rcond = ts.simbolos.obtener(cond.value.lexeme)
                        return rcond
                    else:
                        print("Error la variable evaluada no es un boleano.\n")
                        sys.exit()
                else:
                    print(f"Error: La variable {cond.value.lexeme} no existe.\n")
                    sys.exit()
            case _:
                print("Error: El resultado debe ser un boleano")
                sys.exit()

    def solverFor(self, n: Nodo):
        ini = n.hijos[0]
        cond = n.hijos[1]
        increase = n.hijos[2]
        body = n.hijos[3:]

        self.solverVar(n.hijos[0])

        while self.checkCond(cond):
            raiz = Nodo(Token(TokenType.NULL, "", "", None))
            raiz.insertar_hijos(body)
            araux = Arbol(raiz)
            araux.recorrer()
            self.solverAsig(increase)

        pass

    def solverVar(self, n: Nodo):

        if len(n.hijos) == 1:
            if ts.simbolos.existeIdentificador(n.hijos[0].value.lexeme):
                print(f"Error: La variable {n.hijos[0].value.lexeme} ya existe")
                return
            ts.simbolos.asignar(n.hijos[0].value.lexeme, None)
            return
        elif len(n.hijos) == 2:

            if ts.simbolos.existeIdentificador(n.hijos[0].value.lexeme):
                print(f"Error: La variable {n.hijos[0].value.lexeme} ya existe")
                return
            else:
                key = n.hijos[0].value.lexeme

            if self.poshelp.esOperador(n.hijos[1].value.type):
                match n.hijos[1].value.type:
                    case TokenType.ADD | TokenType.SUB | TokenType.MULT | TokenType.DIAG:
                        solver = SolverAritmetico()
                        value = solver.resolver(n.hijos[1])
                    case TokenType.GREAT_EQUAL | TokenType.EQUAL | TokenType.GREAT | TokenType.LESS_EQUAL | TokenType.LESS_THAN | TokenType.DIFERENT:
                        solver = SolverRel()
                        value = solver.resolver(n.hijos[1])
                    case TokenType.AND | TokenType.OR:
                        solver = SolverLogic()
                        value = solver.resolver(n.hijos[1])



            elif n.hijos[1].value.type == TokenType.IDENTIFIER:
                if ts.simbolos.existeIdentificador(n.hijos[1].value.lexeme):
                    value = ts.simbolos.obtener(n.hijos[1].value.lexeme)
                    pass
                else:
                    print(
                        f"Error: La variable {n.hijos[1].value.lexeme} no existe, por lo cual no puede ser asignada a {n.hijos[0].value.lexeme}\n")
                    sys.exit()

            else:
                value = n.hijos[1].value.literal
            ts.simbolos.asignar(key, value)
            return
        else:
            print("Error al declarar la variable")
            return None

    def solverAsig(self, n: Nodo):

        if ts.simbolos.existeIdentificador(n.hijos[0].value.lexeme):

            if n.hijos[0].value.type == TokenType.IDENTIFIER:
                if self.poshelp.esOperador(n.hijos[1].value.type):

                    match n.hijos[1].value.type:
                        case TokenType.ADD | TokenType.SUB | TokenType.MULT | TokenType.DIAG:
                            solver = SolverAritmetico()
                            value = solver.resolver(n.hijos[1])
                        case TokenType.GREAT_EQUAL | TokenType.EQUAL | TokenType.GREAT | TokenType.LESS_EQUAL | TokenType.LESS_THAN | TokenType.DIFERENT:
                            solver = SolverRel()
                            value = solver.resolver(n.hijos[1])
                        case TokenType.AND | TokenType.OR:
                            solver = SolverLogic()
                            value = solver.resolver(n.hijos[1])

                    ts.simbolos.reasig(n.hijos[0].value.lexeme, value)
                    return
                else:
                    ts.simbolos.reasig(n.hijos[0].value.lexeme, n.hijos[1].value.literal)
                    return
        else:
            print(f"Error: La variable {n.hijos[0].value.lexeme} no existe")
            return

    def solverPrint(self, n: Nodo):

        if n.hijos is None:
            if n.value.type == TokenType.NUMBER or n.value.type == TokenType.STRING:
                return n.value.literal
            elif n.value.type == TokenType.IDENTIFIER:
                return ts.simbolos.obtener(n.value.lexeme)

        hijo: Nodo = n.hijos[0]

        if self.poshelp.esOperador(hijo.value.type):
            match hijo.value.type:
                case TokenType.ADD | TokenType.SUB | TokenType.MULT | TokenType.DIAG:
                    solver = SolverAritmetico()
                    res = solver.resolver(hijo)
                    return res
                case TokenType.AND | TokenType.OR:

                    solver = SolverLogic()
                    res = solver.resolver(hijo)
                    return res
                case TokenType.GREAT_EQUAL | TokenType.EQUAL | TokenType.GREAT | TokenType.LESS_EQUAL | TokenType.LESS_THAN | TokenType.DIFERENT:
                    solver = SolverRel()
                    res = solver.resolver(hijo)
                    return res
        else:
            valor = self.solverPrint(hijo)
            return valor
