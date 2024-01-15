class Parser {
    constructor(tokens) {
        this.tokens = tokens;
        this.errors = false;
        this.preanalysis = null;
        this.i = 0;
    }
    parse() {
        this.preanalysis = this.tokens[this.i];
        this.PROGRAM();
        if (!this.errors && !this.preanalysis.type === TokenType.EOF) {
            let msg = `Token inesperado: ${this.preanalysis.type}`;
            Interprete.error(this.preanalysis.line, msg);
        }
        else if (!this.errors && this.preanalysis.type === TokenType.EOF) {
            // pass
        }
    }
    PROGRAM() {
        if (
                this.preanalysis.type === TokenType.CLASS ||
                        this.preanalysis.type === TokenType.FUN ||
                        this.preanalysis.type === TokenType.VAR ||
                        this.preanalysis.type === TokenType.NEGATION ||
                        this.preanalysis.type === TokenType.LESS ||
                        this.preanalysis.type === TokenType.TRUE ||
                        this.preanalysis.type === TokenType.FALSE ||
                        this.preanalysis.type === TokenType.NULL ||
                        this.preanalysis.type === TokenType.THIS ||
                        this.preanalysis.type === TokenType.NUMBER ||
                        this.preanalysis.type === TokenType.STRING ||
                        this.preanalysis.type === TokenType.IDENTIFIER ||
                        this.preanalysis.type === TokenType.PARENT_OPEN ||
                        this.preanalysis.type === TokenType.SUPER ||
                        this.preanalysis.type === TokenType.FOR ||
                        this.preanalysis.type === TokenType.IF ||
                        this.preanalysis.type === TokenType.PRINT ||
                        this.preanalysis.type === TokenType.RETURN ||
                        this.preanalysis.type === TokenType.WHILE ||
                        this.preanalysis.type === TokenType.BRACKET_OPEN
        ) {
            this.DECLARATION();
        }
    }
    DECLARATION() {
        if (this.errors) {
            return;
        }
        if (this.preanalysis.type === TokenType.CLASS) {
            this.CLASS_DECL();
            this.DECLARATION();
        }
        else if (this.preanalysis.type === TokenType.FUN) {
            this.FUN_DECL();
            this.DECLARATION();
        }
        else if (this.preanalysis.type === TokenType.VAR) {
            this.VAR_DECL();
            this.DECLARATION();
        }
        else if (
                this.preanalysis.type === TokenType.NEGATION ||
                        this.preanalysis.type === TokenType.LESS ||
                        this.preanalysis.type === TokenType.TRUE ||
                        this.preanalysis.type === TokenType.FALSE ||
                        this.preanalysis.type === TokenType.NULL ||
                        this.preanalysis.type === TokenType.THIS ||
                        this.preanalysis.type === TokenType.NUMBER ||
                        this.preanalysis.type === TokenType.STRING ||
                        this.preanalysis.type === TokenType.IDENTIFIER ||
                        this.preanalysis.type === TokenType.PARENT_OPEN ||
                        this.preanalysis.type === TokenType.SUPER ||
                        this.preanalysis.type === TokenType.FOR ||
                        this.preanalysis.type === TokenType.IF ||
                        this.preanalysis.type === TokenType.PRINT ||
                        this.preanalysis.type === TokenType.RETURN ||
                        this.preanalysis.type === TokenType.WHILE ||
                        this.preanalysis.type === TokenType.BRACKET_OPEN ||
                        this.preanalysis.type === TokenType.GREAT
        ) {
            this.STATEMENT();
            this.DECLARATION();
        }
    }
    CLASS_DECL() {
        if (this.errors) {
            return;
        }
        if (this.preanalysis.type === TokenType.CLASS) {
            this.matchToken(TokenType.CLASS);
            this.matchToken(TokenType.IDENTIFIER);
            this.CLASS_INHER();
            this.matchToken(TokenType.BRACKET_OPEN);
            this.FUNCTIONS();
            this.matchToken(TokenType.BRACKET_CLOSE);
        }
        else {
            this.errors = true;
            let msg = `No se esperaba el token: ${this.preanalysis.type}`;
            Interprete.error(this.preanalysis.line, msg);
        }
    }
    CLASS_INHER() {
        if (this.errors) {
            return;
        }
        if (this.preanalysis.type === TokenType.LESS_THAN) {
            this.matchToken(TokenType.LESS_THAN);
            this.matchToken(TokenType.IDENTIFIER);
        }
    }
    FUN_DECL() {
        if (this.errors) {
            return;
        }
        if (this.preanalysis.type === TokenType.FUN) {
            this.matchToken(TokenType.FUN);
            this.FUNCTION();
        }
        else {
            this.errors = true;
            let msg = `No se esperaba el token: ${this.preanalysis.type}`;
            Interprete.error(this.preanalysis.line, msg);
        }
    }
    VAR_DECL() {
        if (this.errors) {
            return;
        }
        if (this.preanalysis.type === TokenType.VAR) {
            this.matchToken(TokenType.VAR);
            this.matchToken(TokenType.IDENTIFIER);
            this.VAR_INIT();
            this.matchToken(TokenType.SEMICOLON);
            this.jump_op();
        }
        else {
            this.errors = true;
            let msg = `No se esperaba el token: ${this.preanalysis.type}`;
            Interprete.error(this.preanalysis.line, msg);
        }
    }
    VAR_INIT() {
        if (this.errors) {
            return;
        }
        if (this.preanalysis.type === TokenType.ASIGNATION) {
            this.matchToken(TokenType.ASIGNATION);
            this.EXPRESSION();
        }
    }
    STATEMENT() {
        if (this.errors) {
            return;
        }
        if (
                this.preanalysis.type === TokenType.NEGATION ||
                        this.preanalysis.type === TokenType.LESS ||
                        this.preanalysis.type === TokenType.GREAT ||
                        this.preanalysis.type === TokenType.TRUE ||
                        this.preanalysis.type === TokenType.FALSE ||
                        this.preanalysis.type === TokenType.NULL ||
                        this.preanalysis.type === TokenType.THIS ||
                        this.preanalysis.type === TokenType.NUMBER ||
                        this.preanalysis.type === TokenType.STRING ||
                        this.preanalysis.type === TokenType.IDENTIFIER ||
                        this.preanalysis.type === TokenType.PARENT_OPEN ||
                        this.preanalysis.type === TokenType.SUPER
        ) {
            this.EXPR_STMT();
        }
        else if (this.preanalysis.type === TokenType.FOR) {
            this.FOR_STMT();
        }
        else if (this.preanalysis.type === TokenType.IF) {
            this.IF_STMT();
        }
        else if (this.preanalysis.type === TokenType.PRINT) {
            this.PRINT_STMT();
        }
        else if (this.preanalysis.type === TokenType.RETURN) {
            this.RETURN_STMT();
        }
        else if (this.preanalysis.type === TokenType.WHILE) {
            this.WHILE_STMT();
        }
        else if (this.preanalysis.type === TokenType.BRACKET_OPEN) {
            this.BLOCK();
        }
        else {
            this.errors = true;
            let msg = `No se esperaba el token: ${this.preanalysis.type}`;
            Interprete.error(this.preanalysis.line, msg);
        }
    }
    EXPR_STMT() {
        if (this.errors) {
            return;
        }
        if (
                this.preanalysis.type === TokenType.NEGATION ||
                        this.preanalysis.type === TokenType.LESS ||
                        this.preanalysis.type === TokenType.GREAT ||
                        this.preanalysis.type === TokenType.TRUE ||
                        this.preanalysis.type === TokenType.FALSE ||
                        this.preanalysis.type === TokenType.NULL ||
                        this.preanalysis.type === TokenType.THIS ||
                        this.preanalysis.type === TokenType.NUMBER ||
                        this.preanalysis.type === TokenType.STRING ||
                        this.preanalysis.type === TokenType.IDENTIFIER ||
                        this.preanalysis.type === TokenType.PARENT_OPEN ||
                        this.preanalysis.type === TokenType.SUPER
        ) {
            this.EXPRESSION();
            this.matchToken(TokenType.SEMICOLON);
        }
        else {
            this.errors = true;
            let msg = `No se esperaba el token: ${this.preanalysis.type}`;
            Interprete.error(this.preanalysis.line, msg);
        }
    }
    FOR_STMT() {
        if (this.errors) {
            return;
        }
        if (this.preanalysis.type === TokenType.FOR) {
            this.matchToken(TokenType.FOR);
            this.matchToken(TokenType.PARENT_OPEN);
            this.FOR_STMT_1();
            this.FOR_STMT_2();
            this.FOR_STMT_3();
            this.matchToken(TokenType.PARENT_CLOSE);
            this.STATEMENT();
        }
        else {
            this.errors = true;
            let msg = `No se esperaba el token: ${this.preanalysis.type}`;
            Interprete.error(this.preanalysis.line, msg);
        }
    }
    FOR_STMT_1() {
        if (this.errors) {
            return;
        }
        if (this.preanalysis.type === TokenType.VAR) {
            this.VAR_DECL();
        }
        else if (
                this.preanalysis.type === TokenType.NEGATION ||
                        this.preanalysis.type === TokenType.LESS ||
                        this.preanalysis.type === TokenType.GREAT ||
                        this.preanalysis.type === TokenType.TRUE ||
                        this.preanalysis.type === TokenType.FALSE ||
                        this.preanalysis.type === TokenType.NULL ||
                        this.preanalysis.type === TokenType.THIS ||
                        this.preanalysis.type === TokenType.NUMBER ||
                        this.preanalysis.type === TokenType.STRING ||
                        this.preanalysis.type === TokenType.IDENTIFIER ||
                        this.preanalysis.type === TokenType.PARENT_OPEN ||
                        this.preanalysis.type === TokenType.SUPER
        ) {
            this.EXPR_STMT();
        }
        else if (this.preanalysis.type === TokenType.SEMICOLON) {
            this.matchToken(TokenType.SEMICOLON);
        }
        else {
            this.errors = true;
            let msg = `No se esperaba el token: ${this.preanalysis.type}`;
            Interprete.error(this.preanalysis.line, msg);
        }
    }
    FOR_STMT_2() {
        if (this.errors) {
            return;
        }
        if (
                this.preanalysis.type === TokenType.NEGATION ||
                        this.preanalysis.type === TokenType.LESS ||
                        this.preanalysis.type === TokenType.GREAT ||
                        this.preanalysis.type === TokenType.TRUE ||
                        this.preanalysis.type === TokenType.FALSE ||
                        this.preanalysis.type === TokenType.NULL ||
                        this.preanalysis.type === TokenType.THIS ||
                        this.preanalysis.type === TokenType.NUMBER ||
                        this.preanalysis.type === TokenType.STRING ||
                        this.preanalysis.type === TokenType.IDENTIFIER ||
                        this.preanalysis.type === TokenType.PARENT_OPEN ||
                        this.preanalysis.type === TokenType.SUPER
        ) {
            this.EXPRESSION();
            this.matchToken(TokenType.SEMICOLON);
        }
        else if (this.preanalysis.type === TokenType.SEMICOLON) {
            this.matchToken(TokenType.SEMICOLON);
        }
        else {
            this.errors = true;
            let msg = `No se esperaba el token: ${this.preanalysis.type}`;
            Interprete.error(this.preanalysis.line, msg);
        }
    }
    FOR_STMT_3() {
        if (this.errors) {
            return;
        }
        if (
                this.preanalysis.type === TokenType.NEGATION ||
                        this.preanalysis.type === TokenType.LESS ||
                        this.preanalysis.type === TokenType.GREAT ||
                        this.preanalysis.type === TokenType.TRUE ||
                        this.preanalysis.type === TokenType.FALSE ||
                        this.preanalysis.type === TokenType.NULL ||
                        this.preanalysis.type === TokenType.THIS ||
                        this.preanalysis.type === TokenType.NUMBER ||
                        this.preanalysis.type === TokenType.STRING ||
                        this.preanalysis.type === TokenType.IDENTIFIER ||
                        this.preanalysis.type === TokenType.PARENT_OPEN ||
                        this.preanalysis.type === TokenType.SUPER
        ) {
            this.EXPRESSION();
        }
    }
    IF_STMT() {
        if (this.errors) {
            return;
        }
        if (this.preanalysis.type === TokenType.IF) {
            this.matchToken(TokenType.IF);
            this.matchToken(TokenType.PARENT_OPEN);
            this.EXPRESSION();
            this.matchToken(TokenType.PARENT_CLOSE);
            this.STATEMENT();
            this.ELSE_STATEMENT();
        }
        else {
            this.errors = true;
            let msg = `No se esperaba el token: ${this.preanalysis.type}`;
            Interprete.error(this.preanalysis.line, msg);
        }
    }
    ELSE_STATEMENT() {
        if (this.errors) {
            return;
        }
        if (this.preanalysis.type === TokenType.ELSE) {
            this.matchToken(TokenType.ELSE);
            this.STATEMENT();
        }
    }
    PRINT_STMT() {
        if (this.errors) {
            return;
        }
        if (this.preanalysis.type === TokenType.PRINT) {
            this.matchToken(TokenType.PRINT);
            this.EXPRESSION();
            this.matchToken(TokenType.SEMICOLON);
        }
        else {
            this.errors = true;
            let msg = `No se esperaba el token: ${this.preanalysis.type}`;
            Interprete.error(this.preanalysis.line, msg);
        }
    }
    RETURN_STMT() {
        if (this.errors) {
            return;
        }
        if (this.preanalysis.type === TokenType.RETURN) {
            this.matchToken(TokenType.RETURN);
            this.RETURN_EXP_OPC();
            this.matchToken(TokenType.SEMICOLON);
        }
        else {
            this.errors = true;
            let msg = `No se esperaba el token: ${this.preanalysis.type}`;
            Interprete.error(this.preanalysis.line, msg);
        }
    }
    RETURN_EXP_OPC() {
        if (this.errors) {
            return;
        }
        if (
                this.preanalysis.type === TokenType.NEGATION ||
                        this.preanalysis.type === TokenType.LESS ||
                        this.preanalysis.type === TokenType.GREAT ||
                        this.preanalysis.type === TokenType.TRUE ||
                        this.preanalysis.type === TokenType.FALSE ||
                        this.preanalysis.type === TokenType.NULL ||
                        this.preanalysis.type === TokenType.THIS ||
                        this.preanalysis.type === TokenType.NUMBER ||
                        this.preanalysis.type === TokenType.STRING ||
                        this.preanalysis.type === TokenType.IDENTIFIER ||
                        this.preanalysis.type === TokenType.PARENT_OPEN ||
                        this.preanalysis.type === TokenType.SUPER
        ) {
            this.EXPRESSION();
        }
    }
    WHILE_STMT() {
        if (this.errors) {
            return;
        }
        if (this.preanalysis.type === TokenType.WHILE) {
            this.matchToken(TokenType.WHILE);
            this.matchToken(TokenType.PARENT_OPEN);
            this.EXPRESSION();
            this.matchToken(TokenType.PARENT_CLOSE);
            this.STATEMENT();
        }
        else {
            this.errors = true;
            let msg = `No se esperaba el token: ${this.preanalysis.type}`;
            Interprete.error(this.preanalysis.line, msg);
        }
    }
    BLOCK() {
        if (this.errors) {
            return;
        }
        if (this.preanalysis.type === TokenType.BRACKET_OPEN) {
            this.matchToken(TokenType.BRACKET_OPEN);
            this.BLOCK_DECL();
            this.matchToken(TokenType.BRACKET_CLOSE);
        }
        else {
            this.errors = true;
            let msg = `No se esperaba el token: ${this.preanalysis.type}`;
            Interprete.error(this.preanalysis.line, msg);
        }
    }
    BLOCK_DECL() {
        if (this.errors) {
            return;
        }
        if (
                this.preanalysis.type === TokenType.CLASS ||
                        this.preanalysis.type === TokenType.FUN ||
                        this.preanalysis.type === TokenType.VAR ||
                        this.preanalysis.type === TokenType.NEGATION ||
                        this.preanalysis.type === TokenType.LESS ||
                        this.preanalysis.type === TokenType.GREAT ||
                        this.preanalysis.type === TokenType.TRUE ||
                        this.preanalysis.type === TokenType.FALSE ||
                        this.preanalysis.type === TokenType.NULL ||
                        this.preanalysis.type === TokenType.THIS ||
                        this.preanalysis.type === TokenType.NUMBER ||
                        this.preanalysis.type === TokenType.STRING ||
                        this.preanalysis.type === TokenType.IDENTIFIER ||
                        this.preanalysis.type === TokenType.PARENT_OPEN ||
                        this.preanalysis.type === TokenType.SUPER ||
                        this.preanalysis.type === TokenType.FOR ||
                        this.preanalysis.type === TokenType.IF ||
                        this.preanalysis.type === TokenType.PRINT ||
                        this.preanalysis.type === TokenType.RETURN ||
                        this.preanalysis.type === TokenType.WHILE ||
                        this.preanalysis.type === TokenType.BRACKET_OPEN
        ) {
            this.DECLARATION();
            this.BLOCK_DECL();
        }
    }
    EXPRESSION() {
        if (this.errors) {
            return;
        }
        if (
                this.preanalysis.type === TokenType.NEGATION ||
                        this.preanalysis.type === TokenType.LESS ||
                        this.preanalysis.type === TokenType.TRUE ||
                        this.preanalysis.type === TokenType.FALSE ||
                        this.preanalysis.type === TokenType.NULL ||
                        this.preanalysis.type === TokenType.THIS ||
                        this.preanalysis.type === TokenType.NUMBER ||
                        this.preanalysis.type === TokenType.STRING ||
                        this.preanalysis.type === TokenType.IDENTIFIER ||
                        this.preanalysis.type === TokenType.PARENT_OPEN ||
                        this.preanalysis.type === TokenType.SUPER ||
                        this.preanalysis.type === TokenType.GREAT
        ) {
            this.ASSIGNMENT();
        }
        else {
            this.errors = true;
            let msg = `No se esperaba el token: ${this.preanalysis.type}`;
            Interprete.error(this.preanalysis.line, msg);
        }
    }
    ASSIGNMENT() {
        if (this.errors) {
            return;
        }
        if (
                this.preanalysis.type === TokenType.NEGATION ||
                        this.preanalysis.type === TokenType.LESS ||
                        this.preanalysis.type === TokenType.TRUE ||
                        this.preanalysis.type === TokenType.FALSE ||
                        this.preanalysis.type === TokenType.NULL ||
                        this.preanalysis.type === TokenType.THIS ||
                        this.preanalysis.type === TokenType.NUMBER ||
                        this.preanalysis.type === TokenType.STRING ||
                        this.preanalysis.type === TokenType.IDENTIFIER ||
                        this.preanalysis.type === TokenType.PARENT_OPEN ||
                        this.preanalysis.type === TokenType.SUPER ||
                        this.preanalysis.type === TokenType.GREAT
        ) {
            this.LOGIC_OR();
            this.ASSIGNMENT_OPC();
        }
        else {
            this.errors = true;
            let msg = `No se esperaba el token: ${this.preanalysis.type}`;
            Interprete.error(this.preanalysis.line, msg);
        }
    }
    ASSIGNMENT_OPC() {
        if (this.errors) {
            return;
        }
        if (this.preanalysis.type === TokenType.ASIGNATION) {
            this.matchToken(TokenType.ASIGNATION);
            this.EXPRESSION();
        }
    }
}


    function LOGIC_OR() {
        if (this.errors) {
            return;
        }
        if (
                this.preanalysis.type === "NEGATION" ||
                        this.preanalysis.type === "LESS" ||
                        this.preanalysis.type === "TRUE" ||
                        this.preanalysis.type === "FALSE" ||
                        this.preanalysis.type === "NULL" ||
                        this.preanalysis.type === "THIS" ||
                        this.preanalysis.type === "NUMBER" ||
                        this.preanalysis.type === "STRING" ||
                        this.preanalysis.type === "IDENTIFIER" ||
                        this.preanalysis.type === "PARENT_OPEN" ||
                        this.preanalysis.type === "SUPER" ||
                        this.preanalysis.type === "GREAT"
        ) {
            this.LOGIC_AND();
            this.LOGIC_OR_2();
        } else {
            this.errors = true;
            var msg = "No se esperaba el token: " + this.preanalysis.type;
            Interprete.error(this.preanalysis.line, msg);
        }
    }

    function LOGIC_OR_2() {
        if (this.errors) {
            return;
        }
        if (this.preanalysis.type === "OR") {
            this.matchToken("OR");
            this.LOGIC_AND();
            this.LOGIC_OR_2();
        }
    }

    function LOGIC_AND() {
        if (this.errors) {
            return;
        }
        if (
                this.preanalysis.type === "NEGATION" ||
                        this.preanalysis.type === "LESS" ||
                        this.preanalysis.type === "TRUE" ||
                        this.preanalysis.type === "FALSE" ||
                        this.preanalysis.type === "NULL" ||
                        this.preanalysis.type === "THIS" ||
                        this.preanalysis.type === "NUMBER" ||
                        this.preanalysis.type === "STRING" ||
                        this.preanalysis.type === "IDENTIFIER" ||
                        this.preanalysis.type === "PARENT_OPEN" ||
                        this.preanalysis.type === "SUPER" ||
                        this.preanalysis.type === "GREAT"
        ) {
            this.EQUALITY();
            this.LOGIC_AND_2();
        } else {
            this.errors = true;
            var msg = "No se esperaba el token: " + this.preanalysis.type;
            Interprete.error(this.preanalysis.line, msg);
        }
    }

    function LOGIC_AND_2() {
        if (this.errors) {
            return;
        }
        if (this.preanalysis.type === "AND") {
            this.matchToken("AND");
            this.EQUALITY();
            this.LOGIC_AND_2();
        }
    }

    function EQUALITY() {
        if (this.errors) {
            return;
        }
        if (
                this.preanalysis.type === "NEGATION" ||
                        this.preanalysis.type === "LESS" ||
                        this.preanalysis.type === "TRUE" ||
                        this.preanalysis.type === "FALSE" ||
                        this.preanalysis.type === "NULL" ||
                        this.preanalysis.type === "THIS" ||
                        this.preanalysis.type === "NUMBER" ||
                        this.preanalysis.type === "STRING" ||
                        this.preanalysis.type === "IDENTIFIER" ||
                        this.preanalysis.type === "PARENT_OPEN" ||
                        this.preanalysis.type === "SUPER" ||
                        this.preanalysis.type === "GREAT"
        ) {
            this.COMPARISON();
            this.EQUALITY_2();
        } else {
            this.errors = true;
            var msg = "No se esperaba el token: " + this.preanalysis.type;
            Interprete.error(this.preanalysis.line, msg);
        }
    }

    function EQUALITY_2() {
        if (this.errors) {
            return;
        }
        if (this.preanalysis.type === "DIFERENT") {
            this.matchToken("DIFERENT");
            this.COMPARISON();
            this.EQUALITY_2();
        } else if (this.preanalysis.type === "EQUAL") {
            this.matchToken("EQUAL");
            this.COMPARISON();
            this.EQUALITY_2();
        }
    }

    function COMPARISON() {
        if (this.errors) {
            return;
        }
        if (
                this.preanalysis.type === "NEGATION" ||
                        this.preanalysis.type === "LESS" ||
                        this.preanalysis.type === "TRUE" ||
                        this.preanalysis.type === "FALSE" ||
                        this.preanalysis.type === "NULL" ||
                        this.preanalysis.type === "THIS" ||
                        this.preanalysis.type === "NUMBER" ||
                        this.preanalysis.type === "STRING" ||
                        this.preanalysis.type === "IDENTIFIER" ||
                        this.preanalysis.type === "PARENT_OPEN" ||
                        this.preanalysis.type === "SUPER" ||
                        this.preanalysis.type === "GREAT"
        ) {
            this.TERM();
            this.COMPARISON_2();
        } else {
            this.errors = true;
            var msg = "No se esperaba el token: " + this.preanalysis.type;
            Interprete.error(this.preanalysis.line, msg);
        }
    }

    function COMPARISON_2() {
        if (this.errors) {
            return;
        }
        if (this.preanalysis.type === "GREAT_THAN") {
            this.matchToken("GREAT_THAN");
            this.TERM();
            this.COMPARISON_2();
        } else if (this.preanalysis.type === "GREAT_EQUAL") {
            this.matchToken("GREAT_EQUAL");
            this.TERM();
            this.COMPARISON_2();
        } else if (this.preanalysis.type === "LESS_THAN") {
            this.matchToken("LESS_THAN");
            this.TERM();
            this.COMPARISON_2();
        } else if (this.preanalysis.type === "LESS_EQUAL") {
            this.matchToken("LESS_EQUAL");
            this.TERM();
            this.COMPARISON_2();
        }
    }

    function TERM() {
        if (this.errors) {
            return;
        }
        if (
                this.preanalysis.type === "NEGATION" ||
                        this.preanalysis.type === "LESS" ||
                        this.preanalysis.type === "TRUE" ||
                        this.preanalysis.type === "FALSE" ||
                        this.preanalysis.type === "NULL" ||
                        this.preanalysis.type === "THIS" ||
                        this.preanalysis.type === "NUMBER" ||
                        this.preanalysis.type === "STRING" ||
                        this.preanalysis.type === "IDENTIFIER" ||
                        this.preanalysis.type === "PARENT_OPEN" ||
                        this.preanalysis.type === "SUPER" ||
                        this.preanalysis.type === "GREAT"
        ) {
            this.FACTOR();
            this.TERM_2();
        } else {
            this.errors = true;
            var msg = "No se esperaba el token: " + this.preanalysis.type;
            Interprete.error(this.preanalysis.line, msg);
        }
    }

    function TERM_2() {
        if (this.errors) {
            return;
        }
        if (this.preanalysis.type === "LESS") {
            this.matchToken("LESS");
            this.FACTOR();
            this.TERM_2();
        } else if (this.preanalysis.type === "ADD") {
            this.matchToken("ADD");
            this.FACTOR();
            this.TERM_2();
        } else if (this.preanalysis.type === "SUB") {
            this.matchToken("SUB");
            this.FACTOR();
            this.TERM_2();
        } else if (this.preanalysis.type === "GREAT") {
            this.matchToken("GREAT");
            this.FACTOR();
            this.TERM_2();
        }
    }

    function FACTOR() {
        if (this.errors) {
            return;
        }
        if (
                this.preanalysis.type === "NEGATION" ||
                        this.preanalysis.type === "LESS" ||
                        this.preanalysis.type === "TRUE" ||
                        this.preanalysis.type === "FALSE" ||
                        this.preanalysis.type === "NULL" ||
                        this.preanalysis.type === "THIS" ||
                        this.preanalysis.type === "NUMBER" ||
                        this.preanalysis.type === "STRING" ||
                        this.preanalysis.type === "IDENTIFIER" ||
                        this.preanalysis.type === "PARENT_OPEN" ||
                        this.preanalysis.type === "SUPER" ||
                        this.preanalysis.type === "GREAT"
        ) {
            this.UNARY();
            this.FACTOR_2();
        } else {
            this.errors = true;
            var msg = "No se esperaba el token: " + this.preanalysis.type;
            Interprete.error(this.preanalysis.line, msg);
        }
    }

    function FACTOR_2() {
        if (this.errors) {
            return;
        }
        if (this.preanalysis.type === "DIAG") {
            this.matchToken("DIAG");
            this.UNARY();
            this.FACTOR_2();
        } else if (this.preanalysis.type === "MULT") {
            this.matchToken("MULT");
            this.UNARY();
            this.FACTOR_2();
        }
    }

    function UNARY() {
        if (this.errors) {
            return;
        }
        if (this.preanalysis.type === "NEGATION") {
            this.matchToken("NEGATION");
            this.UNARY();
        } else if (this.preanalysis.type === "LESS") {
            this.matchToken("LESS");
            this.UNARY();
        } else if (this.preanalysis.type === "GREAT") {
            this.matchToken("GREAT");
            this.UNARY();
        } else if (
                this.preanalysis.type === "TRUE" ||
                        this.preanalysis.type === "FALSE" ||
                        this.preanalysis.type === "NULL" ||
                        this.preanalysis.type === "THIS" ||
                        this.preanalysis.type === "NUMBER" ||
                        this.preanalysis.type === "STRING" ||
                        this.preanalysis.type === "IDENTIFIER" ||
                        this.preanalysis.type === "PARENT_OPEN" ||
                        this.preanalysis.type === "SUPER"
        ) {
            this.CALL();
        } else {
            this.errors = true;
            var msg = "No se esperaba el token: " + this.preanalysis.type;
            Interprete.error(this.preanalysis.line, msg);
        }
    }

    function CALL() {
        if (this.errors) {
            return;
        }
        if (
                this.preanalysis.type === "TRUE" ||
                        this.preanalysis.type === "FALSE" ||
                        this.preanalysis.type === "NULL" ||
                        this.preanalysis.type === "THIS" ||
                        this.preanalysis.type === "NUMBER" ||
                        this.preanalysis.type === "STRING" ||
                        this.preanalysis.type === "IDENTIFIER" ||
                        this.preanalysis.type === "PARENT_OPEN" ||
                        this.preanalysis.type === "SUPER"
        ) {
            this.PRIMARY();
            this.CALL_2();
        } else {
            this.errors = true;
            var msg = "No se esperaba el token: " + this.preanalysis.type;
            Interprete.error(this.preanalysis.line, msg);
        }
    }

    function CALL_2() {
        if (this.errors) {
            return;
        }
        if (this.preanalysis.type === "PARENT_OPEN") {
            this.matchToken("PARENT_OPEN");
            this.ARGUMENTS_OPC();
            this.matchToken("PARENT_CLOSE");
            this.CALL_2();
        } else if (this.preanalysis.type === "DOT") {
            this.matchToken("DOT");
            this.matchToken("IDENTIFIER");
            this.CALL_2();
        }
    }

    function PRIMARY() {
        if (this.errors) {
            return;
        }
        if (this.preanalysis.type === "TRUE") {
            this.matchToken("TRUE");
        } else if (this.preanalysis.type === "FALSE") {
            this.matchToken("FALSE");
        } else if (this.preanalysis.type === "NULL") {
            this.matchToken("NULL");
        } else if (this.preanalysis.type === "THIS") {
            this.matchToken("THIS");
        } else if (this.preanalysis.type === "NUMBER") {
            this.matchToken("NUMBER");
        } else if (this.preanalysis.type === "STRING") {
            this.matchToken("STRING");
        } else if (this.preanalysis.type === "IDENTIFIER") {
            this.matchToken("IDENTIFIER");
        } else if (this.preanalysis.type === "PARENT_OPEN") {
            this.matchToken("PARENT_OPEN");
            this.EXPRESSION();
            this.matchToken("PARENT_CLOSE");
        } else if (this.preanalysis.type === "SUPER") {
            this.matchToken("SUPER");
            this.matchToken("DOT");
            this.matchToken("IDENTIFIER");
        } else {
            this.errors = true;
            var msg = "No se esperaba el token: " + this.preanalysis.type;
            Interprete.error(this.preanalysis.line, msg);
        }
    }

    function FUNCTION() {
        if (this.errors) {
            return;
        }
        if (this.preanalysis.type === "IDENTIFIER") {
            this.matchToken("IDENTIFIER");
            this.matchToken("PARENT_OPEN");
            this.PARAMETERS_OPC();
            this.matchToken("PARENT_CLOSE");
            this.jump_op();
            this.BLOCK();
        } else {
            this.errors = true;
            var msg = "No se esperaba el token: " + this.preanalysis.type;
            Interprete.error(this.preanalysis.line, msg);
        }
    }

    function FUNCTIONS() {
        if (this.errors) {
            return;
        }
        if (this.preanalysis.type === "IDENTIFIER") {
            this.FUNCTION();
            this.FUNCTIONS();
        }
    }

    function PARAMETERS_OPC() {
        if (this.errors) {
            return;
        }
        if (this.preanalysis.type === "IDENTIFIER") {
            this.PARAMETERS();
        }
    }

    function jump_op() {
        if (this.errors) {
            return;
        }
        if (this.preanalysis.type === "jump") {
            this.jump_par();
        }
    }

    function jump_par() {
        if (this.errors) {
            return;
        }
        if (this.preanalysis.type === "jump") {
            this.matchToken("jump");
        } else {
            this.errors = true;
            var msg = "No se esperaba el token: " + this.preanalysis.type;
            Interprete.error(this.preanalysis.line, msg);
        }
    }

    function PARAMETERS() {
        if (this.errors) {
            return;
        }
        if (this.preanalysis.type === "IDENTIFIER") {
            this.matchToken("IDENTIFIER");
            this.PARAMETERS_2();
        } else {
            this.errors = true;
            var msg = "No se esperaba el token: " + this.preanalysis.type;
            Interprete.error(this.preanalysis.line, msg);
        }
    }

    function PARAMETERS_2() {
        if (this.errors) {
            return;
        }
        if (this.preanalysis.type === "COMMA") {
            this.matchToken("COMMA");
            this.matchToken("IDENTIFIER");
            this.PARAMETERS_2();
        }
    }

    function ARGUMENTS_OPC() {
        if (this.errors) {
            return;
        }
        if (
                this.preanalysis.type === "NEGATION" ||
                        this.preanalysis.type === "LESS" ||
                        this.preanalysis.type === "GREAT" ||
                        this.preanalysis.type === "TRUE" ||
                        this.preanalysis.type === "FALSE" ||
                        this.preanalysis.type === "NULL" ||
                        this.preanalysis.type === "THIS" ||
                        this.preanalysis.type === "NUMBER" ||
                        this.preanalysis.type === "STRING" ||
                        this.preanalysis.type === "IDENTIFIER" ||
                        this.preanalysis.type === "PARENT_OPEN" ||
                        this.preanalysis.type === "SUPER"
        ) {
            this.ARGUMENTS();
        }
    }

    function ARGUMENTS() {
        if (this.errors) {
            return;
        }
        if (
                this.preanalysis.type === "NEGATION" ||
                        this.preanalysis.type === "LESS" ||
                        this.preanalysis.type === "GREAT" ||
                        this.preanalysis.type === "TRUE" ||
                        this.preanalysis.type === "FALSE" ||
                        this.preanalysis.type === "NULL" ||
                        this.preanalysis.type === "THIS" ||
                        this.preanalysis.type === "NUMBER" ||
                        this.preanalysis.type === "STRING" ||
                        this.preanalysis.type === "IDENTIFIER" ||
                        this.preanalysis.type === "PARENT_OPEN" ||
                        this.preanalysis.type === "SUPER"
        ) {
            this.EXPRESSION();
            this.ARGUMENTS_2();
        } else {
            this.errors = true;
            var msg = "No se esperaba el token: " + this.preanalysis.type;
            Interprete.error(this.preanalysis.line, msg);
        }
    }

    function ARGUMENTS_2() {
        if (this.errors) {
            return;
        }
        if (this.preanalysis.type === "COMMA") {
            this.matchToken("COMMA");
            this.EXPRESSION();
            this.ARGUMENTS_2();
        }
    }

    function matchToken(t) {
        if (this.errors) {
            return;
        }
        if (this.preanalysis.type === t) {
            this.i += 1;
            this.preanalysis = this.tokens[this.i];
        } else {
            this.errors = true;
            var msg = "Se esperaba el token: " + t;
            Interprete.error(this.preanalysis.line, msg);
        }
    }

