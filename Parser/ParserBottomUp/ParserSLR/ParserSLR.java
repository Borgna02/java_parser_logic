package Implementazione.Parser.ParserBottomUp.ParserSLR;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import Implementazione.Domain.*;
import Implementazione.Parser.ParserUtility;
import Implementazione.Parser.ParserBottomUp.Action;
import Implementazione.Parser.ParserBottomUp.Action.ActionType;

public class ParserSLR {
    

    private Grammatica grammaticaAumentata;
    private LinkedHashSet<Simbolo> alfabeto;
    private ParserUtility parserUtility;
    private Terminale epsilon;
    private AutomaSLR automaLR0;
    private ParsingTableSLR parsingTableLR0;
    private LinkedHashMap<Produzione, Integer> produzioniOrdinate;

    public ParserSLR(Grammatica grammatica) {
        this.epsilon = grammatica.getTermSeEsiste("eps");
        this.parserUtility = new ParserUtility(grammatica);

        // Creo una copia per valore della grammatica
        this.grammaticaAumentata = this.calculateGrammaticaAumentata(grammatica);
        this.alfabeto = this.calculateAlfabeto();
        this.produzioniOrdinate = this.ordinaProduzioni();

        this.parserUtility = new ParserUtility(grammatica);

        this.automaLR0 = this.calculateAutomaLR0();
        this.parsingTableLR0 = this.calculateParsingTableLR0();

    }

    public ParsingTableSLR getParsingTableLR0() {
        return parsingTableLR0;
    }

    public AutomaSLR getAutomaLR0() {
        return automaLR0;
    }

    private LinkedHashSet<Simbolo> calculateAlfabeto() {
        // Recupero tutti i simboli della grammatica originale, tranne epsilon
        LinkedHashSet<Simbolo> alfabeto = new LinkedHashSet<>(this.grammaticaAumentata.getNonTerminali());
        alfabeto.remove(this.grammaticaAumentata.getPartenza());
        alfabeto.addAll(this.grammaticaAumentata.getTerminali());
        alfabeto.remove(this.epsilon);
        return alfabeto;
    }

    private ParsingTableSLR calculateParsingTableLR0() {
        ParsingTableSLR table = new ParsingTableSLR(this.alfabeto, this.automaLR0.keySet());
        // Per ogni riga dell'automa
        for (IndiceSLR indiceRiga : this.automaLR0.keySet()) {
            LinkedHashMap<Simbolo, IndiceSLR> riga = this.automaLR0.get(indiceRiga);
            // Calcolo di shift e goto della riga
            for (Simbolo simbolo : riga.keySet()) {
                Action action = new Action();
                // Recupero l'indice della shift o goto (copia/incolla dall'automa)
                IndiceSLR indiceRisultato = this.automaLR0.get(indiceRiga).get(simbolo);
                if (indiceRisultato != null) {
                    // Scelgo quale azione devo inserire a seconda che il simbolo sia terminale o
                    // meno
                    action.setNumber(indiceRisultato.getItemSetIndex());

                    if (simbolo instanceof Terminale) {
                        action.setActionType(ActionType.SHIFT);
                    } else {
                        action.setActionType(ActionType.GOTO);
                    }

                    table.get(indiceRiga).get(simbolo).add(action);
                }
            }

            // Calcolo delle reduce della riga
            ItemSetSLR itemsConPuntatoreAllaFine = indiceRiga.getItemSet().getItemsByPuntatoreAllaFine();
            for (ItemSLR item : itemsConPuntatoreAllaFine) {
                for (Terminale terminale : this.parserUtility.getFollow(item.getProduzione().getTesta())) {
                    table.get(indiceRiga).get(terminale)
                            .add(new Action(ActionType.REDUCE, this.produzioniOrdinate.get(item.getProduzione())));
                }
            }
        }
        return table;
    }

    private Produzione getProduzionePartenza() {
        LinkedHashSet<Produzione> produzioni = this.grammaticaAumentata
                .getProduzioniByTesta(this.grammaticaAumentata.getPartenza());
        // Per costruzione della grammatica aumentata, sono sicuro che viene restituita
        // solo la produzione cercata
        return produzioni.iterator().next();
    }

    private LinkedHashMap<Produzione, Integer> ordinaProduzioni() {
        // Assegno ad ogni produzione un indice univoco
        LinkedHashMap<Produzione, Integer> produzioni = new LinkedHashMap<Produzione, Integer>();
        int i = 0;
        produzioni.put(this.getProduzionePartenza(), i++);
        for (Produzione produzione : this.grammaticaAumentata.getProduzioni()) {
            produzioni.putIfAbsent(produzione, i++);
        }
        return produzioni;
    }

    private ItemSetSLR closure(ItemSetSLR itemSet) {
        ItemSetSLR result = new ItemSetSLR();
        // Per definizione l'input è contenuto nella closure
        result.addAll(itemSet);
        for (ItemSLR item : itemSet) {
            Simbolo simboloPuntato = item.getSimboloPuntato();
            // Se il simbolo puntato è un non terminale
            if (simboloPuntato != null && simboloPuntato instanceof NonTerminale) {
                // Aggiungo item relativi a tutte le produzioni con il simbolo in testa e il
                // puntatore al primo simbolo del corpo
                for (Produzione produzione : this.grammaticaAumentata.getProduzioniByTesta(simboloPuntato)) {
                    ItemSLR newItem = new ItemSLR(produzione, 0);
                    // result.add restituisce false se il simbolo era già presente nell'insieme.
                    // Inserisco ciò nell'if per evitare cicli infiniti
                    // Se ho aggiunto un item con un non terminale puntato, devo aggiungere anche la
                    // closure di tale insieme
                    if (result.add(newItem) && produzione.getCorpo().getFirst() instanceof NonTerminale) {
                        ItemSetSLR inputNuovaClosure = new ItemSetSLR();
                        inputNuovaClosure.add(newItem);
                        result.addAll(closure(inputNuovaClosure));
                    }
                }
            }
        }

        return result;
    }

    private ItemSetSLR goTo(ItemSetSLR itemSet, Simbolo simbolo) {
        ItemSetSLR kernel = new ItemSetSLR();

        // Calcolo il kernel del nuovo itemSet
        for (ItemSLR item : itemSet) {
            // Devo creare una copia per valore dell'item, altrimenti vado a shiftare il
            // puntatore nell'item originale
            ItemSLR newItem = new ItemSLR(item.getProduzione(), item.getIndicePuntatore());
            if (newItem.getSimboloPuntato() != null && newItem.getSimboloPuntato().equals(simbolo)) {
                newItem.shiftPuntatore();
                kernel.add(newItem);
            }

        }
        // Restituisco la closure del kernel
        return closure(kernel);
    }

    private Grammatica calculateGrammaticaAumentata(Grammatica grammatica) {
        Grammatica grammaticaAumentata = new Grammatica();
        grammaticaAumentata.setProduzioni(grammatica.getProduzioni());
        grammaticaAumentata.setTerminali(grammatica.getTerminali());
        grammaticaAumentata.setNonTerminali(grammatica.getNonTerminali());
        // Aggiungo il nuovo simbolo e la nuova produzione
        // Lo indico con * per non andare in conflitto con eventuali simboli secondi
        grammaticaAumentata.addNonTerminale(grammatica.getPartenza().toString() + "*");
        grammaticaAumentata
                .setPartenza(grammaticaAumentata.getNonTermSeEsiste(grammatica.getPartenza().toString() + "*"));
        grammaticaAumentata.addProduzione(grammaticaAumentata.getPartenza().toString(),
                new Corpo(grammatica.getPartenza()));

        return grammaticaAumentata;
    }

    private AutomaSLR calculateAutomaLR0() {
        AutomaSLR automa = new AutomaSLR();
        // Recupero tutti i simboli della grammatica esclusi il nuovo simbolo di
        // partenza e epsilon
        LinkedHashSet<Simbolo> alfabeto = new LinkedHashSet<>(this.grammaticaAumentata.getNonTerminali());
        alfabeto.remove(this.grammaticaAumentata.getPartenza());
        alfabeto.addAll(this.grammaticaAumentata.getTerminali());
        alfabeto.remove(this.epsilon);

        // Passo 0: aggiungo nella prima riga la closure della produzione iniziale
        IndiceSLR indiceAttuale = new IndiceSLR(closure(new ItemSetSLR(new ItemSLR(this.getProduzionePartenza(), 0))),
                0);
        automa.put(indiceAttuale, new LinkedHashMap<Simbolo, IndiceSLR>());

        int indiceUltimaAggiunta = 0;
        // Finché raggiungo l'ultimo indice inserito
        while (indiceAttuale.getItemSetIndex() <= indiceUltimaAggiunta) {
            // Per ogni simbolo dell'alfabeto (tranne epsilon)
            for (Simbolo simbolo : alfabeto) {
                // Calcolo la goto per ogni simbolo dell'alfabeto
                if (indiceAttuale.getItemSet().getSimboliPuntati().contains(simbolo)) {
                    ItemSetSLR goToAttuale = goTo(indiceAttuale.getItemSet(), simbolo);
                    IndiceSLR newIndice = new IndiceSLR(goToAttuale, indiceUltimaAggiunta + 1);
                    // Se l'indice esisteva già, inserisco nella casella il suo indice
                    if (automa.containsKey(newIndice)) {
                        for (IndiceSLR indice : automa.keySet()) {
                            if (indice.equals(newIndice)) {
                                automa.get(indiceAttuale).put(simbolo, indice);
                            }
                        }
                    }
                    // Altrimenti creo il nuovo indice ed inserisco nella casella il suo indice
                    else {
                        automa.put(newIndice, new LinkedHashMap<Simbolo, IndiceSLR>());
                        automa.get(indiceAttuale).put(simbolo, newIndice);
                        indiceUltimaAggiunta++;
                    }
                }
                // Se il simbolo non era puntato da nessun item, inserisco null nella casella
                // del simbolo
                else {
                    automa.get(indiceAttuale).put(simbolo, null);
                }
            }

            // Se non ho aggiunto nuovi stati e sono arrivato all'ultimo, ho finito
            if (indiceAttuale.getItemSetIndex() == indiceUltimaAggiunta) {
                break;
            }

            // altrimenti recupero l'indice successivo
            for (IndiceSLR indice : automa.keySet()) {
                if (indice.getItemSetIndex() == indiceAttuale.getItemSetIndex() + 1) {
                    indiceAttuale = indice;
                    break;
                }
            }
        }

        return automa;

    }

}
