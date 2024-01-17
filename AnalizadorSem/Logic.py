from Tokentype import TokenType
from Nodo import Nodo
import SymbolTable as ts
from Postfija import Postfix
from Relacion import SolverRel


class SolverLogic:
    def __init__(self) -> None:
        self.poshelp = Postfix([])
        pass

    def resolver(self, n: Nodo):

        if n.hijos is None:
            if n.value.type == TokenType.TRUE or n.value.type == TokenType.FALSE:
                return n.value.literal
            elif n.value.type == TokenType.IDENTIFIER:
                return ts.simbolos.obtener(n.value.lexeme)
            else:
                return None

        nizq: Nodo = n.hijos[0]
        nder: Nodo = n.hijos[1]

        if self.poshelp.esOperador(nizq.value.type) and self.poshelp.esOperador(nder.value.type):
            sol = SolverRel()
            rizquierdo = sol.resolver(nizq)
            rderecho = sol.resolver(nder)
        elif self.poshelp.esOperador(nizq.value.type):
            rderecho = self.resolver(nder)
            sol = SolverRel()
            rizquierdo = sol.resolver(nizq)
        elif self.poshelp.esOperador(nder.value.type):
            rizquierdo = self.resolver(nizq)
            sol = SolverRel()
            rderecho = sol.resolver(nder)
        else:
            rizquierdo = self.resolver(nizq)
            rderecho = self.resolver(nder)

        if isinstance(rizquierdo, bool) and isinstance(rderecho, bool):
            match n.value.type:
                case TokenType.AND:
                    return rizquierdo and rderecho
                case TokenType.OR:
                    return rizquierdo or rderecho
        else:
            print(
                f"No se puede realizar la operacion {str(n.value.type)[10:]} con las instancias {rizquierdo} y {rderecho}")