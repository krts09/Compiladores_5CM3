from Tokentype import TokenType
from Token import Token
from Nodo import Nodo
import SymbolTable as ts
import sys


class SolverAritmetico:
    def __init__(self) -> None:
        pass

    def resolver(self, n: Nodo):
        if n.hijos is None:
            if n.value.type == TokenType.NUMBER or n.value.type == TokenType.STRING:
                return n.value.literal

            elif n.value.type == TokenType.IDENTIFIER:
                return ts.simbolos.obtener(n.value.lexeme)
            else:
                return None

        nizq: Nodo = n.hijos[0]
        nder: Nodo = n.hijos[1]

        rizquierdo = self.resolver(nizq)
        rderecho = self.resolver(nder)

        if isinstance(rizquierdo, float) and isinstance(rderecho, float):
            match n.value.type:
                case TokenType.ADD:
                    return rizquierdo + rderecho
                case TokenType.SUB:
                    return rizquierdo - rderecho
                case TokenType.MULT:
                    return rizquierdo * rderecho
                case TokenType.DIAG:
                    return rizquierdo / rderecho
        elif isinstance(rizquierdo, str) and isinstance(rderecho, str):
            if n.value.type == TokenType.ADD:
                return rizquierdo + rderecho
        else:
            print(F"Error: No se puede resolver la operacion {n.value.type} con las instancias {type(rizquierdo)} y {type(rderecho)}")
        return None