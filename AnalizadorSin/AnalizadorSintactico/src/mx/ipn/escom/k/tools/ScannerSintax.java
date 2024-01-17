//!Igual al del Analizador Léxico, solo debe agregarse la función scanTokens()
package mx.ipn.escom.k.tools;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScannerSintax {
	private final String source;

	private final List<Token> tokens = new ArrayList<>();

	private static final Map<String, TipoToken> keywords;
	static {
        keywords = new HashMap<>();
        keywords.put("and",    TipoToken.AND);
        keywords.put("else",   TipoToken.ELSE);
        keywords.put("false",  TipoToken.FALSE);
        keywords.put("for",    TipoToken.FOR);
        keywords.put("fun",    TipoToken.FUN);
        keywords.put("if",     TipoToken.IF);
        keywords.put("null",   TipoToken.NULL);
        keywords.put("or",     TipoToken.OR);
        keywords.put("print",  TipoToken.PRINT);
        keywords.put("return", TipoToken.RETURN);
        keywords.put("true",   TipoToken.TRUE);
        keywords.put("var",    TipoToken.VAR);
        keywords.put("while",  TipoToken.WHILE);
    }

	ScannerSintax (String source) {	
		this.source = source + " "; 
	}

	List <Token> scanTokens() {
		int estado = 0;
		char caracter = 0;
		String lexema = "";
		int inicioLexema = 0;

		for(int i=0; i<source.length(); i++){
			caracter = source.charAt(i);

			switch (estado){
				case 0:
					if(caracter == '*'){
						tokens.add(new Token(TipoToken.STAR, "*", i + 1));
					}
					else if(caracter == ','){
						tokens.add(new Token(TipoToken.COMMA, ",", i + 1));
					}
					else if(caracter == '.'){
						tokens.add(new Token(TipoToken.DOT, ".", i + 1));
					}
					else if(Character.isAlphabetic(caracter)){
						estado = 1;
						lexema = lexema + caracter;
						inicioLexema = i;
					}
					break;

				case 1:
					if(Character.isAlphabetic(caracter) || Character.isDigit(caracter) ){
						lexema = lexema + caracter;
					}
					else{
						TipoToken tt = keywords.get(lexema);
						if(tt == null){
							tokens.add(new Token(TipoToken.IDENTIFIER, lexema, inicioLexema + 1));
						}
						else{
							tokens.add(new Token(tt, lexema, inicioLexema + 1));
						}

						estado = 0;
						i--;
						lexema = "";
						inicioLexema = 0;
					}
					break;
			}
		}
		tokens.add(new Token(TipoToken.EOF, "", source.length()));

		return tokens;
	}
}
