package com.example;

import javafx.util.Pair;
import java.util.ArrayList;
import java.util.LinkedList;

public class Equation {
    public static final char[] operadores = { '+', '-', '(', ')', '*', '/' };
    public static final char[] literals = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
            'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'y', 'z' };
    public static final char variable = 'x';
    public static final char[] digitos = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
    public static final char pwr = '^';

    private static class xprsnAlge {
        private LinkedList<termino> terminos;
        private ArrayList<Character> ops;
        String XpresionS;

        private boolean isPolinomio;//right after initialize, check; after a solve, check 

        public xprsnAlge() {
            terminos = new LinkedList<termino>();
            ops = new ArrayList<>();
            XpresionS = "";
        }

        public static boolean frmlzSuccess(int lado, String regex) {
            xprsnAlge buffer = new xprsnAlge();
            boolean need[] = { true, true, true, true };
            if (regex.length() == 0) {
                buffer.terminos.add(termino.cero);
                if (lado == 1)
                    lado1 = buffer;
                else
                    lado2 = buffer;
                return true;
            }
            for (int i = 0; i < regex.length(); i++) {
                char tken = regex.charAt(i);
                ArrayList<Character> nextCoe = new ArrayList<>();
                if (need[0] && getIndexof(tken, operadores) == -1) { // si no es un operador...
                    if (need[1] && getIndexof(tken, digitos) == -1) { // ni un digito...
                        if (need[2] && getIndexof(tken, literals) == -1) { // o literal...
                            if (need[3] && tken == variable) { // debe de ser la incognita
                                if (need.equals(handleNeed(1, 0, 0, 1)))
                                    buffer.ops.add('*');
                                buffer.terminos.add(termino.incognita);
                                need = handleNeed(1, 0, 0, 1);
                            } else { // o si no...
                                if (need.equals(handleNeed(1, 1, 1, 1)))
                                    warning = "Error: Invalid char at " + i + "\n Lado nmr " + lado;
                                if (need.equals(handleNeed(1, 0, 0, 1)))
                                    warning = "Error: bad char after x " + i + "\n Lado nmr " + lado;
                                return false;
                            }
                        }
                    }
                }
            }
            if (lado == 1)
                lado1 = buffer;
            else
                lado2 = buffer;
            return true;
        }

        private static boolean[] handleNeed(int a, int b, int c, int d) {
            boolean need[] = { (a == 1) ? true : false, (b == 1) ? true : false, (c == 1) ? true : false,
                    (d == 1) ? true : false };
            return need;
        }
    }

    enum termino {
        cero(0.0, 0, "0"),
        positivo(1.0, 0, "+"),
        negativo(-1.0, 0, "-"),
        literal(0.0, 0, ""),
        anidado(0.0, 0, ""),
        incognita(1.0, 1, "x"),
        nomio(0.0, 0, "");

        private String looks;
        private Pair<Double, Integer> data;
        private xprsnAlge inter;

        termino(Double coef, Integer grado, String looks) {
            this.data = new Pair<Double, Integer>(coef, grado);
            this.looks = looks;
        }
    }

    private static xprsnAlge lado1;
    private static xprsnAlge lado2;
    private static String warning = "Equation hasn't been initialized";
    private static String metodo;

    public static boolean onNewEcu(String ecuacion) {
        int indexOfequals = ecuacion.indexOf("=");
        if (indexOfequals == -1) {
            warning = "No se encontro ningun signo '=' \nDeben de haber 2 expresiones para resolver una ecuacion";
            return false;
        }
        if (ecuacion.indexOf("=", ecuacion.indexOf("=")) != -1) {
            warning = "Solo pueden haber 2 expresiones a comparar";
            return false;
        }
        return true;
    }

    static int getIndexof(char s, char l[]) {
        for (int i = 0; i < l.length; i++) {
            if (s == l[i])
                return i;
        }
        return -1;
    }
}
