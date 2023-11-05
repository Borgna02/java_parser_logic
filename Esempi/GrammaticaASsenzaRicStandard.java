package Implementazione.Esempi;

import Implementazione.Corpo;
import Implementazione.Grammatica;
import Implementazione.ParserUtility;

public class GrammaticaASsenzaRicStandard {
    public static void main(String[] args) {
        Grammatica grammatica = new Grammatica();
        grammatica.setNonTerminali("S", "A", "A'");
        grammatica.setTerminali("a", "b", "c", "d", "eps");
        grammatica.setPartenza(grammatica.getNonTermSeEsiste("S"));

        // S -> Aa
        grammatica.addProduzione("S", new Corpo(grammatica, "A", "a"));
        // S -> b
        grammatica.addProduzione("S", new Corpo(grammatica, "b"));
        // A -> bdA'
        grammatica.addProduzione("A", new Corpo(grammatica, "b", "d", "A'"));
        // A -> A'
        grammatica.addProduzione("A", new Corpo(grammatica, "A'"));
        // A' -> cA'
        grammatica.addProduzione("A'", new Corpo(grammatica, "c", "A'"));
        // A' -> adA'
        grammatica.addProduzione("A'", new Corpo(grammatica, "a", "d", "A'"));
        // A' -> eps
        grammatica.addProduzione("A'", new Corpo(grammatica, "eps"));

        System.out.println(grammatica);
        ParserUtility parserUtility = new ParserUtility(grammatica);
        System.out.println(parserUtility.firstFollowTable());

    }
}

