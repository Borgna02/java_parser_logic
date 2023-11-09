package Implementazione.Esempi;

import Implementazione.Domain.Corpo;
import Implementazione.Domain.Grammatica;
import Implementazione.Parser.ParserUtility;
import Implementazione.Parser.ParserTopDown.ParserTopDown;

public class GrammaticaEsercitazione06_11 {
    public static void main(String[] args) {
        Grammatica grammatica = new Grammatica();
        grammatica.setTerminali("a", "b", "c", "eps");
        grammatica.setNonTerminali("S", "A", "B");
        grammatica.setPartenza(grammatica.getNonTermSeEsiste("S"));

        // S -> AB
        grammatica.addProduzione("S", new Corpo(grammatica, "A", "B"));
        // A -> abAba
        grammatica.addProduzione("A", new Corpo(grammatica, "a", "b", "A", "b", "a"));
        // A -> abAba
        grammatica.addProduzione("A", new Corpo(grammatica, "c"));
        // B -> baBab
        grammatica.addProduzione("B", new Corpo(grammatica, "b", "a", "B", "a", "b"));
        // B -> c
        grammatica.addProduzione("B", new Corpo(grammatica, "c"));

        System.out.println(grammatica);

        ParserUtility parserUtility = new ParserUtility(grammatica);
        System.out.println(parserUtility.firstFollowTable());
        ParserTopDown parser = new ParserTopDown(grammatica);
        System.out.println(parser.getParsingTable());
        try {
            System.out.println(parser.parsing("a", "b", "c", "b", "a", "b", "a", "b", "a", "c", "a", "b", "a", "b"));
        } catch (Exception e) {
            e.getMessage();
        }
    }
}
