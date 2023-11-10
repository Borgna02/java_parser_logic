package Implementazione.Esempi;

import Implementazione.Domain.Corpo;
import Implementazione.Domain.Grammatica;
import Implementazione.Parser.ParserUtility;
import Implementazione.Parser.ParserBottomUp.ParserSLR.ParserSLR;
import Implementazione.Parser.ParserTopDown.ParserTopDown;

public class GrammaticaeTaH {
    public static void main(String[] args) {
        Grammatica grammatica = new Grammatica();
        grammatica.setNonTerminali("S","T","H");
        grammatica.setTerminali("a","d","e","l","eps");
        grammatica.setPartenza(grammatica.getNonTermSeEsiste("S"));

        // S -> eTaH
        grammatica.addProduzione("S",new Corpo(grammatica, "e","T","a","H"));
        // S -> aH
        grammatica.addProduzione("S",new Corpo(grammatica, "a","H"));
        // T -> l
        grammatica.addProduzione("T",new Corpo(grammatica, "l"));
        // T -> eps
        grammatica.addProduzione("T",new Corpo(grammatica, "eps"));
        // H -> dT
        grammatica.addProduzione("H",new Corpo(grammatica, "d","T"));

        System.out.println(grammatica);
        ParserUtility parserUtility = new ParserUtility(grammatica);
        System.out.println(parserUtility.firstFollowTable());
        ParserTopDown parserTopDown = new ParserTopDown(grammatica);
        ParserSLR parserSLR = new ParserSLR(grammatica);
        System.out.println(parserTopDown.getParsingTable());

        try {
            System.out.println(parserTopDown.parsing("e","l","a","d"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println(parserSLR.getAutomaLR0());
        System.out.println(parserSLR.getParsingTableLR0());
    }    
}
