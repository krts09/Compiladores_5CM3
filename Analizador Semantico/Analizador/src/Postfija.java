import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Postfix {
    private List<Token> infija;
    private Stack<Token> stackk;
    private List<Token> postfija;
    private Map<String, TokenType> palabras_reservadas;

    public Postfix(List<Token> tokens) {
        this.infija = tokens;
        this.stackk = new Stack<>();
        this.postfija = new ArrayList<>();
        this.palabras_reservadas = new HashMap<>();

        this.palabras_reservadas.put("class", TokenType.CLASS);
        this.palabras_reservadas.put("also", TokenType.ALSO);
        this.palabras_reservadas.put("for", TokenType.FOR);
        this.palabras_reservadas.put("fun", TokenType.FUN);
        this.palabras_reservadas.put("if", TokenType.IF);
        this.palabras_reservadas.put("null", TokenType.NULL);
        this.palabras_reservadas.put("print", TokenType.PRINT);
        this.palabras_reservadas.put("return", TokenType.RETURN);
        this.palabras_reservadas.put("super", TokenType.SUPER);
        this.palabras_reservadas.put("this", TokenType.THIS);
        this.palabras_reservadas.put("var", TokenType.VAR);
        this.palabras_reservadas.put("while", TokenType.WHILE);
        this.palabras_reservadas.put("else", TokenType.ELSE);
    }

    public List<Token> convertir() {
        boolean estructuraDeControl = false;
        Stack<Token> stack_Estruc = new Stack<>();
        int auxfor = 0;
        for (int index = 0; index < this.infija.size(); index++) {
            Token t = this.infija.get(index);
            if (t.getType() == TokenType.EOF) {
                break;
            }
            if (this.palabras_reservadas.containsValue(t.getType())) {
                this.postfija.add(t);
                if (this.esEstructuraDeControl(t.getType())) {
                    estructuraDeControl = true;
                    stack_Estruc.push(t);
                }
            } else if (this.esOperando(t.getType())) {
                this.postfija.add(t);
            } else if (t.getType() == TokenType.PARENT_OPEN) {
                this.stackk.push(t);
            } else if (t.getType() == TokenType.PARENT_CLOSE) {
                while (!this.stackk.isEmpty() && this.stackk.peek().getType() != TokenType.PARENT_OPEN) {
                    Token temp = this.stackk.pop();
                    this.postfija.add(temp);
                }
                if (!this.stackk.isEmpty() && this.stackk.peek().getType() == TokenType.PARENT_OPEN) {
                    this.stackk.pop();
                }
                if (estructuraDeControl) {
                    this.postfija.add(new Token(TokenType.SEMICOLON, ";", ";", null));
                }
            } else if (this.esOperador(t.getType())) {
                while (!this.stackk.isEmpty() && this.precedenciaMayorIgual(this.stackk.peek().getType(), t.getType())) {
                    Token temp = this.stackk.pop();
                    this.postfija.add(temp);
                }
                this.stackk.push(t);
            } else if (t.getType() == TokenType.SEMICOLON) {
                while (!this.stackk.isEmpty() && this.stackk.peek().getType() != TokenType.BRACKET_OPEN) {
                    if (estructuraDeControl) {
                        if (stack_Estruc.peek().getType() == TokenType.FOR) {
                            Token temp = this.stackk.pop();
                            this.postfija.add(temp);
                            break;
                        }
                        continue;
                    }
                    Token temp = this.stackk.pop();
                    this.postfija.add(temp);
                }
                this.postfija.add(t);
            } else if (t.getType() == TokenType.BRACKET_OPEN) {
                this.stackk.push(t);
            } else if (t.getType() == TokenType.BRACKET_CLOSE && estructuraDeControl) {
                if (this.infija.get(index + 1).getType() == TokenType.ELSE) {
                    this.stackk.pop();
                } else {
                    this.stackk.pop();
                    this.postfija.add(new Token(TokenType.SEMICOLON, ";", ";", null));
                    stack_Estruc.pop();
                    if (stack_Estruc.isEmpty()) {
                        estructuraDeControl = false;
                    }
                }
            }
        }
        while (!this.stackk.isEmpty()) {
            Token temp = this.stackk.pop();
            this.postfija.add(temp);
        }
        while (!stack_Estruc.isEmpty()) {
            stack_Estruc.pop();
            this.postfija.add(new Token(TokenType.SEMICOLON, ";", ";", null));
        }
        return this.postfija;
    }

    private boolean esOperando(TokenType tipo) {
        switch (tipo) {
            case IDENTIFIER:
                return true;
            case NUMBER:
            case STRING:
            case TRUE:
            case FALSE:
                return true;
            default:
                return false;
        }
    }

    private boolean esEstructuraDeControl(TokenType tipo) {
        switch (tipo) {
            case IF:
            case ELSE:
                return true;
            case WHILE:
            case FOR:
                return true;
            default:
                return false;
        }
    }

    private boolean esOperador(TokenType tipo) {
        switch (tipo) {
            case ADD:
            case SUB:
            case MULT:
            case DIAG:
                return true;
            case EQUAL:
            case DIFERENT:
            case GREAT:
            case GREAT_EQUAL:
                return true;
            case AND:
            case OR:
            case ASIGNATION:
                return true;
            case LESS_THAN:
            case LESS_EQUAL:
                return true;
            default:
                return false;
        }
    }

    private boolean precedenciaMayorIgual(TokenType tipo1, TokenType tipo2) {
        return this.obtenerPrecedencia(tipo1) >= this.obtenerPrecedencia(tipo2);
    }

    private int obtenerPrecedencia(TokenType tipo) {
        switch (tipo) {
            case MULT:
            case DIAG:
                return 7;
            case ADD:
            case SUB:
                return 6;
            case GREAT_EQUAL:
            case GREAT:
            case LESS_THAN:
            case LESS_EQUAL:
                return 5;
            case DIFERENT:
            case EQUAL:
                return 4;
            case AND:
                return 3;
            case OR:
                return 2;
            case ASIGNATION:
                return 1;
            default:
                return 0;
        }
    }

    private int obAridad(TokenType tipo) {
        switch (tipo) {
            case MULT:
            case DIAG:
            case SUB:
            case ADD:
            case EQUAL:
            case GREAT:
            case GREAT_EQUAL:
            case ASIGNATION:
                return 2;
            case LESS_THAN:
            case LESS_EQUAL:
            case DIFERENT:
            case AND:
            case OR:
                return 2;
            default:
                return 0;
        }
    }
}

