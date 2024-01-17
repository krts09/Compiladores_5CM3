//! No se encuenta en el analizador léxico
package mx.ipn.escom.k.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Main {
	static boolean existenErrores = false;

	public static void main(String[] args) throws IOException {
		ejecutarPrompt();
	}

	private static void ejecutarPrompt() throws IOException {
		InputStreamReader input = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(input);
		
		for(;;){
			System.out.println(">>> ");
			String linea = reader.readLine();
			if (linea == null) break;
			ejecutar(linea);
			existenErrores = false;
		}
	}

	private static void ejecutar(String source) {
		ScannerSintax scanner = new ScannerSintax(source);
		List<Token> tokens = scanner.scanTokens();

		/*for(Token token : tokens){
			System.out.println(token);
		}*/

		Parser parser = new AnaSinDesRec(tokens);
		parser.parse();
	}

	static void error(int linea, String mensaje){
		reportar(linea, "", mensaje);
	}

	private static void reportar(int linea, String donde, String mensaje){
		System.err.println("[linea " + linea + "] Error " + donde + ": " + mensaje);
		existenErrores = true;
	}
}
