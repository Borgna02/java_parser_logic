package Implementazione.Esempi;

import Implementazione.Domain.Corpo;
import Implementazione.Domain.Grammatica;
import Implementazione.Parser.ParserUtility;
import Implementazione.Parser.ParserTopDown.ParserTopDown;

public class GrammaticaInventata09_11 {
    public static void main(String[] args) {
        Grammatica grammatica = new Grammatica();
        grammatica.setNonTerminali("S","A","B");
        grammatica.setTerminali("a","b","c","eps");
        grammatica.setPartenza(grammatica.getNonTermSeEsiste("S"));

        // S -> A
        grammatica.addProduzione("S", new Corpo(grammatica, "A"));
        // S -> Ba
        grammatica.addProduzione("S", new Corpo(grammatica, "B","a"));
        // A -> cA
        grammatica.addProduzione("A", new Corpo(grammatica, "c","A"));
        // A -> BS
        grammatica.addProduzione("A", new Corpo(grammatica, "B","S"));
        // B -> a
        grammatica.addProduzione("B", new Corpo(grammatica, "a"));
        // B -> b
        grammatica.addProduzione("B", new Corpo(grammatica, "b"));
        // B -> eps
        grammatica.addProduzione("B", new Corpo(grammatica, "eps"));


        System.out.println(grammatica);
        ParserUtility utility = new ParserUtility(grammatica);
        System.out.println(utility.firstFollowTable());
        ParserTopDown parser = new ParserTopDown(grammatica);
        System.out.println(parser.getParsingTable());
    }   
}
