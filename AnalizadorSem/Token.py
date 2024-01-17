from Tokentype import TokenType
class Token:    # Clase para representar los tokens encontrados por el scanner
    def __init__(self, tipo: TokenType, lexema: str, literal, linea: int) -> None:
        self.type = tipo
        self.lexeme = lexema
        self.literal = literal  ##Palabra reservada no lleva literal
        self.line = linea

    def __eq__(self, other):
        if not isinstance(other, Token):
            return False
        return self.type == other.type

    def __repr__(self) -> str:
        return f"<{self.type}, lexema:{self.lexeme}, literal:{self.literal}, linea:{self.line}>"