import java.util.List;

import TokenType;
import Token;

public class Nodo {
    private Token value;
    private List<Nodo> hijos;

    public Nodo(Token value) {
        this.value = value;
        this.hijos = null;
    }

    public void insertar_hijo(Nodo n) {
        if (this.hijos == null) {
            this.hijos = new ArrayList<>();
            this.hijos.add(n);
        } else {
            this.hijos.add(0, n);
        }
    }

    public void insertar_sig_hijo(Nodo n) {
        if (this.hijos == null) {
            this.hijos = new ArrayList<>();
            this.hijos.add(n);
        } else {
            this.hijos.add(n);
        }
    }

    public void insertar_hijos(List<Nodo> nodosHijos) {
        if (this.hijos == null) {
            this.hijos = new ArrayList<>();
        }
        this.hijos.addAll(nodosHijos);
    }
}

