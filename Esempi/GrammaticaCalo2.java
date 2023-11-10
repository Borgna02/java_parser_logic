package Implementazione.Esempi;

import Implementazione.Domain.Corpo;
import Implementazione.Domain.Grammatica;
import Implementazione.Parser.ParserUtility;
import Implementazione.Parser.ParserBottomUp.ParserSLR.ParserSLR;
import Implementazione.Parser.ParserTopDown.ParserTopDown;

public class GrammaticaCalo2 {
    public static void main(String[] args) {
        Grammatica grammatica = new Grammatica();
        grammatica.setNonTerminali("S", "A", "B");
        grammatica.setTerminali("a", "b", "c", "d", "eps");
        grammatica.setPartenza(grammatica.getNonTermSeEsiste("S"));

        // S -> abA
        grammatica.addProduzione("S", new Corpo(grammatica, "a","b","A"));
        // S -> cAd
        grammatica.addProduzione("S", new Corpo(grammatica, "c","A","d"));
        // S -> Bd
        grammatica.addProduzione("S", new Corpo(grammatica, "B","d"));
        // S -> eps
        grammatica.addProduzione("S", new Corpo(grammatica, "eps"));
        // A -> bB
        grammatica.addProduzione("A", new Corpo(grammatica, "b","B"));
        // A -> aBc
        grammatica.addProduzione("A", new Corpo(grammatica, "a","B","c"));
        // B -> c
        grammatica.addProduzione("B", new Corpo(grammatica, "c"));

        System.out.println(grammatica);
        ParserUtility parserUtility = new ParserUtility(grammatica);
        System.out.println(parserUtility.firstFollowTable());
        ParserTopDown parserTopDown = new ParserTopDown(grammatica);
        ParserSLR parserSLR = new ParserSLR(grammatica);
        System.out.println(parserTopDown.getParsingTable());

        try {
            System.out.println(parserTopDown.parsing("c","a","c","c","d"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println(parserSLR.getAutomaLR0());
        System.out.println(parserSLR.getParsingTableLR0());
    }

}
