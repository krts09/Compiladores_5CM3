import re
from Tokentype import TokenType #Importar la clase TokenType del módulo tipo_token
from Token import Token #Importar la clase Token del módulo token
import string  #Importar el módulo string

class Scanner: #Clase Scanner
    def __init__(self,source) -> None: #Definir el método constructor de la clase
        self.source = source
        self.__linea = 1  #Asignar el atributo linea a la instancia de la clase, con valor inicial de 1
        self.tokens = []  #Asignar el atributo tokens a la instancia de la clase, con una lista vacía como valor inicial
        self.palabras_reservadas = { # Diccionario con las palabras reservadas y su tipo de token correspondiente
            # "palabra" - "tipo token" 
            'and': TokenType.AND,
            'class': TokenType.CLASS,
            'also': TokenType.ALSO,
            'for': TokenType.FOR,
            'fun' : TokenType.FUN,
            'if' : TokenType.IF,
            'null' : TokenType.NULL,
            'print' : TokenType.PRINT,
            'return' : TokenType.RETURN,
            'super' : TokenType.SUPER,
            'this' : TokenType.THIS,
            'true' : TokenType.TRUE,
            'var' : TokenType.VAR,
            'while' : TokenType.WHILE,
            'else': TokenType.ELSE,
            'true': TokenType.TRUE,
            'false': TokenType.FALSE,
            'or': TokenType.OR
        } 

    def ScanTokens(self): # -> list[TokenType]:
        ##Aqui va el scanner


        self.estado = 0

        for line in self.source:
            current = ""
            line1 = self.clean(line)
            line1 +=" "
             # Ciclo for que recorre cada caracter en la línea ya limpia
            for char in line1:
                match self.estado: # Inicia un bloque de comparaciones de acuerdo al valor de la variable estado
                    case 0:
                        if char == "<": # Dependiento del caracter actual el en este caso es "<", se cambia el valor de estado a 1
                            self.estado = 1
                        elif char == "=":
                            self.estado = 2
                        elif char == ">":
                            self.estado = 3
                        elif char.isdigit(): # Si el caracter actual es un dígito, se agrega a la variable current y se cambia el valor de estado a 4
                           current += char
                           self.estado = 4
                        elif char.isalpha():
                            current += char
                            self.estado = 5
                        elif char == "/":
                            self.estado = 6
                        elif char == "{": # Si el caracter actual es "{", se agrega un token de tipo TokenType.ALLAVE a la lista tokens, y se cambia el valor de estado a 0
                            self.tokens.append(Token(TokenType.BRACKET_OPEN,"{",None,self.__linea))
                            self.estado = 0
                        elif char == "}":
                            self.tokens.append(Token(TokenType.BRACKET_CLOSE,"}",None,self.__linea))
                            self.estado = 0
                        elif char == "(":
                            self.tokens.append(Token(TokenType.PARENT_OPEN,"(",None,self.__linea))
                            self.estado = 0
                        elif char == ")":
                            self.tokens.append(Token(TokenType.PARENT_CLOSE,")",None,self.__linea))
                            self.estado = 0
                        elif char == "+":
                            self.tokens.append(Token(TokenType.ADD,"+",None,self.__linea))
                            self.estado = 0
                        elif char == "-":
                            self.tokens.append(Token(TokenType.SUB,"-",None,self.__linea))
                            self.estado = 0
                        elif char == "*":
                            self.tokens.append(Token(TokenType.MULT,"*",None,self.__linea))
                            self.estado = 0
                        elif char == "!":
                            self.estado = 8
                        elif char == '"':
                            current += char
                            self.estado = 9
                        elif char == ";":
                            self.tokens.append(Token(TokenType.SEMICOLON,";",None,self.__linea))
                            self.estado = 0
                        elif char == ",":
                            self.tokens.append(Token(TokenType.COMMA,",",None,self.__linea))
                            self.estado = 0
                        else:# Si el carácter actual no es ninguno de los anteriores: No hacemos nada y pasamos al siguiente carácter.
                            pass
                    case 1: # Si el estado actual es 1:
                        if char == "=":
                            self.tokens.append(Token(TokenType.LESS_EQUAL,"<=",None,self.__linea)) # Añadimos un nuevo objeto Token a la lista de tokens.
                            self.estado = 0 # Cambiamos el estado del scanner a 0.
                        else:
                            self.tokens.append(Token(TokenType.LESS_THAN,"<",None,self.__linea))
                            self.estado = 0
                    case 2:
                        if char == "=":
                            self.tokens.append(Token(TokenType.EQUAL,"==",None,self.__linea))
                            self.estado = 0
                        else:
                            self.tokens.append(Token(TokenType.ASIGNATION,"=",None,self.__linea))
                            self.estado = 0
                    case 3:
                        if char == "=":
                            self.tokens.append(Token(TokenType.GREAT_EQUAL,">=",None,self.__linea))
                            self.estado = 0
                        else:
                            self.tokens.append(Token(TokenType.GREAT,">",None,self.__linea))
                            self.estado=0
                        pass
                    case 4:
                        if char.isdigit() or char == ".":
                            current += char
                        else:
                            self.tokens.append(Token(TokenType.NUMBER,current,float(current),self.__linea))
                            current = ""
                            self.estado = 0
                    case 5:
                        if char.isdigit() or char.isalpha():
                            current += char
                        else:
                            if current in self.palabras_reservadas:
                                #print(f"{current}")
                                if current.lower() == "true":
                                    self.tokens.append(Token(TokenType.TRUE,current,True,self.__linea))
                                    current = ""
                                    self.estado = 0
                                elif current.lower() == "false":
                                    self.tokens.append(Token(TokenType.FALSE,current,False,self.__linea))
                                    current = ""
                                    self.estado = 0
                                else:
                                    self.tokens.append(Token(self.palabras_reservadas[current],current,None,self.__linea))
                                    current = ""
                                    self.estado = 0
                            else:
                                self.tokens.append(Token(TokenType.IDENTIFIER,current,None,self.__linea))
                                current = ""
                                self.estado = 0
                    case 6:
                        if char == "/":
                            self.estado = 7
                        elif char == "*":
                            self.estado = 11
                        else:
                            self.tokens.append(Token(TokenType.DIAG,"/",None,self.__linea))
                            self.estado = 0
                    case 7:
                        if char == "\n":
                            self.estado = 0
                        else:
                            self.estado = 7
                    case 8:
                        if char == "=":
                            self.tokens.append(Token(TokenType.DIFERENT,"!=",None,self.__linea))
                            self.estado = 0
                        else:
                            self.tokens.append(Token(TokenType.NEGATION,"!",None,self.__linea))
                            self.estado = 0
                    case 9:
                        if char == '"':
                            current += char
                            self.tokens.append(Token(TokenType.STRING,current,current[2:-2],self.__linea))
                            current = ""
                            self.estado = 0
                        else:
                            current += char
                    case 11:
                        if char == "*":
                            self.estado = 12
                    case 12:
                        if char == "/":
                            self.estado = 0
                        else:
                            self.estado = 11

            self.__linea += 1 # Despues de leer la primera linea se incrementa
                        
        self.tokens.append(Token(TokenType.EOF,None,None,self.__linea-1)) # Se termina el archivo y se agrega el token EOF
        return self.tokens # Devuelve la lista de tokens generada

    def clean(self,cadena): # Define una función 'clean' que toma una cadena como entrada y devuelve una cadena limpia
        simbolos = ['(', ')' ,'{' ,'}', '=', '<', '>', '!', '+', '-', ';', '*', '/']   # Define una lista de Símbolos
        clean_str = ''

        pattern = r'\/\/.*|\/\*[\s\S]*?\*\/|([A-Za-z_][A-Za-z0-9_]*|\d+(?:\.\d+)?|\S)'
        result = re.findall(pattern, cadena)    
        
        clean_str = " ".join(result)

        

        clean_str=clean_str.replace("/ *","/*")
        clean_str=clean_str.replace("* /","*/")
        clean_str=clean_str.replace("> =",">=")
        clean_str=clean_str.replace("< =","<=")
        clean_str=clean_str.replace("= =","==")
        clean_str=clean_str.replace("! =","!=")

        #print(clean_str)

        return f"{clean_str}\n"

        
