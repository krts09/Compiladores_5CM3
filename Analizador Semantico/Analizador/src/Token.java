public class Token {

    final Tokentype type;
    final String lexeme;
    final Object literal;
    final Object line;

    public Token(Tokentype tipo, String lexema) {
        this.type = tipo;
        this.lexeme = lexema;
        this.literal = literal;
        this.line = linea;
    }

    public Token(Tokentype tipo, String lexema, Object literal) {
        this.type = tipo;
        this.lexeme = lexema;
        this.literal = literal;
    }

    public String toString() {
        return "<" + type + " " + lexeme + " " + literal +  "" + line + "">";
        
    }
}

