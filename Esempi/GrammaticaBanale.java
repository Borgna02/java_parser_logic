package Implementazione.Esempi;


import Implementazione.Domain.Corpo;
import Implementazione.Domain.Grammatica;
import Implementazione.Parser.ParserBottomUp.ParserSLR.ParserSLR;

public class GrammaticaBanale {
    public static void main(String[] args) {
        Grammatica grammatica = new Grammatica();
        grammatica.setNonTerminali("A", "B");
        grammatica.setTerminali("b", "c", "eps");
        grammatica.setPartenza(grammatica.getNonTermSeEsiste("A"));

        // A -> BA
        grammatica.addProduzione("A", new Corpo(grammatica, "B", "A"));
        // A -> Ac
        grammatica.addProduzione("A", new Corpo(grammatica, "A", "c"));
        // A -> b
        grammatica.addProduzione("A", new Corpo(grammatica, "b"));
        // B -> c
        grammatica.addProduzione("B", new Corpo(grammatica, "c"));
        // B -> eps
        grammatica.addProduzione("B", new Corpo(grammatica, "eps"));

        ParserSLR parser = new ParserSLR(grammatica);
        System.out.println(parser.getParsingTableLR0());


    }
}
