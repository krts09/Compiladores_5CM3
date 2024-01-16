from Token import Token
from Tokentype import TokenType
from SymbolTable import TSimbolos

class Postfix:
    def __init__(self, tokens) -> None:
        #self.simbolos = simbolos
        self.infija = tokens
        self.stackk = []
        self.postfija = []
        self.palabras_reservadas = {  # Diccionario con las palabras reservadas y su tipo de token correspondiente
            # "palabra" - "tipo token"
            "class": TokenType.CLASS,
            "also": TokenType.ALSO,
            "for": TokenType.FOR,
            "fun": TokenType.FUN,
            "if": TokenType.IF,
            "null": TokenType.NULL,
            "print": TokenType.PRINT,
            "return": TokenType.RETURN,
            "super": TokenType.SUPER,
            "this": TokenType.THIS,
            #"true": TokenType.TRUE,
            "var": TokenType.VAR,
            "while": TokenType.WHILE,
            "else": TokenType.ELSE,
            #"true": TokenType.TRUE,
            #"false": TokenType.FALSE,
            #"and": TokenType.AND,
            #"or": TokenType.OR
        }

    def convertir(self):
        estructuraDeControl = False
        stack_Estruc:list[Token] = []
        auxfor = 0

        for index,t in enumerate(self.infija):
            if t.type == TokenType.EOF:
                break
            
            if t.type in self.palabras_reservadas.values():
                self.postfija.append(t)
                if self.esEstructuraDeControl(t.type):
                    estructuraDeControl = True
                    stack_Estruc.append(t)
            elif self.esOperando(t.type):
                self.postfija.append(t)
            elif t.type == TokenType.PARENT_OPEN:
                self.stackk.append(t)
            elif t.type == TokenType.PARENT_CLOSE:
                #print(f"{estructuraDeControl} y {self.stackk[-1].type}")
                while( ( len(self.stackk) != 0) and (self.stackk[-1].type != TokenType.PARENT_OPEN) ):
                    #if estructuraDeControl:
                        #if stack_Estruc[-1].type == TokenType.FOR:
                    temp = self.stackk.pop()
                    self.postfija.append(temp)
                if self.stackk[-1].type == TokenType.PARENT_OPEN:
                    self.stackk.pop()
                if estructuraDeControl:
                    #print("semiak")
                    self.postfija.append(Token(TokenType.SEMICOLON,";",";",None))
            elif self.esOperador(t.type):
                #print(f"si -> {t.type}")
                while len(self.stackk)!=0 and self.precedenciaMayorIgual(self.stackk[-1].type, t.type):
                    temp = self.stackk.pop()
                    self.postfija.append(temp)
                self.stackk.append(t)
            elif t.type == TokenType.SEMICOLON:
                while len(self.stackk) !=0 and self.stackk[-1].type != TokenType.BRACKET_OPEN:
                    if estructuraDeControl:
                        if stack_Estruc[-1].type == TokenType.FOR: #and auxfor <2:
                            temp = self.stackk.pop()
                            self.postfija.append(temp)
                            break
                        pass
                    temp = self.stackk.pop()
                    self.postfija.append(temp)
                self.postfija.append(t) 
            elif t.type == TokenType.BRACKET_OPEN:
                self.stackk.append(t)
            elif t.type == TokenType.BRACKET_CLOSE and estructuraDeControl:
                #print(f"caso else -> {self.infija[index+1].type}")
                if self.infija[index+1].type == TokenType.ELSE:
                    self.stackk.pop()
                else:
                    self.stackk.pop()
                    #print("aki")
                    self.postfija.append(Token(TokenType.SEMICOLON,";",";",None))
                    #self.postfija.append("semi")
                    stack_Estruc.pop()
                    if len(stack_Estruc) == 0:
                        estructuraDeControl = False

        while(len(self.stackk) != 0):
            temp = self.stackk.pop()
            self.postfija.append(temp)

        while( len(stack_Estruc) != 0):
            #print("sem")
            stack_Estruc.pop()
            self.postfija.append(Token(TokenType.SEMICOLON,";",";",None))


        return self.postfija

    def esOperando(self,tipo:TokenType):
        match tipo:
            case TokenType.IDENTIFIER:
                return True
            case TokenType.NUMBER | TokenType.STRING | TokenType.TRUE | TokenType.FALSE:
                return True
            case other:
                return False

    def esEstructuraDeControl(self,tipo):
        match tipo:
            case TokenType.IF | TokenType.ELSE:
                return True
            case TokenType.WHILE | TokenType.FOR:
                return True
            case _:
                return False

    def esOperador(self,tipo:TokenType):
        #print("esop")
        match tipo:
            case TokenType.ADD | TokenType.SUB | TokenType.MULT | TokenType.DIAG:
                return True
            case TokenType.EQUAL | TokenType.DIFERENT | TokenType.GREAT | TokenType.GREAT_EQUAL:
                return True
            case TokenType.AND | TokenType.OR | TokenType.ASIGNATION:
                return True
            case TokenType.LESS_THAN | TokenType.LESS_EQUAL:
                return True
            case other:
                return False


    def precedenciaMayorIgual(self, tipo1, tipo2):
        #print(f"{tipo1}->{self.obtenerPrecedencia(tipo1)} y  {tipo2}->{self.obtenerPrecedencia(tipo2)}")
        return  self.obtenerPrecedencia(tipo1) >= self.obtenerPrecedencia(tipo2)

    def obtenerPrecedencia(self,tipo):
        match tipo:
            case TokenType.MULT | TokenType.DIAG:
                return 7 
            case TokenType.ADD | TokenType.SUB:
                return 6
            case TokenType.GREAT_EQUAL | TokenType.GREAT | TokenType.LESS_THAN | TokenType.LESS_EQUAL:
                return 5
            case TokenType.DIFERENT | TokenType.EQUAL:
                return 4
            case TokenType.AND:
                return 3
            case TokenType.OR:
                return 2
            case TokenType.ASIGNATION:
                return 1
            case _:
                return 0


    def obAridad(self,tipo: TokenType):
        match tipo:
            case TokenType.MULT| TokenType.DIAG| TokenType.SUB| TokenType.ADD| TokenType.EQUAL| TokenType.GREAT| TokenType.GREAT_EQUAL | TokenType.ASIGNATION:
                return 2
            case TokenType.LESS_THAN | TokenType.LESS_EQUAL | TokenType.DIFERENT | TokenType.AND | TokenType.OR :
                return 2
            case other:
                return 0