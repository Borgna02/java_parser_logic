package Implementazione.Esempi;

import Implementazione.Domain.Corpo;
import Implementazione.Domain.Grammatica;
import Implementazione.Parser.ParserUtility;
import Implementazione.Parser.ParserTopDown.ParserTopDown;

public class Grammatica01senzaRicFattorizzata {

    public static void main(String[] args) {
        Grammatica grammatica = new Grammatica();
        grammatica.setTerminali("0", "1", "eps");
        grammatica.setNonTerminali("S", "A", "A'", "A''");
        grammatica.setPartenza(grammatica.getNonTermSeEsiste("S"));

        // S -> A1A
        grammatica.addProduzione("S", new Corpo(grammatica, "A", "1", "A"));
        // A -> 1SA'
        grammatica.addProduzione("A", new Corpo(grammatica, "1", "S", "A'"));
        // A -> 0A'''
        grammatica.addProduzione("A", new Corpo(grammatica, "0", "A''"));
        // A' -> 1A1A'
        grammatica.addProduzione("A'", new Corpo(grammatica, "1", "A", "1", "A'"));
        // A' -> eps
        grammatica.addProduzione("A'", new Corpo(grammatica, "eps"));
        // A'' -> AA'
        grammatica.addProduzione("A''", new Corpo(grammatica, "A", "A'"));
        // A'' -> A'
        grammatica.addProduzione("A''", new Corpo(grammatica, "A'"));
        // A'' -> 1A''0
        grammatica.addProduzione("A''", new Corpo(grammatica, "1", "A''", "0"));

        System.out.println(grammatica);
        ParserUtility parserUtility = new ParserUtility(grammatica);
        System.out.println(parserUtility.firstFollowTable());

        ParserTopDown parser = new ParserTopDown(grammatica);
        System.out.println(parser.getParsingTable());
    }
}
