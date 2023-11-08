package Implementazione.Esempi;

import java.util.LinkedHashSet;

import Implementazione.Corpo;
import Implementazione.Grammatica;
import Implementazione.Produzione;
import Implementazione.ItemSLR;
import Implementazione.ParserSLR;

public class GrammaticaBanale {
    public static void main(String[] args) {
        Grammatica grammatica = new Grammatica();
        grammatica.setNonTerminali("A'","A", "B");
        grammatica.setTerminali("a", "b", "c", "eps");
        grammatica.setPartenza(grammatica.getNonTermSeEsiste("A"));

        // A' -> A
        Produzione produzioneAp_A = new Produzione(grammatica, "A'", new Corpo(grammatica, "A"));
        grammatica.addProduzione(produzioneAp_A.getTesta().toString(), produzioneAp_A.getCorpo());
        // A -> BA
        Produzione produzioneA_BA = new Produzione(grammatica, "A", new Corpo(grammatica, "B", "A"));
        grammatica.addProduzione(produzioneA_BA.getTesta().toString(), produzioneA_BA.getCorpo());
        // A -> Ac
        Produzione produzioneA_Ac = new Produzione(grammatica, "A", new Corpo(grammatica, "A", "c"));
        grammatica.addProduzione(produzioneA_Ac.getTesta().toString(), produzioneA_Ac.getCorpo());
        // A -> b
        grammatica.addProduzione("A", new Corpo(grammatica, "b"));
        // B -> c
        grammatica.addProduzione("B", new Corpo(grammatica, "c"));
        // B -> eps
        grammatica.addProduzione("B", new Corpo(grammatica, "eps"));

        LinkedHashSet<ItemSLR> itemSet = new LinkedHashSet<>();
        itemSet.add(new ItemSLR(produzioneAp_A, 0));

        ParserSLR parser = new ParserSLR(grammatica);
        LinkedHashSet<ItemSLR> I0 = parser.closure(itemSet);
        System.out.println(I0);
        
        
        LinkedHashSet<ItemSLR> I1 = parser.goTo(I0, grammatica.getNonTermSeEsiste("A"));
        System.out.println(I1);
        LinkedHashSet<ItemSLR> I2 = parser.goTo(I0, grammatica.getNonTermSeEsiste("B"));
        System.out.println(I2);
        LinkedHashSet<ItemSLR> I3 = parser.goTo(I0, grammatica.getTermSeEsiste("b"));
        System.out.println(I3);
        LinkedHashSet<ItemSLR> I4 = parser.goTo(I0, grammatica.getTermSeEsiste("c"));
        System.out.println(I4);
        LinkedHashSet<ItemSLR> I5 = parser.goTo(I1, grammatica.getTermSeEsiste("c"));
        System.out.println(I5);
        

        


    }
}
