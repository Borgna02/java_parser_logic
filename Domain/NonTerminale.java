package Implementazione.Domain;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NonTerminale implements Simbolo {
    private String nonTerminale;

    public NonTerminale(String nonTerminale) throws IllegalArgumentException {
        // L'espressione regolare verifica se l'input è un singolo carattere maiuscolo
        // seguito da zero o un apice
        Pattern pattern = Pattern.compile("^[A-Z]('*|\\*)$");
        Matcher matcher = pattern.matcher(nonTerminale);

        if (!matcher.matches())
            throw new IllegalArgumentException("Input non valido: " + nonTerminale);
        this.nonTerminale = nonTerminale;
    }

    public String getSimbolo() {
        return nonTerminale;
    }

    @Override
    public String toString() {
        return this.nonTerminale;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NonTerminale) {
            String other = ((NonTerminale) obj).getSimbolo();
            return this.nonTerminale.equals(other);
        }
        return false;
    }

}
