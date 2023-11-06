package Implementazione.Esempi;

import Implementazione.Corpo;
import Implementazione.Grammatica;
import Implementazione.ParserTopDown;
import Implementazione.ParserUtility;

public class GrammaticaTot14_02_22NonRic {
    public static void main(String[] args) {
        Grammatica grammatica = new Grammatica();
        grammatica.setNonTerminali("R", "R'", "T", "T'", "F", "F'");
        grammatica.setTerminali("a", "b", "p", "k", "eps");
        grammatica.setPartenza(grammatica.getNonTermSeEsiste("R"));

        // R -> TR'
        grammatica.addProduzione("R", new Corpo(grammatica, "T", "R'"));

        // R' -> pTR'
        grammatica.addProduzione("R'", new Corpo(grammatica, "p", "T", "R'"));

        // R' -> eps
        grammatica.addProduzione("R'", new Corpo(grammatica, "eps"));

        // T -> FT'
        grammatica.addProduzione("T", new Corpo(grammatica, "F", "T'"));

        // T' -> FT'
        grammatica.addProduzione("T'", new Corpo(grammatica, "F", "T'"));

        // T' -> eps
        grammatica.addProduzione("T'", new Corpo(grammatica, "eps"));

        // F -> aF'
        grammatica.addProduzione("F", new Corpo(grammatica, "a", "F'"));

        // F -> bF'
        grammatica.addProduzione("F", new Corpo(grammatica, "b", "F'"));

        // F' -> kF'
        grammatica.addProduzione("F'", new Corpo(grammatica, "k", "F'"));

        // F' -> kF'
        grammatica.addProduzione("F'", new Corpo(grammatica, "eps"));

        System.out.println(grammatica);
        ParserUtility parserUtility = new ParserUtility(grammatica);
        System.out.println(parserUtility.firstFollowTable());
        ParserTopDown parserTopDown = new ParserTopDown(grammatica);
        System.out.println(parserTopDown.getParsingTableToString());
        try {
            System.out.println(parserTopDown.parsing("a", "k"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
