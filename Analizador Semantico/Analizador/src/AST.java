import java.util.List;

public class GeneradorAST {
    private List<Token> postfija;
    private List<Nodo> stackk;
    private Postfix poshelp;

    public GeneradorAST(List<Token> postfija) {
        this.postfija = postfija;
        this.stackk = new ArrayList<>();
        this.poshelp = new Postfix(null);
    }

    public Arbol generarAST() {
        List<Nodo> stackPadres = new ArrayList<>();
        Nodo raiz = new Nodo(new Token(TokenType.NULL, "", "", null));
        stackPadres.add(raiz);
        Nodo padre = raiz;
        for (int i = 0; i < postfija.size(); i++) {
            Token t = postfija.get(i);
            if (t.getType() == TokenType.EOF) {
                break;
            }
            if (poshelp.palabras_reservadas.containsValue(t.getType())) {
                Nodo n = new Nodo(t);
                padre = stackPadres.get(stackPadres.size() - 1);
                padre.insertar_sig_hijo(n);
                stackPadres.add(n);
                padre = n;
            } else if (poshelp.esOperando(t.getType())) {
                Nodo n = new Nodo(t);
                stackk.add(n);
            } else if (poshelp.esOperador(t.getType())) {
                int aridad = poshelp.obAridad(t.getType());
                Nodo n = new Nodo(t);
                for (int j = 0; j < aridad; j++) {
                    Nodo nodoAux = stackk.remove(stackk.size() - 1);
                    n.insertar_hijo(nodoAux);
                }
                stackk.add(n);
            } else if (t.getType() == TokenType.SEMICOLON) {
                if (stackk.size() == 0) {
                    stackPadres.remove(stackPadres.size() - 1);
                    padre = stackPadres.get(stackPadres.size() - 1);
                } else {
                    Nodo n = stackk.remove(stackk.size() - 1);
                    if (padre.getValue().getType() == TokenType.VAR) {
                        if (n.getValue().getType() == TokenType.ASSIGNATION) {
                            padre.insertar_hijos(n.getHijos());
                        } else {
                            padre.insertar_sig_hijo(n);
                        }
                        stackPadres.remove(stackPadres.size() - 1);
                        padre = stackPadres.get(stackPadres.size() - 1);
                    } else if (padre.getValue().getType() == TokenType.PRINT) {
                        padre.insertar_sig_hijo(n);
                        stackPadres.remove(stackPadres.size() - 1);
                        padre = stackPadres.get(stackPadres.size() - 1);
                    } else {
                        padre.insertar_sig_hijo(n);
                    }
                }
            }
        }
        Arbol programa = new Arbol(raiz);
        return programa;
    }

    public void printAST(Nodo nodo, int nivel) {
        if (nodo == null) {
            return;
        }
        System.out.println(" ".repeat(nivel) + nodo.getValue());
        if (nodo.getHijos() != null) {
            for (Nodo i : nodo.getHijos()) {
                printAST(i, nivel + 1);
            }
        }
    }
}

