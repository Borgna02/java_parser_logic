package Implementazione.Esempi;

import Implementazione.Corpo;
import Implementazione.Grammatica;
import Implementazione.ParserTopDown;
import Implementazione.ParserUtility;

public class GrammaticaTot14_02_22 {
    public static void main(String[] args) {
        Grammatica grammatica = new Grammatica();
        grammatica.setNonTerminali("R", "T", "F");
        grammatica.setTerminali("a", "b", "p", "k", "eps");
        grammatica.setPartenza(grammatica.getNonTermSeEsiste("R"));

        // R -> RpT
        grammatica.addProduzione("R", new Corpo(grammatica, "R", "p", "T"));
        // R -> T
        grammatica.addProduzione("R", new Corpo(grammatica, "T"));
        // T -> TF
        grammatica.addProduzione("T", new Corpo(grammatica, "T", "F"));
        // T -> F
        grammatica.addProduzione("T", new Corpo(grammatica, "F"));
        // F -> Fk
        grammatica.addProduzione("F", new Corpo(grammatica, "F", "k"));
        // F -> a
        grammatica.addProduzione("F", new Corpo(grammatica, "a"));
        // F -> b
        grammatica.addProduzione("F", new Corpo(grammatica, "b"));

        System.out.println(grammatica);
        grammatica.makeNonRecursive();
        System.out.println(grammatica);
        ParserUtility parserUtility = new ParserUtility(grammatica);
        System.out.println(parserUtility.firstFollowTable());
        ParserTopDown parserTopDown = new ParserTopDown(grammatica);
        try {
            System.out.println(parserTopDown.getParsingTableToString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
    }
}
