package com.example;

import javafx.util.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

public class Equation {
    public static final char[] operadores = { '+', '-', '(', ')', '*', '/' };
    public static final char[] literals = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
                                            'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'y', 'z' };
    public static final char variable = 'x';
    public static final char[] digitos = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
    public static final char pwr = '^';

    private static String ecuLook = "";

    private static ArrayList<coeficiente> termProf1 = new ArrayList<>();
    private static ArrayList<coeficiente> termProf2 = new ArrayList<>();
    private static ArrayList<coeficiente> termProf3 = new ArrayList<>();
    private static ArrayList<coeficiente> termProf4 = new ArrayList<>();

    //minipasos .- pasos que se realizaron para presentar el paso
    private static class termino implements Comparable<termino> {
        private ArrayList<coeficiente> coeficientes;
        private Double coeNumEqui = 1.0;
        private Integer[] liteCoe = new Integer[literals.length];
        private ArrayList<termino> nestedCoe; // estos de aca es la lista de coeficientes anidados que posee
        private coeficiente antiCoe;
        //private String coeLiteOrd;
        private boolean nextIsInver;
        String looks;  //  **APARIENCIA**
        Integer grado = 0; // **GRADO**
        private boolean signed; //su signo es visible? si es negativo siempre será visible de otra manera si es el primero
                                //en una lista no se verá
        //private boolean isPolinomio; // todos sus coeficientes son monomios

        public termino(boolean isSig) {
            coeficientes = new ArrayList<coeficiente>();
            looks = "";
            signed = isSig;
        }
        private termino(coeficiente c){
            coeficientes = new ArrayList<coeficiente>();
            coeficientes.add(c);
            ultmtLook();
            signed = false;
        }
        private void ultmtLook(){ //Sets the looks to la forma que debe tener en el espacio de tiempo que se invoca
            looks = "";
            for(int i = 0; i < this.coeficientes.size(); i++){
                looks += coeficientes.get(i).getLooks();
            }
        }

        /*
         * reglas de formalizacion
         * antes de toda formalizacion se van a eliminar todos los espacios (' ') del regex
         * solo pueden haber 4 niveles de profundidad en la ecuacion
         * el proceso de construir el arreglo de terminos es:
         * 1) Crear un buffer para el termino actual (currentT) y un buffer para la lista de terminos
         * 2) leer caracteres e interpretarlos como coeficientes del currentT
         * () Cada vez que se agregue un coeficiente al termino o el termino a la lista de terminos se va a resetear sus valores
         *    a defaults (coeficiente => positivol, termino => new termino(true))
         * 2.a) si es un digito entonces se agrega este digito a un arreglo para despues pasarlo a decimal
         * 2.b) si es un literal, entonces agregamos el arreglo de digitos como decimal y el literal como coeficientes
         * 2.c) si es un operador
         * 2.c.1) si es + o - : agregar el currentT a la lista dependiendo del signo leido
         *        a no ser que el anterior caracter es un + o -, de otra manera agregar un coeficiente signo al currentT
         * 2.c.2) si es * se puede ignorar a no ser que este este precedido por '+' '-' '*' o '/', si sucede esto es un error 
         * 2.c.3) si es '(' entonces se va a buscar la ultima aparicion de ')' y entregar todo su contenido para intentar formalizarlo en un nuevo arreglo
         *        de terminos, el cual al ser succesful se va a encapsular en un coeficiente compuesto. si 
         * 2.d) si es la variable x entonces se agrega un coeficiente incgonita al currentT
         * 2.c) si es ')' significa un error puesto que no se deberian encontrar, pues el termino '(' que deberia precederlos
         *      hace que estos caracteres no sean posibles de leer, entonces significa que se cerro un termino compuesto sin
         *      abrirlo
         * 3) si se lee un caracter diferente a los mencionados es un error
         * 
         * @param buffer arreglo de terminos que se terminara de construir si es que todo sale bien
         */
        public static boolean frmlzSuccess(ArrayList<termino> obj, String regex, boolean onLado1, int profundidad) {
            ArrayList<termino> buffer = new ArrayList<>();
            termino current = new termino(true);
            coeficiente cCoe = coeficiente.positivo;
            char last = '+';
            ArrayList<Character> numCoe = new ArrayList<>();

            if (regex.length() == 0) {// si el lado esta vacio es equivalente a cero
                current.coeficientes.add(coeficiente.cero);
                buffer.add(current);
                obj = buffer;
                return true;
            }
            for (int i = 0; i < regex.length(); i++) {
                char tken = regex.charAt(i);
                if(i != 0) last = regex.charAt(i-1);
                if(getIndexof(tken, operadores) != -1){ // es un operador
                switch(tken){
                    case '+':
                    if(last == '+' || last == '-') current.coeficientes.add(coeficiente.positivo);
                    else if(getIndexof(last, digitos) != -1){

                    }
                    else{
                        buffer.add(current);
                        current = new termino(true);
                    }
                    break;
                    case '-':
                    if(last == '+' || last == '-') current.coeficientes.add(coeficiente.negativo);
                    else {
                        buffer.add(current);
                        current = new termino(false);
                    }
                    break;
                    case '*':
                    if(last == '+' || last == '-' || last == '(' || last == '*') {warning = "No puede haber signos de * o / precedidos de + o -";
                    return false;}
                    if(getIndexof(last, digitos) != -1){
                        cCoe = coeficiente.numeral;
                        cCoe.setCoeNum(toDecimal(numCoe));
                        current.coeficientes.add(cCoe);
                        numCoe.clear();
                    }
                    /*else if(getIndexof(last, literals) != -1){
                        cCoe = coeficiente.literal;
                        cCoe.setCoeLite(getIndexof(last, literals));
                        current.coeficientes.add(cCoe);
                    }*/
                    break;
                    case '/':
                    if(last == '+' || last == '-' || last == '(' || last == '*') {
                        warning = "No puede haber signos de * o / precedidos de + o -";
                        return false;
                    }
                    if(getIndexof(last, digitos) != -1){
                        cCoe = coeficiente.numeral;
                        cCoe.setCoeNum(toDecimal(numCoe));
                        current.coeficientes.add(cCoe);
                        numCoe.clear();
                    }
                    break;
                    case '(': //al recibir un '(' entonces se va crear  
                    if(getIndexof(last, digitos) != -1){ //si era precedido por un número
                        cCoe = coeficiente.numeral;
                        cCoe.setCoeNum(toDecimal(numCoe));
                        current.coeficientes.add(cCoe);
                        numCoe.clear();
                    }
                    ArrayList<termino> anidado = new ArrayList<>();
                    if(frmlzSuccess(anidado, regex.substring(i + 1, regex.lastIndexOf(")")) , onLado1, profundidad + 1)){
                        coeficiente coeCompuesto = coeficiente.compuesta;
                        coeCompuesto.setCoeCmpnd(anidado);
                        if(last == '/'){
                            coeficiente newInver = coeficiente.inversa;
                            newInver.setCoeInver(coeCompuesto);
                            current.coeficientes.add(newInver);
                        }
                        current.coeficientes.add(coeCompuesto);
                        switch(profundidad + 1){//esto podria hacerse sin un switch
                            case 1:
                            termProf1.add(coeCompuesto);
                            break;
                            case 2:
                            termProf2.add(coeCompuesto);
                            break;
                            case 3:
                            termProf3.add(coeCompuesto);
                            break;
                            case 4:
                            termProf4.add(coeCompuesto);
                            break;
                            default:
                            warning = "NO puede haber mas de 4 parentesis anidados";
                            return false;
                        }
                        i = regex.lastIndexOf(")");
                    }
                    break;
                    case ')':
                    String currentLado = (onLado1)? "1": "2";
                    warning = "Se encontro un ')' sin haber abierto un '(' en el lado " + currentLado;
                    return false;
                }
                }
                else if(getIndexof(tken, digitos) != -1){ //es un digito
                    numCoe.add(tken);
                }
                else if(getIndexof(tken, literals) != -1){ //es un literal
                    coeficiente unLite = coeficiente.literal;
                    unLite.setCoeLite(getIndexof(tken, literals));
                    current.coeficientes.add(unLite);
                }
                else if(tken == 'x'){ // es la variable
                    current.coeficientes.add(coeficiente.incognita); //ATENCION AQUI!... no sabia que se podia agregar de esta manera
                }
            } //no quedan más casos?? ... aca esta seteando el arreglo de terminos a lo que acabamos de contruir
            obj = buffer;
            return true;
        }
        private static int toDecimal(ArrayList<Character> li){
            int buffer = 1;
            for(int i = 0; i< li.size(); i++){
                buffer += getIndexof(li.get(i), digitos) * Math.pow(10, li.size() - (i + 1));
                //example {1,2,3} -> i = 0 buffer = 1*10^(3-1) + 2*10^(2-1) + 3*10^(1-1) = 1*10*10 + 2*10 + 3*1
            }
            return buffer;
        }
        @Override
        public int compareTo(termino arg0) {
            if(grado > arg0.grado) return -1;
            else if(grado == arg0.grado) return 0;
            return 1;
        }
        private void reduce() {
            coeNumEqui = 1.0;
            for(coeficiente c: coeficientes){
                coeNumEqui *= c.numCoe;
                if(c.liteCoe != -1)
                    liteCoe[c.liteCoe]++;
                nestedCoe = c.nestedCoe;
                if(c == c.incognita) grado++;
                if(c.inverso != null){
                    antiCoe = c;
                }
            }
        }

        //public coeficiente Box(){
        //    coeficiente self = coeficiente.anidado;
        //}
    }
    //3x(6-x)xxcda -> 3adc(6-x)x^3 numeral literal anidado grado inverso
    enum coeficiente{
        cero(0,  "0"),       // 
        positivo(1,  "+"),   // 
        negativo(-1,  "-"),
        numeral(1,""), //solo parte numerica
        literal(1, ""), //es una letra que representa un coeficiente
        compuesta(1, ""), //representa un coeficiente que es en su una lista de terminos
        incognita(1, "x"),
        inversa(1, ""); //representa un coeficiente que esta dividiendo, por lo que en su interior tiene
                                     //un coeficiente y su looks es (1/coeficiente) 

        private String looks;
        //para estos se supone que se estan multiplicando, pero solo se denotan como multiplicando
        private int numCoe;
        private int liteCoe = -1;
        private ArrayList<termino> nestedCoe; // almacena el coeficiente anidado
        private coeficiente inverso; // almacena el coeficiente que está dividiendo
        coeficiente(int coef, String looks) {
            numCoe = coef;
            liteCoe = 0;
            this.looks = looks;
        }

        public String getLooks(){
            return looks;
        }

        public void setCoeNum(int coef){
            numCoe = coef;
            looks = "" + numCoe;
        }

        public void setCoeLite(int coef){
            liteCoe = coef;
            looks = ""+ literals[coef];
        }

        public void setCoeCmpnd(ArrayList<termino> nested){
            nestedCoe = nested;
        }

        public void setCoeInver(coeficiente toInver){
            inverso = toInver;
        }
    }

    private static ArrayList<termino> lado1;
    private static ArrayList<termino> lado2;
    private static String warning = "Equation hasn't been initialized";
    private static String metodo;
    private static String orgnlEqu;
    public static boolean onNewEcu(String ecuacion) { // it makes sure that the unformilazed equation has 1 equals sign and
                                                        // the same amount of "(", ")".
        int counter = 0, counterPi = 0, counterPf = 0;
        int ecuLen = ecuacion.length();
        for(int i = 0; i < ecuacion.length(); i++){
            if(ecuacion.charAt(i) == '=') counter++;        //cuenta la cantidad de operadores "="
            else if(ecuacion.charAt(i) == '(') counterPi++; //cuenta la cantidad de parentesis abiertos "("
            else if(ecuacion.charAt(i) == ')') counterPf++; //cuenta la cantidad de parentesis cerrados ")"
        }
        if(counter == 1){
            if(counterPi == counterPf && counterPi < 5){
                int indexEquals = ecuacion.indexOf('=');
                orgnlEqu = ecuacion;
                return (indexEquals == ecuLen - 1)?
                    termino.frmlzSuccess(lado1, ecuacion.substring(0, indexEquals), true, 0) && termino.frmlzSuccess(lado2, "", false, 0)
                    : termino.frmlzSuccess(lado1, ecuacion.substring(0, indexEquals), true, 0) && termino.frmlzSuccess(lado2, ecuacion.substring(indexEquals + 1), false, 0);
            }
            warning = "La profundidad de esta ecuacion no puede ser mayor a 4, tambien todos los parentesis deben cerrarse";
            return false;
        }
        warning = "Debe de haber una igualdad entre 2 expresiones algebraicas para resolver la ecuacion \n Numero de '=' diferente a 1.";
        return false;
    }

    /*  Todas las ecuaciones deben ser terminadas en los siguientes pasos
     *  - Mostrar la equivalencia en la ecuacion ingresada y la formalizada y ordenada (Propiedad conmutativa)
     *  - Quitar todos los parentesis (distributiva inversa)
     *  - Realizar la suma de terminos semejantes ([2o+ terminos] -> 1 termino)
     *  - pasar todos los terminos diferentes de grado 1 al otro lado de donde se encuentré la variable
     *  - 
     */
    private static void solve(int fase){//algoritmo para realizar el proximo paso para resolver la ecuacion
        switch (fase){
            //primer paso es mostrar la ecuacion formalizada que se tiene
            case 1:
                for(termino t: lado1){
                    Collections.sort(t.coeficientes);
                }
                for(termino t: lado2){
                    Collections.sort(t.coeficientes);
                }
                actualizaEquLook();
                warning = "La ecuacion ingresada es equivalente a " + ecuLook;
            break;
            case 2://resolver anidados
                if(termProf4.size() != 0){
                    for(coeficiente c: termProf4) {
                        for(termino t: c.nestedCoe){
                            t.reduce();
                        }
                    }
                    for(coeficiente c : termProf3){
                        for(termino t: c.nestedCoe){
                            t.reduce();
                        }
                    }
                    for(coeficiente c : termProf2){
                        for(termino t: c.nestedCoe){
                            t.reduce();
                        }
                    }
                    for(coeficiente c : termProf1){
                        for(termino t: c.nestedCoe){
                            t.reduce();
                        }
                    }
                    for(termino t: lado1){
                        t.reduce();
                    }
                    for(termino t: lado2){
                        t.reduce();
                    }
                }
                actualizaEquLook();
                warning = "Despejando los parentesis: " + ecuLook;
            break;
            case 3:
                sumTermsX(lado1);
                sumTermsX(lado2);
            break;
            case 4:
                partite();
            break;
            case 5:
                sumTermsX(lado2);
            break;
            case 6:
                warning = "El valor de x es: "; //+ answer();
            break;
            default: break;
        }
    }
    /*Lista de pasos:
     * 1)El primer paso seria mostrar la nueva ecuacion equivalente a la formalizada ordenada
     * 2)Resolver los terminos anidados, empezando desde el más anidado
     * 3)luego sumar terminos semejantes (en funcion de x)
     * como reducimos terminos del estilo 3a + 5a? podemos decir que la parte literal es comparable y si lo son podemos
     * 4)realizar la suma de terminos literales semejantes a esto lo llamaremos sumar literales
     * por lo que de momento no hay forma de factorizar
     * 5)luego de esto quedarse con el termino de grado 1 en el lado 1
     * 6)finalmente nos deshacemos de los demas coeficientes y tenemos la respuesta
     * 3ax - 2bx + (4x + 2) = 0 + 9x
     */

    private static void partite() {
        for(termino t: lado1){
            if(t.grado != 1) t.coeNumEqui *= -1;
            lado2.add(t);
            lado1.remove(t);
        }
        for(termino t: lado2){
            if(t.grado == 1) t.coeNumEqui *= -1;
            lado1.add(t);
            lado2.remove(t);
        }
    }

    private static void sumTermsX(ArrayList<termino> obj) {
    if(obj.size() > 1){
        for(int i = 1; i < obj.size(); i++){
            if(obj.get(i-1).grado == obj.get(i).grado){
                //obj.set(i, sumaCoefs());
                obj.set(i-1, new termino(coeficiente.cero));
            }
        }
    }
    }

    //proriedad de Look
    /*
     * En el paso 1, los minipasos van a mostrar la formalización de paso a paso, que es un algortimo
     * que los chicos pueden entender. 2xa = 2ax , (3x-2)xx2xazs -> 2(3x-2)aszx^3
     * Podemos ver que un termino esta ordenado ya no necesita tener coeficientes por separado
     * Pero que hay de los anidados... bueno un puntero a ellos... pero recordamos que despues de pasar terminos
     * y factorizar 
     */
    static void actualizaEquLook(){ // actualiza el ecuLook para que este de acorde a los lados
        //algoritmo simple
        //cada lista devuelve su forma y las separamos por una igualdad
        ecuLook = lookOf(lado1) + " = " + lookOf(lado2);
    }

    private static String lookOf(ArrayList<termino> arre) {
        String look = "";
        for(termino t : arre){
            look += t.looks;
        }
        return look;
    }

    static int getIndexof(char s, char l[]) {//busca en las listas estaticas un elemento
        for (int i = 0; i < l.length; i++) {
            if (s == l[i])
                return i;
        }
        return -1;
    }
    public static String getWarning(){
        return warning;
    }
    public static String getEcuation(){
        return ecuLook;
    }
}
