package Implementazione.Esempi;

import Implementazione.Domain.Corpo;
import Implementazione.Domain.Grammatica;
import Implementazione.Parser.ParserUtility;
import Implementazione.Parser.ParserTopDown.ParserTopDown;

public class GrammaticaIfElseRicorsiva {
    public static void main(String[] args) {

        Grammatica grammatica = new Grammatica();
        grammatica.setTerminali("*", "(", ")", "id", "+", "eps");
        grammatica.setNonTerminali("E", "T", "F");
        grammatica.setPartenza(grammatica.getNonTermSeEsiste("E"));

        // E -> E+T
        grammatica.addProduzione("E", new Corpo(grammatica, "E", "+", "F"));
        // E -> T
        grammatica.addProduzione("E", new Corpo(grammatica, "T"));
        // T -> T*F
        grammatica.addProduzione("T", new Corpo(grammatica, "T", "*", "F"));
        // T -> F
        grammatica.addProduzione("T", new Corpo(grammatica, "F"));
        // T -> eps
        grammatica.addProduzione("T", new Corpo(grammatica, "eps"));
        // F -> (E)
        grammatica.addProduzione("F", new Corpo(grammatica, "(", "E", ")"));
        // F -> id
        grammatica.addProduzione("F", new Corpo(grammatica, "id"));

        System.out.println(grammatica);
        ParserUtility parserUtility = new ParserUtility(grammatica);
        System.out.println(parserUtility.firstFollowTable());
        ParserTopDown parserTopDown = new ParserTopDown(grammatica);
        System.out.println(parserTopDown.getParsingTable());

    }
}
