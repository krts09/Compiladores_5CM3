import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {
    private String source;
    private int linea;
    private List<Token> tokens;
    private Map<String, TokenType> palabras_reservadas;

    public Scanner(String source) {
        this.source = source;
        this.linea = 1;
        this.tokens = new ArrayList<>();
        this.palabras_reservadas = new HashMap<>();

        palabras_reservadas.put("and", TokenType.AND);
        palabras_reservadas.put("class", TokenType.CLASS);
        palabras_reservadas.put("also", TokenType.ALSO);
        palabras_reservadas.put("for", TokenType.FOR);
        palabras_reservadas.put("fun", TokenType.FUN);
        palabras_reservadas.put("if", TokenType.IF);
        palabras_reservadas.put("null", TokenType.NULL);
        palabras_reservadas.put("print", TokenType.PRINT);
        palabras_reservadas.put("return", TokenType.RETURN);
        palabras_reservadas.put("super", TokenType.SUPER);
        palabras_reservadas.put("this", TokenType.THIS);
        palabras_reservadas.put("true", TokenType.TRUE);
        palabras_reservadas.put("var", TokenType.VAR);
        palabras_reservadas.put("while", TokenType.WHILE);
        palabras_reservadas.put("else", TokenType.ELSE);
        palabras_reservadas.put("false", TokenType.FALSE);
        palabras_reservadas.put("or", TokenType.OR);
    }

    public List<Token> ScanTokens() {
        int estado = 0;
        String current = "";

        String[] lines = source.split("\n");
        for (String line : lines) {
            String line1 = clean(line);
            line1 += " ";

            for (char c : line1.toCharArray()) {
                switch (estado) {
                    case 0:
                        if (c == '<') {
                            estado = 1;
                        } else if (c == '=') {
                            estado = 2;
                        } else if (c == '>') {
                            estado = 3;
                        } else if (Character.isDigit(c)) {
                            current += c;
                            estado = 4;
                        } else if (Character.isLetter(c)) {
                            current += c;
                            estado = 5;
                        } else if (c == '/') {
                            estado = 6;
                        } else if (c == '{') {
                            tokens.add(new Token(TokenType.BRACKET_OPEN, "{", null, linea));
                            estado = 0;
                        } else if (c == '}') {
                            tokens.add(new Token(TokenType.BRACKET_CLOSE, "}", null, linea));
                            estado = 0;
                        } else if (c == '(') {
                            tokens.add(new Token(TokenType.PARENT_OPEN, "(", null, linea));
                            estado = 0;
                        } else if (c == ')') {
                            tokens.add(new Token(TokenType.PARENT_CLOSE, ")", null, linea));
                            estado = 0;
                        } else if (c == '+') {
                            tokens.add(new Token(TokenType.ADD, "+", null, linea));
                            estado = 0;
                        } else if (c == '-') {
                            tokens.add(new Token(TokenType.SUB, "-", null, linea));
                            estado = 0;
                        } else if (c == '*') {
                            tokens.add(new Token(TokenType.MULT, "*", null, linea));
                            estado = 0;
                        } else if (c == '!') {
                            estado = 8;
                        } else if (c == '"') {
                            current += c;
                            estado = 9;
                        } else if (c == ';') {
                            tokens.add(new Token(TokenType.SEMICOLON, ";", null, linea));
                            estado = 0;
                        } else if (c == ',') {
                            tokens.add(new Token(TokenType.COMMA, ",", null, linea));
                            estado = 0;
                        }
                        break;
                    case 1:
                        if (c == '=') {
                            tokens.add(new Token(TokenType.LESS_EQUAL, "<=", null, linea));
                            estado = 0;
                        } else {
                            tokens.add(new Token(TokenType.LESS_THAN, "<", null, linea));
                            estado = 0;
                        }
                        break;
                    case 2:
                        if (c == '=') {
                            tokens.add(new Token(TokenType.EQUAL, "==", null, linea));
                            estado = 0;
                        } else {
                            tokens.add(new Token(TokenType.ASIGNATION, "=", null, linea));
                            estado = 0;
                        }
                        break;
                    case 3:
                        if (c == '=') {
                            tokens.add(new Token(TokenType.GREAT_EQUAL, ">=", null, linea));
                            estado = 0;
                        } else {
                            tokens.add(new Token(TokenType.GREAT, ">", null, linea));
                            estado = 0;
                        }
                        break;
                    case 4:
                        if (Character.isDigit(c) || c == '.') {
                            current += c;
                        } else {
                            tokens.add(new Token(TokenType.NUMBER, current, Float.parseFloat(current), linea));
                            current = "";
                            estado = 0;
                        }
                        break;
                    case 5:
                        if (Character.isDigit(c) || Character.isLetter(c)) {
                            current += c;
                        } else {
                            if (palabras_reservadas.containsKey(current)) {
                                if (current.toLowerCase().equals("true")) {
                                    tokens.add(new Token(TokenType.TRUE, current, true, linea));
                                    current = "";
                                    estado = 0;
                                } else if (current.toLowerCase().equals("false")) {
                                    tokens.add(new Token(TokenType.FALSE, current, false, linea));
                                    current = "";
                                    estado = 0;
                                } else {
                                    tokens.add(new Token(palabras_reservadas.get(current), current, null, linea));
                                    current = "";
                                    estado = 0;
                                }
                            } else {
                                tokens.add(new Token(TokenType.IDENTIFIER, current, null, linea));
                                current = "";
                                estado = 0;
                            }
                        }
                        break;
                    case 6:
                        if (c == '/') {
                            estado = 7;
                        } else if (c == '*') {
                            estado = 11;
                        } else {
                            tokens.add(new Token(TokenType.DIAG, "/", null, linea));
                            estado = 0;
                        }
                        break;
                    case 7:
                        if (c == '\n') {
                            estado = 0;
                        } else {
                            estado = 7;
                        }
                        break;
                    case 8:
                        if (c == '=') {
                            tokens.add(new Token(TokenType.DIFERENT, "!=", null, linea));
                            estado = 0;
                        } else {
                            tokens.add(new Token(TokenType.NEGATION, "!", null, linea));
                            estado = 0;
                        }
                        break;
                    case 9:
                        if (c == '"') {
                            current += c;
                            tokens.add(new Token(TokenType.STRING, current, current.substring(1, current.length() - 1), linea));
                            current = "";
                            estado = 0;
                        } else {
                            current += c;
                        }
                        break;
                    case 11:
                        if (c == '*') {
                            estado = 12;
                        }
                        break;
                    case 12:
                        if (c == '/') {
                            estado = 0;
                        } else {
                            estado = 11;
                        }
                        break;
                }
            }
            linea++;
        }
        tokens.add(new Token(TokenType.EOF, null, null, linea - 1));
        return tokens;
    }

    private String clean(String cadena) {
        String[] simbolos = { "(", ")", "{", "}", "=", "<", ">", "!", "+", "-", ";", "*", "/" };
        String clean_str = "";
        String pattern = "\\/\\/.*|\\/\\*[\\s\\S]*?\\*\\/|([A-Za-z_][A-Za-z0-9_]*|\\d+(?:\\.\\d+)?|\\S)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(cadena);
        List<String> result = new ArrayList<>();
        while (m.find()) {
            result.add(m.group());
        }
        clean_str = String.join(" ", result);
        clean_str = clean_str.replace("/ *", "/*");
        clean_str = clean_str.replace("* /", "*/");
        clean_str = clean_str.replace("> =", ">=");
        clean_str = clean_str.replace("< =", "<=");
        clean_str = clean_str.replace("= =", "==");
        clean_str = clean_str.replace("! =", "!=");

        return clean_str + "\n";
    }
}

