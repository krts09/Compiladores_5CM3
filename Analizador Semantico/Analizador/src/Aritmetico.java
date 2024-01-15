import TokenType;
import Token;
import Nodo;
import SymbolTable as ts;
import sys;

public class SolverAritmetico {
    public SolverAritmetico() {

    }

    public Object resolver(Nodo n) {
        if (n.hijos == null) {
            if (n.value.type == TokenType.NUMBER || n.value.type == TokenType.STRING) {
                return n.value.literal;
            } else if (n.value.type == TokenType.IDENTIFIER) {
                return ts.simbolos.obtener(n.value.lexeme);
            } else {
                return null;
            }
        }
        Nodo nizq = n.hijos[0];
        Nodo nder = n.hijos[1];
        Object rizquierdo = resolver(nizq);
        Object rderecho = resolver(nder);
        if (rizquierdo instanceof Float && rderecho instanceof Float) {
            switch (n.value.type) {
                case TokenType.ADD:
                    return (Float)rizquierdo + (Float)rderecho;
                case TokenType.SUB:
                    return (Float)rizquierdo - (Float)rderecho;
                case TokenType.MULT:
                    return (Float)rizquierdo * (Float)rderecho;
                case TokenType.DIAG:
                    return (Float)rizquierdo / (Float)rderecho;
            }
        } else if (rizquierdo instanceof String && rderecho instanceof String) {
            if (n.value.type == TokenType.ADD) {
                return (String)rizquierdo + (String)rderecho;
            }
        } else {
            System.out.println("Error: No se puede resolver la operacion " + n.value.type + " con las instancias " + rizquierdo.getClass().getName() + " y " + rderecho.getClass().getName());
        }
        return null;
    }
}

