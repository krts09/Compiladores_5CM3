import SymbolTable as ts;
import Postfija.Postfix;
import Relacion.SolverRel;

public class SolverLogic {
    private Postfix poshelp;

    public SolverLogic() {
        this.poshelp = new Postfix(new ArrayList<>());
    }

    public Object resolver(Nodo n) {
        if (n.hijos == null) {
            if (n.value.type == TokenType.TRUE || n.value.type == TokenType.FALSE) {
                return n.value.literal;
            } else if (n.value.type == TokenType.IDENTIFIER) {
                return ts.simbolos.obtener(n.value.lexeme);
            } else {
                return null;
            }
        }
        Nodo nizq = n.hijos[0];
        Nodo nder = n.hijos[1];
        if (this.poshelp.esOperador(nizq.value.type) && this.poshelp.esOperador(nder.value.type)) {
            SolverRel sol = new SolverRel();
            Object rizquierdo = sol.resolver(nizq);
            Object rderecho = sol.resolver(nder);
        } else if (this.poshelp.esOperador(nizq.value.type)) {
            Object rderecho = this.resolver(nder);
            SolverRel sol = new SolverRel();
            Object rizquierdo = sol.resolver(nizq);
        } else if (this.poshelp.esOperador(nder.value.type)) {
            Object rizquierdo = this.resolver(nizq);
            SolverRel sol = new SolverRel();
            Object rderecho = sol.resolver(nder);
        } else {
            Object rizquierdo = this.resolver(nizq);
            Object rderecho = this.resolver(nder);
        }
        if (rizquierdo instanceof Boolean && rderecho instanceof Boolean) {
            switch (n.value.type) {
                case TokenType.AND:
                    return (Boolean) rizquierdo && (Boolean) rderecho;
                case TokenType.OR:
                    return (Boolean) rizquierdo || (Boolean) rderecho;
            }
        } else {
            System.out.println("No se puede realizar la operacion " + n.value.type.toString().substring(10) + " con las instancias " + rizquierdo.toString() + " y " + rderecho.toString());
        }
    }
}
