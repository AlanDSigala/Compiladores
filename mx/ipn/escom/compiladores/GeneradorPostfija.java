package mx.ipn.escom.compiladores;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class GeneradorPostfija {

    private final List<Token> infija; //Expresiones en forma infija
    private final Stack<Token> pila; //pila para almacenar temporalmente operadores
    private final List<Token> postfija; //Expresiones en forma postfija

    public GeneradorPostfija(List<Token> infija) {
        this.infija = infija;
        this.pila = new Stack<>();
        this.postfija = new ArrayList<>();
    }

    public List<Token> convertir(){
        boolean estructuraDeControl = false;
        Stack<Token> pilaEstructurasDeControl = new Stack<>();

        for(int i=0; i<infija.size(); i++){
            Token t = infija.get(i);

            if(t.tipo == TipoToken.EOF){
                break;
            }

            if(t.esPalabraReservada()){
                /*
                 Si el token actual es una palabra reservada, se va directo a la
                 lista de salida.
                 */
                postfija.add(t);
                if (t.esEstructuraDeControl()){
                    estructuraDeControl = true;
                    pilaEstructurasDeControl.push(t);
                }
            }
            else if(t.esOperando()){
                postfija.add(t);
            }
            else if(t.tipo == TipoToken.PAREN_IZQ){
                pila.push(t);
            }
            else if(t.tipo == TipoToken.PAREN_DER){
                while(!pila.isEmpty() && pila.peek().tipo != TipoToken.PAREN_IZQ){
                    Token temp = pila.pop();
                    postfija.add(temp);
                }
                if(!pila.isEmpty() && pila.peek().tipo == TipoToken.PAREN_IZQ){
                    pila.pop();
                }
                if(estructuraDeControl){
                    postfija.add(new Token(TipoToken.PUNTO_COMA, ";", null, 0, 0));
                }
            }
            else if(t.esOperador()){
                while(!pila.isEmpty() && pila.peek().precedenciaMayorIgual(t)){
                    Token temp = pila.pop();
                    postfija.add(temp);
                }
                if (!pila.isEmpty() && t.tipo == TipoToken.MENOS){
                    if (infija.get(i - 1).tipo != TipoToken.NUMERO && infija.get(i - 1).tipo != TipoToken.IDENTIFICADOR){
                        Token cero = new Token(TipoToken.NUMERO, "0", 0.0, 0, 0);
                        postfija.add(cero);
                    }
                }
                pila.push(t);

            }
            else if(t.tipo == TipoToken.PUNTO_COMA){
                while(!pila.isEmpty() && pila.peek().tipo != TipoToken.LLAVE_IZQ){
                    Token temp = pila.pop();
                    postfija.add(temp);
                }
                postfija.add(t);
            }
            else if(t.tipo == TipoToken.LLAVE_IZQ){
                // Se mete a la pila, tal como el parentesis. Este paso
                // pudiera omitirse, sólo hay que tener cuidado en el manejo
                // del "}".
                pila.push(t);
            }
            else if(t.tipo == TipoToken.LLAVE_DER && estructuraDeControl){

                // Primero verificar si hay un else:
                if(infija.get(i + 1).tipo == TipoToken.SI_NO){
                    // Sacar el "{" de la pila
                    pila.pop();
                }
                else{
                    // En este punto, en la pila sólo hay un token: "{"
                    // El cual se extrae y se añade un ";" a cadena postfija,
                    // El cual servirá para indicar que se finaliza la estructura
                    // de control.
                    if(!pila.isEmpty()){
                        pila.pop();
                    }

                    postfija.add(new Token(TipoToken.PUNTO_COMA, ";", null, 0, 0));

                    // Se extrae de la pila de estrucuras de control, el elemento en el tope
                    pilaEstructurasDeControl.pop();
                    if(pilaEstructurasDeControl.isEmpty()){
                        estructuraDeControl = false;
                    }
                }


            }
        }
        while(!pila.isEmpty()){
            Token temp = pila.pop();
            postfija.add(temp);
        }

        while(!pilaEstructurasDeControl.isEmpty()){
            pilaEstructurasDeControl.pop();
            postfija.add(new Token(TipoToken.PUNTO_COMA, ";", null, 0, 0));
        }

        return postfija;
    }

}
