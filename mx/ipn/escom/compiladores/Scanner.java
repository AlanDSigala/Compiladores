package mx.ipn.escom.compiladores;

import mx.ipn.escom.compiladores.automatas.Letra;
import mx.ipn.escom.compiladores.automatas.Numbers;
import mx.ipn.escom.compiladores.automatas.OpeRelacional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {

    private final String source;

    private final List<Token> tokens = new ArrayList<>();

    private int linea = 1;

    private static final Map<String, TipoToken> palabrasReservadas;
    static {
        palabrasReservadas = new HashMap<>();
        palabrasReservadas.put("y", TipoToken.Y);
        palabrasReservadas.put("clase", TipoToken.CLASE);
        palabrasReservadas.put("ademas", TipoToken.ADEMAS );
        palabrasReservadas.put("falso", TipoToken.FALSO);
        palabrasReservadas.put("para", TipoToken.PARA);
        palabrasReservadas.put("fun", TipoToken.FUNCION); //definir funciones
        palabrasReservadas.put("si", TipoToken.SI);
        palabrasReservadas.put("nulo", TipoToken.NULO);
        palabrasReservadas.put("o", TipoToken.O);
        palabrasReservadas.put("imprimir", TipoToken.IMPRIMIR);
        palabrasReservadas.put("retornar", TipoToken.RETORNAR);
        palabrasReservadas.put("super", TipoToken.SUPER);
        palabrasReservadas.put("este", TipoToken.ESTE);
        palabrasReservadas.put("verdadero", TipoToken.VERDADERO);
        palabrasReservadas.put("var", TipoToken.VARIABLE); //definir variables
        palabrasReservadas.put("mientras", TipoToken.MIENTRAS);
    }


    Scanner(String source){
        this.source = source;
    }

    List<Token> scanTokens(){
        int estado = 0;
        int iLexema = 0;
        int fLexema;
        String lexema;

        //Aquí va el corazón del scanner.
        for (int i = 0; i < source.length(); i++) {
            char vistazo = source.charAt(i);
            fLexema = i;
            estado = Numbers.CompIfIsNumber(estado, vistazo);
            estado = Letra.CompIfIsLetter(estado, vistazo);
            estado = OpeRelacional.CompIfIsOpRel(estado, vistazo);

            //System.out.println("flag " + estado);

            switch (estado){
                case 0:
                    if (vistazo == ' ' || vistazo == '\t'){
                        iLexema = fLexema + 1;
                        continue;
                    } else if (vistazo == '\n') {
                        linea++;
                    }

                    if (Numbers.isDigit(vistazo)){
                      //Entra al diagrama de transicion para los numeros sin signo
                      estado = 12;
                      estado = Numbers.CompIfIsNumber(estado, vistazo);
                    }

                    if(Letra.isLetter(vistazo)){
                        //Entra al diagrama de transicion para los identificadores y palabras reservadas
                        estado = 9;
                        estado = Letra.CompIfIsLetter(estado, vistazo);
                    }

                    if(OpeRelacional.isOpRel(vistazo)){
                        //Entra al automata de los operadores relacionales
                        estado = 0;
                        estado = OpeRelacional.CompIfIsOpRel(estado, vistazo);
                    }
                    break;
                    //Estados finales
                case 19:
                    lexema = source.substring(iLexema, fLexema);
                    int E = lexema.indexOf('E');
                    String entero = lexema.substring(0, E);
                    String potencia = lexema.substring(E+1);
                    float enteroP = Float.parseFloat(entero);
                    int pow = Integer.parseInt(potencia);
                    tokens.add(new Token(TipoToken.NUMERO, lexema, enteroP*(Math.pow(10, pow)), linea));
                    iLexema = fLexema;
                    estado = 0;
                    i--;
                    break;
                case 20:
                    lexema = source.substring(iLexema, fLexema);
                    tokens.add(new Token(TipoToken.NUMERO, lexema, Integer.parseInt(lexema), linea));
                    iLexema = fLexema;
                    estado = 0;
                    i--;
                    break;
                case 21:
                    lexema = source.substring(iLexema, fLexema);
                    tokens.add(new Token(TipoToken.NUMERO, lexema, Float.parseFloat(lexema), linea));
                    iLexema = fLexema;
                    estado = 0;
                    i--;
                    break;
                case 11:
                    lexema = source.substring(iLexema, fLexema);
                    TipoToken tt = palabrasReservadas.get(lexema);
                    if(tt == null) {
                        //Crear el token tipo identificador
                        tokens.add( new Token(TipoToken.IDENTIFICADOR, lexema, null, linea) );
                    }
                    else{
                        tokens.add( new Token(tt, lexema, null, linea) );
                    }
                    estado = 0;
                    iLexema = fLexema;
                    i--;
                    break;
                case 2:
                    lexema = source.substring(iLexema, fLexema);
                    tokens.add(new Token(TipoToken.MENOR_EQ, lexema, null, linea));
                    estado = 0;
                    iLexema = fLexema;
                    break;
                case 3:
                    lexema = source.substring(iLexema, fLexema);
                    tokens.add(new Token(TipoToken.NOT_EQ, lexema, null, linea));
                    estado = 0;
                    iLexema = fLexema;
                    break;
                case 4:
                    lexema = source.substring(iLexema, fLexema);
                    tokens.add(new Token(TipoToken.MENOR, lexema, null, linea));
                    estado = 0;
                    iLexema =fLexema;
                    i--;
                    break;
                case 5:
                    lexema = source.substring(iLexema, fLexema + 1);
                    tokens.add(new Token(TipoToken.IGUAL, lexema, null , linea));
                    estado = 0; iLexema = fLexema;
                    break;
                case 7:
                    lexema = source.substring(iLexema, fLexema);
                    tokens.add(new Token(TipoToken.MAYOR_EQ, lexema, null, linea));
                    estado = 0; iLexema = fLexema;
                    break;
                case 8:
                    lexema = source.substring(iLexema, fLexema);
                    tokens.add(new Token(TipoToken.MAYOR, lexema, null, linea));
                    estado = 0; iLexema = fLexema;
                    i--;
                    break;
            }


            /*switch (vistazo){

                case ('('):
                    tokens.add(new Token(TipoToken.PAREN_IZQ, "(", null, linea));
                    break;
                case (')'):
                    tokens.add(new Token(TipoToken.PAREN_DER, ")",null, linea));
                    break;

                case ('{'):
                    tokens.add(new Token(TipoToken.LLAVE_IZQ, "{", null, linea));
                    break;

                case ('}'):
                    tokens.add(new Token(TipoToken.LLAVE_DER, "}", null, linea));
                    break;

                case (','):
                    tokens.add(new Token(TipoToken.COMA,",",null, linea));
                    break;

                case ('.'):
                    tokens.add(new Token(TipoToken.PUNTO,".",null, linea));
                    break;

                case (';'):
                    tokens.add(new Token(TipoToken.PUTO_COMA,";",null, linea));
                    break;

                case ('-'):
                    tokens.add(new Token(TipoToken.MENOS,"-",null, linea));

                case ("+"):
                    tokens.add(new Token(TipoToken.MAS,"+",null, linea));
            }*/
        }



        /*
        Analizar el texto de entrada para extraer todos los tokens
        y al final agregar el token de fin de archivo
         */
        tokens.add(new Token(TipoToken.EOF, "", null, linea));



        return tokens;
    }
}

/*
Signos o símbolos del lenguaje:
(
)
{
}
,
.
;
-
+
*
/
!
!=
=
==
<
<=
>
>=
// -> comentarios (no se genera token)
/* ... * / -> comentarios (no se genera token)
Identificador,
Cadena
Numero
Cada palabra reservada tiene su nombre de token

 */