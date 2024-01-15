import java.util.ArrayList;
import java.util.List;

public class Arbol {
    private Nodo raiz;
    private Postfix poshelp;

    public Arbol(Nodo raiz) {
        this.raiz = raiz;
        this.poshelp = new Postfix(new ArrayList<>());
    }

    public void recorrer() {
        for (int index = 0; index < this.raiz.hijos.size(); index++) {
            Nodo n = this.raiz.hijos.get(index);
            TokenType t = n.value.type;
            switch (t) {
                case ADD, SUB, MULT, DIAG -> {
                    SolverAritmetico solver = new SolverAritmetico();
                    Object res = solver.resolver(n);
                    System.out.println(res);
                }
                case VAR -> solverVar(n);
                case IF -> resolverIf(n);
                case WHILE -> solverWhile(n);
                case FOR -> solverFor(n);
                case GREAT_EQUAL, EQUAL, GREAT, LESS_EQUAL, LESS_THAN, DIFERENT -> {
                    SolverRel solver = new SolverRel();
                    Object res = solver.resolver(n);
                    System.out.println(res);
                }
                case AND, OR -> {
                    SolverLogic solver = new SolverLogic();
                    Object res = solver.resolver(n);
                    System.out.println(res);
                }
                case PRINT -> {
                    Object res = solverPrint(n);
                    System.out.println(res);
                }
                case ASIGNATION -> solverAsig(n);
            }
        }
    }

    public void resolverIf(Nodo n) {
        if (n.hijos == null) {
            if (n.value.type == TokenType.NUMBER || n.value.type == TokenType.STRING) {
                return n.value.literal;
            } else if (n.value.type == TokenType.IDENTIFIER) {
                return ts.simbolos.obtener(n.value.lexeme);
            }
        }
        Nodo cond = n.hijos.get(0);
        boolean rcond = checkCond(cond);
        if (n.hijos.get(n.hijos.size() - 1).value.type == TokenType.ELSE) {
            boolean el = true;
            List<Nodo> body = n.hijos.subList(1, n.hijos.size() - 1);
            List<Nodo> ebody = n.hijos.get(n.hijos.size() - 1).hijos;
        } else {
            List<Nodo> body = n.hijos.subList(1, n.hijos.size());
            boolean el = false;
        }
        if (rcond) {
            Nodo raiz = new Nodo(new Token(TokenType.NULL, "", "", null));
            raiz.insertar_hijos(body);
            Arbol araux = new Arbol(raiz);
            araux.recorrer();
        } else {
            if (el) {
                Nodo raiz = new Nodo(new Token(TokenType.NULL, "", "", null));
                raiz.insertar_hijos(ebody);
                Arbol araux = new Arbol(raiz);
                araux.recorrer();
            } else {
                return;
            }
        }
    }

    public void solverWhile(Nodo n) {
        Nodo cond = n.hijos.get(0);
        List<Nodo> body = n.hijos.subList(1, n.hijos.size());
        while (checkCond(cond)) {
            Nodo raiz = new Nodo(new Token(TokenType.NULL, "", "", null));
            raiz.insertar_hijos(body);
            Arbol araux = new Arbol(raiz);
            araux.recorrer();
        }
    }

    public boolean checkCond(Nodo cond) {
        TokenType type = cond.value.type;
        switch (type) {
            case GREAT_EQUAL, EQUAL, GREAT, LESS_EQUAL, LESS_THAN, DIFERENT -> {
                SolverRel solver = new SolverRel();
                boolean rcond = solver.resolver(cond);
                return rcond;
            }
            case AND, OR -> {
                SolverLogic solver = new SolverLogic();
                boolean rcond = solver.resolver(cond);
                return rcond;
            }
            case TRUE -> {
                boolean rcond = true;
                return rcond;
            }
            case FALSE -> {
                boolean rcond = false;
                return rcond;
            }
            case IDENTIFIER -> {
                if (ts.simbolos.existeIdentificador(cond.value.lexeme)) {
                    if (ts.simbolos.obtener(cond.value.lexeme) instanceof Boolean) {
                        boolean rcond = (boolean) ts.simbolos.obtener(cond.value.lexeme);
                        return rcond;
                    } else {
                        System.out.println("Error la variable evaluada no es un boleano.\n");
                        System.exit(0);
                    }
                } else {
                    System.out.println("Error: La variable " + cond.value.lexeme + " no existe.\n");
                    System.exit(0);
                }
            }
            default -> {
                System.out.println("Error: El resultado debe ser un boleano");
                System.exit(0);
            }
        }
    }

    public void solverFor(Nodo n) {
        Nodo ini = n.hijos.get(0);
        Nodo cond = n.hijos.get(1);
        Nodo increase = n.hijos.get(2);
        List<Nodo> body = n.hijos.subList(3, n.hijos.size());
        solverVar(n.hijos.get(0));
        while (checkCond(cond)) {
            Nodo raiz = new Nodo(new Token(TokenType.NULL, "", "", null));
            raiz.insertar_hijos(body);
            Arbol araux = new Arbol(raiz);
            araux.recorrer();
            solverAsig(increase);
        }
    }

    public void solverVar(Nodo n) {
        if (n.hijos.size() == 1) {
            if (ts.simbolos.existeIdentificador(n.hijos.get(0).value.lexeme)) {
                System.out.println("Error: La variable " + n.hijos.get(0).value.lexeme + " ya existe");
                return;
            }
            ts.simbolos.asignar(n.hijos.get(0).value.lexeme, null);
            return;
        } else if (n.hijos.size() == 2) {
            if (ts.simbolos.existeIdentificador(n.hijos.get(0).value.lexeme)) {
                System.out.println("Error: La variable " + n.hijos.get(0).value.lexeme + " ya existe");
                return;
            } else {
                String key = n.hijos.get(0).value.lexeme;
            }
            if (poshelp.esOperador(n.hijos.get(1).value.type)) {
                TokenType type = n.hijos.get(1).value.type;
                Object value;
                switch (type) {
                    case ADD, SUB, MULT, DIAG -> {
                        SolverAritmetico solver = new SolverAritmetico();
                        value = solver.resolver(n.hijos.get(1));
                    }
                    case GREAT_EQUAL, EQUAL, GREAT, LESS_EQUAL, LESS_THAN, DIFERENT -> {
                        SolverRel solver = new SolverRel();
                        value = solver.resolver(n.hijos.get(1));
                    }
                    case AND, OR -> {
                        SolverLogic solver = new SolverLogic();
                        value = solver.resolver(n.hijos.get(1));
                    }
                }
            } else if (n.hijos.get(1).value.type == TokenType.IDENTIFIER) {
                if (ts.simbolos.existeIdentificador(n.hijos.get(1).value.lexeme)) {
                    Object value = ts.simbolos.obtener(n.hijos.get(1).value.lexeme);
                    return;
                } else {
                    System.out.println("Error: La variable " + n.hijos.get(1).value.lexeme + " no existe, por lo cual no puede ser asignada a " + n.hijos.get(0).value.lexeme + "\n");
                    System.exit(0);
                }
            } else {
                Object value = n.hijos.get(1).value.literal;
            }
            ts.simbolos.asignar(key, value);
            return;
        } else {
            System.out.println("Error al declarar la variable");
            return;
        }
    }

    public void solverAsig(Nodo n) {
        if (ts.simbolos.existeIdentificador(n.hijos.get(0).value.lexeme)) {
            if (n.hijos.get(0).value.type == TokenType.IDENTIFIER) {
                if (poshelp.esOperador(n.hijos.get(1).value.type)) {
                    TokenType type = n.hijos.get(1).value.type;
                    Object value;
                    switch (type) {
                        case ADD, SUB, MULT, DIAG -> {
                            SolverAritmetico solver = new SolverAritmetico();
                            value = solver.resolver(n.hijos.get(1));
                        }
                        case AND, OR -> {
                            SolverLogic solver = new SolverLogic();
                            value = solver.resolver(n.hijos.get(1));
                        }
                        case GREAT_EQUAL, EQUAL, GREAT, LESS_EQUAL, LESS_THAN, DIFERENT -> {
                            SolverRel solver = new SolverRel();
                            value = solver.resolver(n.hijos.get(1));
                        }
                    }
                    ts.simbolos.reasig(n.hijos.get(0).value.lexeme, value);
                    return;
                } else {
                    ts.simbolos.reasig(n.hijos.get(0).value.lexeme, n.hijos.get(1).value.literal);
                    return;
                }
            }
        } else {
            System.out.println("Error: La variable " + n.hijos.get(0).value.lexeme + " no existe");
            return;
        }
    }

    public Object solverPrint(Nodo n) {
        if (n.hijos == null) {
            if (n.value.type == TokenType.NUMBER || n.value.type == TokenType.STRING) {
                return n.value.literal;
            } else if (n.value.type == TokenType.IDENTIFIER) {
                return ts.simbolos.obtener(n.value.lexeme);
            }
        }
        Nodo hijo = n.hijos.get(0);
        if (poshelp.esOperador(hijo.value.type)) {
            TokenType type = hijo.value.type;
            Object res;
            switch (type) {
                case ADD, SUB, MULT, DIAG -> {
                    SolverAritmetico solver = new SolverAritmetico();
                    res = solver.resolver(hijo);
                    return res;
                }
                case AND, OR -> {
                    SolverLogic solver = new SolverLogic();
                    res = solver.resolver(hijo);
                    return res;
                }
                case GREAT_EQUAL, EQUAL, GREAT, LESS_EQUAL, LESS_THAN, DIFERENT -> {
                    SolverRel solver = new SolverRel();
                    res = solver.resolver(hijo);
                    return res;
                }
            }
        } else {
            Object valor = solverPrint(hijo);
            return valor;
        }
    }
}

