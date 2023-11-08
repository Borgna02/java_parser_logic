package Implementazione.Esempi;


import Implementazione.Corpo;
import Implementazione.Grammatica;
import Implementazione.ItemSLR;
import Implementazione.ItemSet;
import Implementazione.ParserSLR;

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

        System.out.println(grammatica);
        ParserSLR parser = new ParserSLR(grammatica);

        // parser.getAutomaLR0();
        System.out.println(parser.getAutomaLR0ToString());
        // System.out.println(parser.getAutomaLR0());

    }
}
