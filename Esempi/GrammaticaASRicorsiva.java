package Implementazione.Esempi;

import Implementazione.Corpo;
import Implementazione.Grammatica;
import Implementazione.ParserTopDown;
import Implementazione.ParserUtility;

public class GrammaticaASRicorsiva {
    public static void main(String[] args) {
        Grammatica grammatica = new Grammatica();
        grammatica.setNonTerminali("S", "A");
        grammatica.setTerminali("a", "b", "c", "d", "eps");
        grammatica.setPartenza(grammatica.getNonTermSeEsiste("S"));

        // S -> Aa
        grammatica.addProduzione("S", new Corpo(grammatica, "A", "a"));
        // S -> b
        grammatica.addProduzione("S", new Corpo(grammatica, "b"));
        // A -> Ac
        grammatica.addProduzione("A", new Corpo(grammatica, "A", "c"));
        // A -> Sd
        grammatica.addProduzione("A", new Corpo(grammatica, "S", "d"));
        // A -> eps
        grammatica.addProduzione("A", new Corpo(grammatica, "eps"));

        ParserUtility parserUtility = new ParserUtility(grammatica);
        System.out.println(parserUtility.firstFollowTable());
        ParserTopDown parser = new ParserTopDown(grammatica);
        try {
            System.out.println(parser.getParsingTableToString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }   
}
