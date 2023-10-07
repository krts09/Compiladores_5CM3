import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;
    private boolean inComment = false;

    private static final Map<String, TipoToken> keywords = new HashMap<>();
    static {
        keywords.put("and", TipoToken.AND);
        keywords.put("else", TipoToken.ELSE);
        keywords.put("false", TipoToken.FALSE);
        keywords.put("for", TipoToken.FOR);
        keywords.put("fun", TipoToken.FUN);
        keywords.put("if", TipoToken.IF);
        keywords.put("null", TipoToken.NULL);
        keywords.put("or", TipoToken.OR);
        keywords.put("print", TipoToken.PRINT);
        keywords.put("return", TipoToken.RETURN);
        keywords.put("true", TipoToken.TRUE);
        keywords.put("var", TipoToken.VAR);
        keywords.put("while", TipoToken.WHILE);
    }

    public Scanner(String source) {
        this.source = source;
    }

    public List<Token> scan() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        if (!inComment) {
            tokens.add(new Token(TipoToken.EOF, "", null));
        }

        return tokens;
    }

    private void scanToken() {
        char c = advance();

        switch (c) {
            case '(':
                addToken(TipoToken.LEFT_PAREN);
                break;
            case ')':
                addToken(TipoToken.RIGHT_PAREN);
                break;
            case '{':
                addToken(TipoToken.LEFT_BRACE);
                break;
            case '}':
                addToken(TipoToken.RIGHT_BRACE);
                break;
            case ',':
                addToken(TipoToken.COMMA);
                break;
            case '.':
                addToken(TipoToken.DOT);
                break;
            case '-':
                addToken(TipoToken.MINUS);
                break;
            case '+':
                addToken(TipoToken.PLUS);
                break;
            case ';':
                addToken(TipoToken.SEMICOLON);
                break;
            case '*':
                addToken(TipoToken.STAR);
                break;
            case '!':
                addToken(match('=') ? TipoToken.BANG_EQUAL : TipoToken.BANG);
                break;
            case '=':
                addToken(match('=') ? TipoToken.EQUAL_EQUAL : TipoToken.EQUAL);
                break;
            case '<':
                addToken(match('=') ? TipoToken.LESS_EQUAL : TipoToken.LESS);
                break;
            case '>':
                addToken(match('=') ? TipoToken.GREATER_EQUAL : TipoToken.GREATER);
                break;
            case '/':
                if (match('/')) {
                    // Comentario de una línea, ignorar el resto de la línea
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else if (match('*')) {
                    // Comentario multilineal
                    inComment = true;
                    scanMultilineComment();
                } else {
                    addToken(TipoToken.SLASH);
                }
                break;
            case ' ':
            case '\r':
            case '\t':
                // Ignorar espacios en blanco
                break;
            case '\n':
                line++;
                break;
            case '"':
                scanString();
                break;
            default:
                if (isDigit(c)) {
                    scanNumber();
                } else if (isAlpha(c)) {
                    scanIdentifier();
                } else {
                    // Token no reconocido, generar error
                    Interprete.error(line, "Token no reconocido.");
                }
                break;
        }
    }

    private void scanMultilineComment() {
        while (!isAtEnd()) {
            char c = advance();
            if (c == '*' && peek() == '/') {
                // Fin del comentario multilineal
                inComment = false;
                advance(); // Consumir '/'
                return;
            } else if (c == '\n') {
                line++;
            }
        }

        // Error: comentario multilineal no cerrado
        Interprete.error(line, "Comentario multilineal no cerrado.");
    }

    private void scanString() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }

        if (isAtEnd()) {
            Interprete.error(line, "Cadena no cerrada.");
            return;
        }

        // Consumir el cierre de la cadena
        advance();

        // Extraer el valor de la cadena sin las comillas
        String value = source.substring(start + 1, current - 1);
        addToken(TipoToken.STRING, value);
    }

    private void scanNumber() {
        while (isDigit(peek())) advance();

        // Verificar si hay parte decimal
        if (peek() == '.' && isDigit(peekNext())) {
            advance(); // Consumir el punto decimal

            while (isDigit(peek())) advance();
        }

        // Convertir la cadena numérica a un número en punto flotante
        double value = Double.parseDouble(source.substring(start, current));
        addToken(TipoToken.NUMBER, value);
    }

    private void scanIdentifier() {
        while (isAlphaNumeric(peek())) advance();

        String text = source.substring(start, current);
        TipoToken type = keywords.get(text);
        if (type == null) {
            type = TipoToken.IDENTIFIER;
        }
        addToken(type);
    }

    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        current++;
        return true;
    }

    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private char advance() {
        return source.charAt(current++);
    }

    private void addToken(TipoToken tipo) {
        addToken(tipo, null);
    }

    private void addToken(TipoToken tipo, Object literal) {
        String lexeme = source.substring(start, current);
        tokens.add(new Token(tipo, lexeme, literal));
    }
}
