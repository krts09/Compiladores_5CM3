//!Algo diferente al del Analizador Léxico, agregar función equals(), allá existe una variable llamada "literal", aquí una llamada "posición"
package mx.ipn.escom.k.tools;

public class Token {

    final TipoToken tipo;
    final String lexema;
    final Object literal;

    final int posicion;

    public Token(TipoToken tipo, String lexema, int posicion) {
        this.tipo = tipo;
        this.lexema = lexema;
        this.posicion = posicion;
        this.literal = null;
    }

    public Token(TipoToken tipo, String lexema, Object literal) {
        this.tipo = tipo;
        this.lexema = lexema;
        this.posicion = 0;
        this.literal = literal;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Token)) {
            return false;
        }

        return this.tipo == ((Token) o).tipo;
    }

    public TipoToken getTipo(){
        return this.tipo;
    }

    public Object getLiteral(){
        return this.literal;
    }

    public int getPosition(){
        return this.posicion;
    }

    public String toString(){
        return tipo + " " + lexema + " ";
    }
	
}
