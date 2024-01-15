import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

class Interprete {
    private boolean existenErrores;

    public Interprete() {
        this.existenErrores = false;
    }

    public void ejecutarArchivo(String archivo) {
        List<String> lineas = new ArrayList<>();
        try {
            File file = new File(archivo);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String linea = scanner.nextLine();
                lineas.add(linea);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ejecutar(lineas);
        if (existenErrores) {
            System.exit(0);
        }
    }

    public void ejecutarPrompt() {
        List<String> lineas = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                System.out.print(">>> ");
                String linea = scanner.nextLine();
                lineas.add(linea);
            } catch (Exception e) {
                break;
            }
            if (lineas.isEmpty()) {
                continue;
            }
            ejecutar(lineas);
            lineas.clear();
        }
        scanner.close();
    }

    public void error(int linea, String msj) {
        System.out.println("[line " + linea + "] | " + msj);
    }

    public void reportar(int linea, String donde, String mensaje) {
        // Implement reportar method
    }

    public void ejecutar(List<String> source) {
        Scanner scanner = new Scanner(source);
        List<String> tokens = scanner.ScanTokens();
        Parser parser = new Parser(tokens);
        parser.parse();

        Postfix postfix = new Postfix(tokens);
        List<String> postfija = postfix.convertir();

        GeneradorAST genAst = new GeneradorAST(postfija);
        Nodo programa = genAst.generarAST();
        programa.recorrer();
    }
}

public class Main {
    public static void main(String[] args) {
        Interprete interprete = new Interprete();
        init();
        if (args.length > 2) {
            System.out.println("Uso: interprete ");
            System.exit(0);
        }
        if (args.length == 2) {
            interprete.ejecutarArchivo(args[1]);
        } else {
            interprete.ejecutarPrompt();
        }
    }
}


