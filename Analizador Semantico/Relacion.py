from Tokentype import TokenType
from Token import Token
from Nodo import Nodo
import SymbolTable as ts
import sys
from Postfija import Postfix
from Aritmetico import SolverAritmetico

class SolverRel:
    def __init__(self) -> None:
        self.poshelp = Postfix([])
        pass

    def resolver(self, n:Nodo):

        if n.hijos is None:
            if n.value.type == TokenType.NUMBER or n.value.type == TokenType.STRING or n.value.type ==TokenType.TRUE or n.value.type == TokenType.FALSE:
                return n.value.literal
            elif n.value.type == TokenType.IDENTIFIER:
                return ts.simbolos.obtener(n.value.lexeme)


        nizq:Nodo = n.hijos[0]
        nder:Nodo = n.hijos[1]
        
        if self.poshelp.esOperador(nizq.value.type) and self.poshelp.esOperador(nder.value.type):
            sol = SolverAritmetico()
            rizquierdo = sol.resolver(nizq)
            rderecho = sol.resolver(nder)
        elif self.poshelp.esOperador(nizq.value.type):
            rderecho = self.resolver(nder)
            sol = SolverAritmetico()
            rizquierdo = sol.resolver(nizq)
        elif self.poshelp.esOperador(nder.value.type):
            rizquierdo = self.resolver(nizq)
            sol = SolverAritmetico()
            rderecho = sol.resolver(nder)
        else:
            rizquierdo = self.resolver(nizq)
            rderecho = self.resolver(nder)


        #rizquierdo = self.resolver(nizq)
        #rderecho = self.resolver(nder)

        if type(rizquierdo) == type(rderecho) and type(rderecho) == float:
            match n.value.type:
                case TokenType.GREAT:
                    return rizquierdo > rderecho
                case TokenType.GREAT_EQUAL:
                    return rizquierdo >= rderecho
                case TokenType.EQUAL:
                    return rizquierdo == rderecho
                case TokenType.LESS_THAN:
                    return rizquierdo < rderecho
                case TokenType.LESS_EQUAL:
                    return rizquierdo <= rderecho
                case TokenType.DIFERENT:
                    return rizquierdo != rderecho
        elif type(rizquierdo) == type(rderecho):
            if n.value.type == TokenType.EQUAL:
                return rizquierdo == rderecho
            else:
                print(F"Error: No se puede resolver la operacion {str(n.value.type)[10:]} con las instancias {type(rizquierdo)} y {type(rderecho)}")
                sys.exit()
        else:
            print(F"Error: No se puede resolver la operacion {str(n.value.type)[10:]} con las instancias {type(rizquierdo)} y {type(rderecho)}")
            sys.exit()
        return None





        

    
