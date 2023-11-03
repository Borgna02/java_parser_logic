package Implementazione.Esempi;

import Implementazione.Corpo;
import Implementazione.Grammatica;
import Implementazione.ParserTopDown;
import Implementazione.ParserUtility;

public class GrammaticaIfElseNonRicorsiva {
    public static void main(String[] args) {
        Grammatica grammatica = new Grammatica();

        grammatica.setNonTerminali("E", "E'", "T", "T'", "F");
        grammatica.setTerminali("+", "*", "(", ")", "id", "eps");
        grammatica.setPartenza(grammatica.getNonTermSeEsiste("E"));

        // E -> TE'
        grammatica.addProduzione("E", new Corpo(grammatica, "T", "E'"));
        // E -> T'
        grammatica.addProduzione("E", new Corpo(grammatica, "T'"));
        // E' -> +TE'
        grammatica.addProduzione("E'", new Corpo(grammatica, "+", "T", "E'"));
        // E' -> eps
        grammatica.addProduzione("E'", new Corpo(grammatica, "eps"));
        // T -> FT'
        grammatica.addProduzione("T", new Corpo(grammatica, "F", "T'"));
        // T' -> *FT'
        grammatica.addProduzione("T'", new Corpo(grammatica, "*", "F", "T'"));
        // T' -> eps
        grammatica.addProduzione("T'", new Corpo(grammatica, "eps"));
        // F -> (E)
        grammatica.addProduzione("F", new Corpo(grammatica, "(", "E", ")"));
        // F -> id
        grammatica.addProduzione("F", new Corpo(grammatica, "id"));

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
