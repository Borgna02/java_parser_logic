package Implementazione;

import java.util.LinkedHashSet;

public class ParserSLR {
    private Grammatica grammatica;
    private ParserUtility parserUtility;
    private Terminale epsilon;

    public ParserSLR(Grammatica grammatica) {
        this.grammatica = grammatica;
        this.parserUtility = new ParserUtility(grammatica);
        this.epsilon = grammatica.getTermSeEsiste("eps");
    }

    public LinkedHashSet<ItemSLR> closure(LinkedHashSet<ItemSLR> itemSet) {
        LinkedHashSet<ItemSLR> result = new LinkedHashSet<>();
        // Per definizione l'input è contenuto nella closure
        result.addAll(itemSet);
        for (ItemSLR item : itemSet) {
            Simbolo simboloPuntato = item.getSimboloPuntato();
            // Se il simbolo puntato è un non terminale
            if (simboloPuntato != null && simboloPuntato instanceof NonTerminale) {
                // Aggiungo item relativi a tutte le produzioni con il simbolo in testa e il
                // puntatore al primo simbolo del corpo
                for (Produzione produzione : this.grammatica.getProduzioniByTesta(simboloPuntato)) {
                    ItemSLR newItem = new ItemSLR(produzione, 0);
                    // result.add restituisce false se il simbolo era già presente nell'insieme.
                    // Inserisco ciò nell'if per evitare cicli infiniti
                    // Se ho aggiunto un item con un non terminale puntato, devo aggiungere anche la
                    // closure di tale insieme
                    if (result.add(newItem) && produzione.getCorpo().getSimboli().getFirst() instanceof NonTerminale) {
                        LinkedHashSet<ItemSLR> inputNuovaClosure = new LinkedHashSet<>();
                        inputNuovaClosure.add(newItem);
                        result.addAll(closure(inputNuovaClosure));
                    }
                }
            }
        }

        return result;
    }

    public LinkedHashSet<ItemSLR> goTo(LinkedHashSet<ItemSLR> itemSet, Simbolo simbolo) {
        LinkedHashSet<ItemSLR> result = new LinkedHashSet<>();

        for (ItemSLR item : itemSet) {
            // Devo creare una copia per valore dell'item, altrimenti vado a shiftare il
            // puntatore nell'item originale
            ItemSLR newItem = new ItemSLR(item.getProduzione(), item.getIndicePuntatore());
            if (newItem.getSimboloPuntato() != null && newItem.getSimboloPuntato().equals(simbolo)) {
                newItem.shiftPuntatore();
                result.add(newItem);
            }

        }
        return closure(result);
    }
}
