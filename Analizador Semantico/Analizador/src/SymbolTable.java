import java.util.HashMap;
import java.util.Map;

public class TSimbolos {
    private Map<String, Object> valores;

    public TSimbolos() {
        this.valores = new HashMap<>();
    }

    public boolean existeIdentificador(String key) {
        return valores.containsKey(key);
    }

    public Object obtener(String key) {
        if (existeIdentificador(key)) {
            return valores.get(key);
        } else {
            System.out.println("Variable " + key + " no definida");
            System.exit(1);
            return null;
        }
    }

    public void asignar(String key, Object value) {
        if (existeIdentificador(key)) {
            valores.put(key, value);
        } else {
            valores.put(key, value);
        }
    }

    public void reasig(String key, Object value) {
        valores.put(key, value);
    }
}

public class Main {
    private static TSimbolos simbolos;

    public static void main(String[] args) {
        init();
    }

    public static void init() {
        simbolos = new TSimbolos();
    }
}

