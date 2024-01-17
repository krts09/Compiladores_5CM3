package mx.ipn.escom.k.parser;

import mx.ipn.escom.k.parser.ParserException;
import mx.ipn.escom.k.tools.TipoToken;
import mx.ipn.escom.k.tools.Token;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int i = 0;
    private boolean error = false;
    private Token preanalisis;

    public Parser(List<Token> tokens) throws ParserException {
        this.tokens = tokens;
    }

    public boolean parse() throws ParserException {
        Program();

        if (preanalisis.tipo == TipoToken.EOF && !error) {
            System.out.println("Compilación terminada con éxito");
            return true;
        } else {
            System.out.println("Compilación terminada con errores");
        }
        return false;
    }

    //+GRAMÁTICA
    // Program -> Declaration
    private void Program() throws ParserException {
        declaration();
    }

    //+DECLARACIONES
    // Declaration -> Fun_Declaration Declaration| Var_Declaration Declaration | Statement Declaration | void
    private void declaration() throws ParserException {
        if (error)
            return;

        switch (preanalisis.getTipo()){
            case FUN:
                fun_declaration();
                declaration();
                break;
            case VAR:
                var_declaration();
                declaration();
                break;
            case default:
                statement();
                declaration();
                break;
        }
    }

    //Fun_Declaration -> fun FUNCTION
    private void fun_declaration() throws ParserException {
        if(error)
            return;

        match(TipoToken.FUN);
        function();
    }

    //Var_Declaration -> var IDENTIFIER Var_Init ;
    private void var_declaration() throws ParserException {
        if(error)
            return;

        match(TipoToken.VAR);
        match(TipoToken.IDENTIFIER);
        var_init();
        match(TipoToken.SEMICOLON);
    }

    //Var_Init -> = EXPRESSION | void
    private void var_init() throws ParserException {
        if(error)
            return;

        switch (preanalisis.getTipo()) {
            case EQUAL:
                expression();
                break;
        }
    }

    //+SENTENCIAS
    //Statement_Declaration -> Expr_STMT | For_STMT | If_STMT | Print_STMT | Return_STMT | While_STMT | Block
    private Statement statement(){
        Statement stmt = statement();
        switch (preanalisis.getTipo()){
            case FOR:
                for_stmt();
                break;
            case IF:
                if_stmt();
                break;
            case PRINT:
                print_stmt();
                break;
            case RETURN:
                return_stmt();
                break;
            case WHILE:
                while_stmt();
                break;
            case LEFT_BRACE:
                block();
                break;
            case default:
                expr_stmt();
                break;
        }

    }

    private ExprBinary term(Expression expr) throws ParserException {
        factor();
        term2();
		return ;
    }

    private ExprBinary term2(Expression expr) throws ParserException {
        ExprBinary expBin;
		switch (preanalisis.getTipo()){
			case MINUS:
				match(TipoToken.MINUS);
				factor();
                Token operador = previous();
                Expression expr2 = unary();
                expBin = new ExprBinary(expr, operador, expr2);
				term2();
				break;
			case PLUS:
				match(TipoToken.PLUS);
				factor();
				term2();
				break;
		}
	}

	private ExprBinary factor() throws ParserException{
        ExprBinary expr = unary();
        expr = factor2(expr);
        return expr;
    }

    private Expression factor2(Expression expr) throws ParserException{
        switch (preanalisis.getTipo()){
            case SLASH:
                match(TipoToken.SLASH);
                Token operador = previous();
                Expression expr2 = unary();
                ExprBinary expb = new ExprBinary(expr, operador, expr2);
                return factor2(expb);
            case STAR:
                match(TipoToken.STAR);
                operador = previous();
                expr2 = unary();
                expb = new ExprBinary(expr, operador, expr2);
                return factor2(expb);
        }
        return expr;
    }

    private ExprUnary unary() throws ParserException{
        switch (preanalisis.getTipo()){
            case BANG:
                match(TipoToken.BANG);
                Token operador = previous();
                ExprUnary expr = unary();
                return new ExprUnary(operador, expr);
            case MINUS:
                match(TipoToken.MINUS);
                operador = previous();
                expr = unary();
                return new ExprUnary(operador, expr);
            default:
                return call();
        }
    }

    private Expression call() throws ParserException{
        Expression expr = primary();
        expr = call2(expr);
        return expr;
    }

    private Expression call2(Expression expr) throws ParserException{
        switch (preanalisis.getTipo()){
            case LEFT_PAREN:
                match(TipoToken.LEFT_PAREN);
                List<Expression> lstArguments = argumentsOptional();
                match(TipoToken.RIGHT_PAREN);
                ExprCallFunction ecf = new ExprCallFunction(expr, lstArguments);
                return call2(ecf);
			case DOT:
				match(TipoToken.DOT);
        }
        return expr;
    }

	private List<Token> parametersOptional() throws ParserException {
		List<Token> params = new ArrayList<>();

		switch (preanalisis.getTipo()){
			case IDENTIFIER:
				parameters(params);
				break;
		}
		return params;
	}

	private void parameters (List<Token> params) throws ParserException {
        if (preanalisis.getTipo() == TipoToken.IDENTIFIER) {
            match(TipoToken.IDENTIFIER);
            Token name = previous();
            params.add(name);
            parameters2(params);
        } else {
            String message = "Error en la posicion " + preanalisis.getPosition() + " cerca de " + preanalisis.getLexema() + ". Se esperaba un identificador";
            throw new ParserException(message);
        }
	}

	private void parameters2(List<Token> params) throws ParserException {
		switch (preanalisis.getTipo()){
			case COMMA:
				match(TipoToken.COMMA);
				match(TipoToken.IDENTIFIER);
				Token name = previous();
				params.add(name);
				parameters2(params);
				break;
		}
	}

    private List<Expression> argumentsOptional() throws ParserException {
		List<Expression> lstArguments = new ArrayList<>();

		switch (preanalisis.getTipo()){
			case BANG:
			case MINUS:
			case TRUE:
			case FALSE:
			case NULL:
			case NUMBER:
			case STRING:
			case IDENTIFIER:
			case LEFT_PAREN:
			case SUPER:
			case THIS:
				Expression expr = expression();
				lstArguments.add(expr);
				arguments(lstArguments);
				break;
		}
		return lstArguments;
	}

	private void arguments(List<Expression> lstArguments) throws ParserException {
		switch (preanalisis.getTipo()){
			case COMMA:
				match(TipoToken.COMMA);
				Expression expr = ();
				lstArguments.add(expr);
				arguments(lstArguments);
				break;
		}
	}

	private Expression primary() throws ParserException{
        switch (preanalisis.getTipo()){
            case TRUE:
                match(TipoToken.TRUE);
                return new ExprLiteral(true);
            case FALSE:
                match(TipoToken.FALSE);
                return new ExprLiteral(false);
            case NULL:
                match(TipoToken.NULL);
                return new ExprLiteral(null);
            case NUMBER:
                match(TipoToken.NUMBER);
                Token numero = previous();
                return new ExprLiteral(numero.getLiteral());
            case STRING:
                match(TipoToken.STRING);
                Token cadena = previous();
                return new ExprLiteral(cadena.getLiteral());
            case IDENTIFIER:
                match(TipoToken.IDENTIFIER);
                Token id = previous();
                return new ExprVariable(id);
            case LEFT_PAREN:
                match(TipoToken.LEFT_PAREN);
                Expression expr = expression();
                // Tiene que ser cachado aquello que retorna
                match(TipoToken.RIGHT_PAREN);
                return new ExprGrouping(expr);
        }
        return null;
    }


    private Expression expression() {

        return ;
	}

	private void match(TipoToken tt) throws ParserException {
        if(preanalisis.getTipo() ==  tt){
            i++;
            preanalisis = tokens.get(i);
        }
        else{
            String message = "Error en la línea " +
                    preanalisis.getPosition() +
                    ". Se esperaba " + preanalisis.getTipo() +
                    " pero se encontró " + tt;
            throw new ParserException(message);
        }
    }


    private Token previous() {
        return this.tokens.get(i - 1);
    }

}
