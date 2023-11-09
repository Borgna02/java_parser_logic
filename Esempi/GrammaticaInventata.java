package Implementazione.Esempi;

import Implementazione.Domain.Corpo;
import Implementazione.Domain.Grammatica;
import Implementazione.Parser.ParserUtility;
import Implementazione.Parser.ParserTopDown.ParserTopDown;

class GrammaticaInventata {
        public static void main(String[] args) {

                Grammatica grammatica = new Grammatica();
                grammatica.setTerminali("a", "c", "d", "z", "eps");
                grammatica.setNonTerminali("A", "B", "E");
                grammatica.setPartenza(grammatica.getNonTermSeEsiste("A"));

                // A -> AB
                grammatica.addProduzione("A", new Corpo(grammatica, "A", "B"));
                // A -> EA
                grammatica.addProduzione("A", new Corpo(grammatica, "E", "A"));
                // A -> a
                grammatica.addProduzione("A", new Corpo(grammatica, "a"));
                // E -> z
                grammatica.addProduzione("E", new Corpo(grammatica, "z"));
                // E -> eps
                grammatica.addProduzione("E", new Corpo(grammatica, "eps"));
                // B -> c
                grammatica.addProduzione("B", new Corpo(grammatica, "c"));
                // B -> d
                grammatica.addProduzione("B", new Corpo(grammatica, "d"));

                System.out.println(grammatica);
                ParserUtility parserUtility = new ParserUtility(grammatica);
                System.out.println(parserUtility.firstFollowTable());
                ParserTopDown parserTopDown = new ParserTopDown(grammatica);
                System.out.println(parserTopDown.getParsingTableToString());

                grammatica.makeNonRecursive();

                System.out.println(grammatica);
                parserUtility = new ParserUtility(grammatica);
                System.out.println(parserUtility.firstFollowTable());

                parserTopDown = new ParserTopDown(grammatica);
                System.out.println(parserTopDown.getParsingTableToString());

        }
}
