//! No se encuenta en el analizador léxico, preguntar sobre el método FUNCTIONS
package mx.ipn.escom.k.tools;

import java.util.List;

public class AnaSinDesRec implements Parser{
	private int i = 0;
	private boolean error = false;
	private Token preanalisis;
	private final List<Token> tokens;

	public AnaSinDesRec(List<Token> tokens){
		this.tokens = tokens;
		preanalisis = this.tokens.get(i);
	}

	@Override
	public boolean parse() {
		Program();

		if (preanalisis.tipo == TipoToken.EOF && !error) {
			System.out.println("Compilación terminada con éxito");
			return true;
		} else {
			System.out.println("Compilación terminada con errores");
		}
		return false;
	}
//todo agregar qué se esperaba en los errores (ej: se esperaba un número, se esperaba un identificador, etc)
//+GRAMÁTICA
	// Program -> Declaration
	private void Program(){
		Declaration();
	}

//+DECLARACIONES
	// Declaration -> Fun_Declaration Declaration| Var_Declaration Declaration | Statement Declaration | void
	private void Declaration(){
		if(error)
			return;
		
		if(preanalisis.tipo == TipoToken.FUN){
			Fun_Declaration();
			Declaration();
		}
		else if(preanalisis.tipo == TipoToken.VAR){
			Var_Declaration();
			Declaration();
		}
		else if(preanalisis.tipo == TipoToken.IF || preanalisis.tipo == TipoToken.WHILE || preanalisis.tipo == TipoToken.RETURN || preanalisis.tipo == TipoToken.PRINT || preanalisis.tipo == TipoToken.LEFT_BRACE){ //¿se debe replantear esta condición?
			Statement();
			Declaration();
		}
		else if(preanalisis.tipo == TipoToken.EOF){
			// void
		}
		else{
			error = true;
			System.out.println("Error encontrado");
		}
	}

	//Fun_Declaration -> fun FUNCTION
	private void Fun_Declaration(){
		if(error)
			return;

		match(TipoToken.FUN);
		FUNCTION();
	}

	//Var_Declaration -> var IDENTIFIER Var_Init ;
	private void Var_Declaration(){
		if(error)
			return;

		match(TipoToken.VAR);
		match(TipoToken.IDENTIFIER);
		Var_Init();
		match(TipoToken.SEMICOLON);
	}

	//Var_Init -> = EXPRESSION | void
	private void Var_Init(){
		if(error)
			return;
			
		if(preanalisis.tipo == TipoToken.EQUAL){
			match(TipoToken.EQUAL);
			EXPRESSION();
		}
		else if(preanalisis.tipo == TipoToken.SEMICOLON){
			// void
		}
		else{
			error = true;
			System.out.println("Error encontrado");
		}
	}

//+SENTENCIAS
	//Statement_Declaration -> Expr_STMT | For_STMT | If_STMT | Print_STMT | Return_STMT | While_STMT | Block
	private void Statement(){
		if (error)
			return;
			
		if (preanalisis.tipo == TipoToken.FOR){
			For_STMT();
		}
		else if (preanalisis.tipo == TipoToken.IF){
			If_STMT();
		}
		else if (preanalisis.tipo == TipoToken.PRINT){
			Print_STMT();
		}
		else if (preanalisis.tipo == TipoToken.RETURN){
			Return_STMT();
		}
		else if (preanalisis.tipo == TipoToken.WHILE){
			While_STMT();
		}
		else if (preanalisis.tipo == TipoToken.LEFT_BRACE){
			Block();
		}
		else{
			Expr_STMT();
		}
	}

	//Expr_STMT -> EXPRESSION ;
	private void Expr_STMT(){
		if(error)
			return;

		EXPRESSION();
		match(TipoToken.SEMICOLON);
	}

	//For_STMT -> for ( For_STMT_1 Expr_STMT_2 Expr_STMT_3 ) Statement
	private void For_STMT(){
		if(error)
			return;
			
		match(TipoToken.FOR);
		match(TipoToken.LEFT_PAREN);
		For_STMT_1();
		Expr_STMT_2();
		Expr_STMT_3();
		match(TipoToken.RIGHT_PAREN);
		Statement();
	}

	//For_STMT_1 -> Var_Declaration | Expr_STMT | ;
	private void For_STMT_1(){
		if(error)
			return;

		if(preanalisis.tipo == TipoToken.VAR){
			Var_Declaration();
		}
		else if(preanalisis.tipo == TipoToken.SEMICOLON){
			// void
		}
		else{
			Expr_STMT();
		}
	}

	//Expr_STMT_2 -> EXPRESSION; | ;
	private void Expr_STMT_2(){
		if(error)
			return;
		
		if(preanalisis.tipo == TipoToken.SEMICOLON){
			// void
		}
		else{
			EXPRESSION();
			match(TipoToken.SEMICOLON);
		}
	}

	//Expr_STMT_3 -> EXPRESSION | void
	private void Expr_STMT_3(){
		if(error)
			return;
		
		if(preanalisis.tipo == TipoToken.RIGHT_PAREN){
			// void
		}
		else{
			EXPRESSION();
		}
	}

	//If_STMT -> if ( EXPRESSION ) Statement Else_STMT
	private void If_STMT(){
		if(error)
			return;
		
		match(TipoToken.IF);
		match(TipoToken.LEFT_PAREN);
		EXPRESSION();
		match(TipoToken.RIGHT_PAREN);
		Statement();
		Else_STMT();
	}

	//ELSE_STMT -> else Statement | void
	private void Else_STMT(){
		if(error)
			return;
		
		if(preanalisis.tipo == TipoToken.ELSE){
			match(TipoToken.ELSE);
			Statement();
		}
		else if(preanalisis.tipo == TipoToken.EOF || preanalisis.tipo == TipoToken.RIGHT_BRACE){
			// void
		}
		else{
			error = true;
			System.out.println("Error encontrado");
		}
	}

	//Print_STMT -> print EXPRESSION ;
	private void Print_STMT(){
		if(error)
			return;
		
		match(TipoToken.PRINT);
		EXPRESSION();
		match(TipoToken.SEMICOLON);
	}

	//Return_STMT -> return Return_Exp_Opc ;
	private void Return_STMT(){
		if(error)
			return;
		
		match(TipoToken.RETURN);
		Return_Exp_Opc();
		match(TipoToken.SEMICOLON);
	}
	
	//Return_Exp_Opc -> EXPRESSION | void
	private void Return_Exp_Opc(){
		if(error)
			return;
		
		if(preanalisis.tipo == TipoToken.SEMICOLON){
			// void
		}
		else{
			EXPRESSION();
		}
	}

	//While_STMT -> while ( EXPRESSION ) Statement
	private void While_STMT(){
		if(error)
			return;
		
		match(TipoToken.WHILE);
		match(TipoToken.LEFT_PAREN);
		EXPRESSION();
		match(TipoToken.RIGHT_PAREN);
		Statement();
	}

	//Block -> { DECLARATION }
	private void Block(){
		if(error)
			return;
		
		match(TipoToken.LEFT_BRACE);
		Declaration();
		match(TipoToken.RIGHT_BRACE);
	}


//+EXPRESIONES
	//EXPRESSION -> ASSIGNMENT
	private void EXPRESSION(){
		if(error)
			return;
		
		ASSIGNMENT();
	}

	//ASSIGNMENT -> LOGIC_OR ASSIGNMENT_Opc
	private void ASSIGNMENT(){
		if(error)
			return;
		
		LOGIC_OR();
		ASSIGNMENT_Opc();
	}

	//ASSIGNMENT_Opc -> = EXPRESSION | void
	private void ASSIGNMENT_Opc(){
		if(error)
			return;
		
		if(preanalisis.tipo == TipoToken.EQUAL){
			match(TipoToken.EQUAL);
			EXPRESSION();
		}
		else if(preanalisis.tipo == TipoToken.SEMICOLON || preanalisis.tipo == TipoToken.RIGHT_PAREN){
			// void
		}
		else{
			error = true;
			System.out.println("Error encontrado");
		}
	}

	//LOGIC_OR -> LOGIC_AND LOGIC_OR_2
	private void LOGIC_OR(){
		if(error)
			return;
		
		LOGIC_AND();
		LOGIC_OR_2();
	}

	//LOGIC_OR_2 -> or LOGIC_AND LOGIC_OR_2 | void
	private void LOGIC_OR_2(){
		if(error)
			return;
		
		if(preanalisis.tipo == TipoToken.OR){
			match(TipoToken.OR);
			LOGIC_AND();
			LOGIC_OR_2();
		}
		else if(preanalisis.tipo == TipoToken.SEMICOLON || preanalisis.tipo == TipoToken.RIGHT_PAREN){
			// void
		}
		else{
			error = true;
			System.out.println("Error encontrado");
		}
	}

	//LOGIC_AND -> EQUALITY LOGIC_AND_2
	private void LOGIC_AND(){
		if(error)
			return;
		
		EQUALITY();
		LOGIC_AND_2();
	}

	//LOGIC_AND_2 -> and EQUALITY LOGIC_AND_2 | void
	private void LOGIC_AND_2(){
		if(error)
			return;
		
		if(preanalisis.tipo == TipoToken.AND){
			match(TipoToken.AND);
			EQUALITY();
			LOGIC_AND_2();
		}
		else if(preanalisis.tipo == TipoToken.SEMICOLON || preanalisis.tipo == TipoToken.RIGHT_PAREN){
			// void
		}
		else{
			error = true;
			System.out.println("Error encontrado");
		}
	}

	//EQUALITY -> COMPARISON EQUALITY_2
	private void EQUALITY(){
		if(error)
			return;
		
		COMPARISON();
		EQUALITY_2();
	}

	//EQUALITY_2 -> != COMPARISON EQUALITY_2 | == COMPARISON EQUALITY_2 | void
	private void EQUALITY_2(){
		if(error)
			return;
		
		if(preanalisis.tipo == TipoToken.BANG_EQUAL){
			match(TipoToken.BANG_EQUAL);
			COMPARISON();
			EQUALITY_2();
		}
		else if(preanalisis.tipo == TipoToken.EQUAL_EQUAL){
			match(TipoToken.EQUAL_EQUAL);
			COMPARISON();
			EQUALITY_2();
		}
		else if(preanalisis.tipo == TipoToken.SEMICOLON || preanalisis.tipo == TipoToken.RIGHT_PAREN){ //¿se debe replantear esta condición?
			// void
		}
		else{
			error = true;
			System.out.println("Error encontrado");
		}
	}

	//COMPARISON -> TERM COMPARISON_2
	private void COMPARISON(){
		if(error)
			return;
		
		TERM();
		COMPARISON_2();
	}

	//COMPARISON_2 -> > TERM COMPARISON_2 | >= TERM COMPARISON_2 | < TERM COMPARISON_2 | <= TERM COMPARISON_2 | void
	private void COMPARISON_2(){
		if(error)
			return;
		
		if(preanalisis.tipo == TipoToken.GREATER){
			match(TipoToken.GREATER);
			TERM();
			COMPARISON_2();
		}
		else if(preanalisis.tipo == TipoToken.GREATER_EQUAL){
			match(TipoToken.GREATER_EQUAL);
			TERM();
			COMPARISON_2();
		}
		else if(preanalisis.tipo == TipoToken.LESS){
			match(TipoToken.LESS);
			TERM();
			COMPARISON_2();
		}
		else if(preanalisis.tipo == TipoToken.LESS_EQUAL){
			match(TipoToken.LESS_EQUAL);
			TERM();
			COMPARISON_2();
		}
		/* else if (preanalisis.tipo == TipoToken.SEMICOLON || preanalisis.tipo == TipoToken.RIGHT_PAREN || preanalisis.tipo == TipoToken.AND || preanalisis.tipo == TipoToken.OR){ //¿se debe replantear esta condición?
			// void
		} */
		else if(preanalisis.tipo == TipoToken.SEMICOLON || preanalisis.tipo == TipoToken.RIGHT_PAREN ){ //¿se debe replantear esta condición?
			// void
		}
		else{
			error = true;
			System.out.println("Error encontrado");
		}
	}

	//TERM -> FACTOR TERM_2
	private void TERM(){
		if(error)
			return;
		
		FACTOR();
		TERM_2();
	}

	//TERM_2 -> - FACTOR TERM_2 | + FACTOR TERM_2 | void
	private void TERM_2(){
		if(error)
			return;
		
		if(preanalisis.tipo == TipoToken.MINUS){
			match(TipoToken.MINUS);
			FACTOR();
			TERM_2();
		}
		else if(preanalisis.tipo == TipoToken.PLUS){
			match(TipoToken.PLUS);
			FACTOR();
			TERM_2();
		}
		else if(preanalisis.tipo == TipoToken.SEMICOLON || preanalisis.tipo == TipoToken.RIGHT_PAREN ){ //¿se debe replantear esta condición?
			// void
		}
		/* else if(preanalisis.tipo == TipoToken.SEMICOLON || preanalisis.tipo == TipoToken.RIGHT_PAREN || preanalisis.tipo == TipoToken.GREATER || preanalisis.tipo == TipoToken.GREATER_EQUAL || preanalisis.tipo == TipoToken.LESS || preanalisis.tipo == TipoToken.LESS_EQUAL || preanalisis.tipo == TipoToken.BANG_EQUAL || preanalisis.tipo == TipoToken.EQUAL_EQUAL || preanalisis.tipo == TipoToken.AND || preanalisis.tipo == TipoToken.OR){ //¿se debe replantear esta condición?
			// void
		} */
		else{
			error = true;
			System.out.println("Error encontrado");
		}
	}

	//FACTOR -> UNARY FACTOR_2
	private void FACTOR(){
		if(error)
			return;
		
		UNARY();
		FACTOR_2();
	}

	//FACTOR_2 -> / UNARY FACTOR_2 | * UNARY FACTOR_2 | void
	private void FACTOR_2(){
		if(error)
			return;
		
		if(preanalisis.tipo == TipoToken.SLASH){
			match(TipoToken.SLASH);
			UNARY();
			FACTOR_2();
		}
		else if(preanalisis.tipo == TipoToken.STAR){
			match(TipoToken.STAR);
			UNARY();
			FACTOR_2();
		}
		else if(preanalisis.tipo == TipoToken.SEMICOLON || preanalisis.tipo == TipoToken.RIGHT_PAREN ){ //¿se debe replantear esta condición?
			// void
		}
		else{
			error = true;
			System.out.println("Error encontrado");
		}
	}

	//UNARY -> ! UNARY | - UNARY | CALL
	private void UNARY(){
		if(error)
			return;
		
		if(preanalisis.tipo == TipoToken.BANG){
			match(TipoToken.BANG);
			UNARY();
		}
		else if(preanalisis.tipo == TipoToken.MINUS){
			match(TipoToken.MINUS);
			UNARY();
		}
		else{
			CALL();
		}
	}

	//CALL -> PRIMARY CALL_2
	private void CALL(){
		if(error)
			return;
		
		PRIMARY();
		CALL_2();
	}

	//CALL_2 -> ( Args_Opc ) CALL_2 | void
	private void CALL_2(){
		if(error)
			return;
		
		if(preanalisis.tipo == TipoToken.LEFT_PAREN){
			match(TipoToken.LEFT_PAREN);
			Args_Opc();
			match(TipoToken.RIGHT_PAREN);
			// CALL_2();
		}
		else if(preanalisis.tipo == TipoToken.SEMICOLON || preanalisis.tipo == TipoToken.RIGHT_PAREN){ //¿se debe replantear esta condición?
			// void
		}
		else{
			error = true;
			System.out.println("Error encontrado");
		}
	}

	//PRIMARY -> true | false | null | number | string | IDENTIFIER | ( EXPRESSION )
	private void PRIMARY(){
		if(error)
			return;
		
		if(preanalisis.tipo == TipoToken.TRUE){
			match(TipoToken.TRUE);
		}
		else if(preanalisis.tipo == TipoToken.FALSE){
			match(TipoToken.FALSE);
		}
		else if(preanalisis.tipo == TipoToken.NULL){
			match(TipoToken.NULL);
		}
		else if(preanalisis.tipo == TipoToken.NUMBER){
			match(TipoToken.NUMBER);
		}
		else if(preanalisis.tipo == TipoToken.STRING){
			match(TipoToken.STRING);
		}
		else if(preanalisis.tipo == TipoToken.IDENTIFIER){
			match(TipoToken.IDENTIFIER);
		}
		else if(preanalisis.tipo == TipoToken.LEFT_PAREN){
			match(TipoToken.LEFT_PAREN);
			EXPRESSION();
			match(TipoToken.RIGHT_PAREN);
		}
		else{
			error = true;
			System.out.println("Error encontrado");
		}
	}

//+OTRAS	
	//FUNCTION -> IDENTIFIER (Param_Opc) Body
	private void FUNCTION(){
		if(error)
			return;
		
		match(TipoToken.IDENTIFIER);
		match(TipoToken.LEFT_PAREN);
		Param_Opc();
		match(TipoToken.RIGHT_PAREN);
		Block();
	}

	//FUNCTIONS -> FUN_DECL FUNCTIONS | void
	private void FUNCTIONS(){
		if(error)
			return;
		
		if(preanalisis.tipo == TipoToken.FUN){
			Fun_Declaration();
			FUNCTIONS();
		}
		else if(preanalisis.tipo == TipoToken.EOF){
			// void
		}
		else{
			error = true;
			System.out.println("Error encontrado");
		}
	}

	//Param_Opc -> Param | void
	private void Param_Opc(){
		if(error)
			return;
		
		if(preanalisis.tipo == TipoToken.NUMBER || preanalisis.tipo == TipoToken.STRING){ //¿debe ser solo string?
			Param();
		}
		else if(preanalisis.tipo == TipoToken.RIGHT_PAREN){
			// void
		}
		else{
			error = true;
			System.out.println("Error encontrado");
		}
	}

	//Param -> IDENTIFIER Param_2
	private void Param(){
		if(error)
			return;
		
		match(TipoToken.IDENTIFIER);
		Param_2();
	}

	//Param_2 -> , IDENTIFIER Param_2 | void
	private void Param_2(){
		if(error)
			return;
		
		if(preanalisis.tipo == TipoToken.COMMA){
			match(TipoToken.COMMA);
			match(TipoToken.IDENTIFIER);
			Param_2();
		}
		else if(preanalisis.tipo == TipoToken.RIGHT_PAREN){
			// void
		}
		else{
			error = true;
			System.out.println("Error encontrado");
		}
	}

	//Args_Opc -> EXPRESSION Args | void
	private void Args_Opc(){
		if(error)
			return;
		
		if(preanalisis.tipo == TipoToken.LEFT_PAREN || preanalisis.tipo == TipoToken.NUMBER || preanalisis.tipo == TipoToken.STRING || preanalisis.tipo == TipoToken.IDENTIFIER){
			EXPRESSION();
			Args();
		}
		else if(preanalisis.tipo == TipoToken.RIGHT_PAREN){
			// void
		}
		else{
			error = true;
			System.out.println("Error encontrado");
		}
	}

	//Args -> , EXPRESSION Args | void
	private void Args(){
		if(error)
			return;
		
		if(preanalisis.tipo == TipoToken.COMMA){
			match(TipoToken.COMMA);
			EXPRESSION();
			Args();
		}
		else if(preanalisis.tipo == TipoToken.RIGHT_PAREN){
			// void
		}
		else{
			error = true;
			System.out.println("Error encontrado");
		}
	}



	private void match(TipoToken tt){ //*Función que sirve para avanzar en la lista de tokens (cursor)
        if(preanalisis.tipo == tt){
            i++;
            preanalisis = tokens.get(i);
        }
        else{
            error = true;
            System.out.println("Error encontrado");
        }

    }
}