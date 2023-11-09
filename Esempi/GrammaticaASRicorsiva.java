package Implementazione.Esempi;

import Implementazione.Domain.Corpo;
import Implementazione.Domain.Grammatica;
import Implementazione.Parser.ParserUtility;
import Implementazione.Parser.ParserTopDown.ParserTopDown;

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
        System.out.println(parser.getParsingTable());

    }
}
