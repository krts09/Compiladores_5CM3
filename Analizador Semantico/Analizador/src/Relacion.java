import SymbolTable as ts;
import Postfija as Postfix;
import Aritmetico as SolverAritmetico;

public class SolverRel {
    private Postfix poshelp;

    public SolverRel() {
        this.poshelp = new Postfix([]);
    }

    public Object resolver(Nodo n) {
        if (n.hijos == null) {
            if (n.value.type == TokenType.NUMBER || n.value.type == TokenType.STRING || n.value.type == TokenType.TRUE || n.value.type == TokenType.FALSE) {
                return n.value.literal;
            } else if (n.value.type == TokenType.IDENTIFIER) {
                return ts.simbolos.obtener(n.value.lexeme);
            }
        }
        Nodo nizq = n.hijos[0];
        Nodo nder = n.hijos[1];
        if (this.poshelp.esOperador(nizq.value.type) && this.poshelp.esOperador(nder.value.type)) {
            SolverAritmetico sol = new SolverAritmetico();
            Object rizquierdo = sol.resolver(nizq);
            Object rderecho = sol.resolver(nder);
        } else if (this.poshelp.esOperador(nizq.value.type)) {
            Object rderecho = this.resolver(nder);
            SolverAritmetico sol = new SolverAritmetico();
            Object rizquierdo = sol.resolver(nizq);
        } else if (this.poshelp.esOperador(nder.value.type)) {
            Object rizquierdo = this.resolver(nizq);
            SolverAritmetico sol = new SolverAritmetico();
            Object rderecho = sol.resolver(nder);
        } else {
            Object rizquierdo = this.resolver(nizq);
            Object rderecho = this.resolver(nder);
        }

        if (rizquierdo.getClass() == rderecho.getClass() && rderecho.getClass() == Float.class) {
            switch (n.value.type) {
                case TokenType.GREAT:
                    return rizquierdo > rderecho;
                case TokenType.GREAT_EQUAL:
                    return rizquierdo >= rderecho;
                case TokenType.EQUAL:
                    return rizquierdo == rderecho;
                case TokenType.LESS_THAN:
                    return rizquierdo < rderecho;
                case TokenType.LESS_EQUAL:
                    return rizquierdo <= rderecho;
                case TokenType.DIFERENT:
                    return rizquierdo != rderecho;
            }
        } else if (rizquierdo.getClass() == rderecho.getClass()) {
            if (n.value.type == TokenType.EQUAL) {
                return rizquierdo == rderecho;
            } else {
                System.out.println("Error: No se puede resolver la operacion " + n.value.type.toString().substring(10) + " con las instancias " + rizquierdo.getClass().getName() + " y " + rderecho.getClass().getName());
                System.exit(0);
            }
        } else {
            System.out.println("Error: No se puede resolver la operacion " + n.value.type.toString().substring(10) + " con las instancias " + rizquierdo.getClass().getName() + " y " + rderecho.getClass().getName());
            System.exit(0);
        }
        return null;
    }
}

