package Implementazione.Parser.ParserBottomUp.ParserSLR;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import Implementazione.Domain.*;
import Implementazione.Parser.ParserUtility;
import Implementazione.Parser.ParserBottomUp.Action;
import Implementazione.Parser.ParserBottomUp.Action.ActionType;

public class ParserSLR {
    public class SLRindice {
        private ItemSetSLR itemSet;
        private int itemSetIndex;

        public SLRindice(ItemSetSLR itemSet, int itemSetIndex) {
            this.itemSet = itemSet;
            this.itemSetIndex = itemSetIndex;
        }

        public ItemSetSLR getItemSet() {
            return this.itemSet;
        }

        public int getItemSetIndex() {
            return this.itemSetIndex;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getEnclosingInstance().hashCode();
            result = prime * result + ((itemSet == null) ? 0 : itemSet.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            SLRindice other = (SLRindice) obj;
            if (!getEnclosingInstance().equals(other.getEnclosingInstance()))
                return false;
            if (itemSet == null) {
                if (other.itemSet != null)
                    return false;
            } else if (!itemSet.equals(other.itemSet))
                return false;
            return true;
        }

        @Override
        public String toString() {
            return "SLRindice [itemSet=" + itemSet + ", itemSetIndex=" + itemSetIndex + "]";
        }

        private ParserSLR getEnclosingInstance() {
            return ParserSLR.this;
        }

    }

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

    public String getAutomaLR0ToString() {
        AutomaSLR automa = getAutomaLR0();
        StringBuilder result = new StringBuilder();
        Set<SLRindice> indici = automa.keySet();

        // Conto il numero di items presenti nella tabella. Per ogni item dovrò avere
        // una riga
        int numItems = 0;
        for (SLRindice indice : automa.keySet()) {
            numItems += indice.getItemSet().size();
        }

        // Devo costruire una tabella che ha per colonne i simboli e per righe gli
        // itemSet dell'automa
        int numColonne = this.alfabeto.size() + 2;
        // Aggiungo keySet().size() per inserire delle righe di separazione tra gli
        // itemset
        int numRighe = numItems + 1 + automa.keySet().size();

        String[][] table = new String[numRighe][numColonne];
        table[0][0] = "itemSet";
        table[0][1] = "indice";

        // Riempio le intestazioni
        int col = 2;
        for (Simbolo simbolo : this.alfabeto) {
            table[0][col] = simbolo.toString();
            col++;
        }
        int row = 1;
        for (SLRindice indice : indici) {
            int itemIndex = 0;
            for (int i = 0; i < numColonne; i++) {
                table[row][i] = "----------------";
            }
            row++;
            for (ItemSLR item : indice.getItemSet()) {
                table[row][0] = item.toString();
                if (itemIndex == 0) {
                    table[row][1] = "I" + Integer.toString(indice.getItemSetIndex());
                } else {
                    table[row][1] = " ";
                }
                col = 2;
                for (Simbolo simbolo : this.alfabeto) {
                    if (itemIndex == 0) {
                        if (automa.get(indice).get(simbolo) == null) {
                            table[row][col] = "-";

                        } else {
                            table[row][col] = Integer.toString(automa.get(indice).get(simbolo).getItemSetIndex());
                        }
                    } else {
                        table[row][col] = " ";
                    }
                    col++;
                }
                itemIndex++;
                row++;
            }

        }

        // Costruzione della tabella formattata
        result.append("\n");
        for (int r = 0; r < numRighe; r++) {
            for (int c = 0; c < numColonne; c++) {
                result.append(table[r][c]);
                // formattazione della tabella: aggiungo un numero di spazi dipendente dalla
                // lunghezza della stringa che ho inserito
                for (int k = table[r][c].length(); k < 15; k++) {
                    if (k == 14)
                        result.append("|");
                    result.append(" ");
                }
            }
            result.append("\n");
        }

        result.append("\n");

        return result.toString();

    }

    private LinkedHashSet<Simbolo> calculateAlfabeto() {
        LinkedHashSet<Simbolo> alfabeto = new LinkedHashSet<>(this.grammaticaAumentata.getNonTerminali());
        alfabeto.remove(this.grammaticaAumentata.getPartenza());
        alfabeto.addAll(this.grammaticaAumentata.getTerminali());
        alfabeto.remove(this.epsilon);
        return alfabeto;
    }

    private ParsingTableSLR calculateParsingTableLR0() {
        ParsingTableSLR table = new ParsingTableSLR(this.alfabeto, this.automaLR0.keySet());
        for (SLRindice indiceRiga : this.automaLR0.keySet()) {
            LinkedHashMap<Simbolo, SLRindice> riga = this.automaLR0.get(indiceRiga);
            for (Simbolo simbolo : riga.keySet()) {
                Action action = new Action();
                SLRindice indiceRisultato = this.automaLR0.get(indiceRiga).get(simbolo);
                if (indiceRisultato != null) {

                    action.setNumber(indiceRisultato.getItemSetIndex());

                    if (simbolo instanceof Terminale) {
                        action.setActionType(ActionType.SHIFT);
                    } else {
                        action.setActionType(ActionType.GOTO);
                    }
                    table.get(indiceRiga).get(simbolo).add(action);

                }
                
            }
            ItemSetSLR itemsConPuntatoreAllaFine = indiceRiga.getItemSet().getItemsByPuntatoreAllaFine();
            for (ItemSLR item : itemsConPuntatoreAllaFine) {
                for (Terminale terminale : this.parserUtility.getFollow(item.getProduzione().getTesta())) {
                    table.get(indiceRiga).get(terminale)
                    .add(new Action(ActionType.REDUCE, this.produzioniOrdinate.get(item.getProduzione())));
                }
            }
            System.out.println(indiceRiga.getItemSetIndex() + ": " + table.get(indiceRiga));
        }
        return table;
    }

    private Produzione getProduzionePartenza() {
        LinkedHashSet<Produzione> produzioni = this.grammaticaAumentata
                .getProduzioniByTesta(this.grammaticaAumentata.getPartenza());
        // Per costruzione della grammatica aumentata, sono sicuro che viene restituita
        // solo la produzione cercatax
        return produzioni.iterator().next();
    }

    private LinkedHashMap<Produzione, Integer> ordinaProduzioni() {
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
        ItemSetSLR result = new ItemSetSLR();

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
        SLRindice indiceAttuale = new SLRindice(closure(new ItemSetSLR(new ItemSLR(this.getProduzionePartenza(), 0))),
                0);
        automa.put(indiceAttuale, new LinkedHashMap<Simbolo, SLRindice>());

        int indiceUltimaAggiunta = 0;
        while (indiceAttuale.getItemSetIndex() <= indiceUltimaAggiunta) {
            for (Simbolo simbolo : alfabeto) {
                if (indiceAttuale.getItemSet().getSimboliPuntati().contains(simbolo)) {

                    ItemSetSLR goToAttuale = goTo(indiceAttuale.getItemSet(), simbolo);
                    SLRindice newIndice = new SLRindice(goToAttuale, indiceUltimaAggiunta + 1);
                    if (automa.containsKey(newIndice)) {
                        for (SLRindice indice : automa.keySet()) {
                            if (indice.equals(newIndice)) {
                                automa.get(indiceAttuale).put(simbolo, indice);
                            }
                        }
                    } else {
                        automa.put(newIndice, new LinkedHashMap<Simbolo, SLRindice>());
                        automa.get(indiceAttuale).put(simbolo, newIndice);
                        indiceUltimaAggiunta++;
                    }
                } else {
                    automa.get(indiceAttuale).put(simbolo, null);
                }
            }

            // Se non ho aggiunto nuovi stati e sono arrivato all'ultimo, ho finito
            if (indiceAttuale.getItemSetIndex() == indiceUltimaAggiunta) {
                break;
            }

            // altrimenti recupero l'indice successivo
            for (SLRindice indice : automa.keySet()) {
                if (indice.getItemSetIndex() == indiceAttuale.getItemSetIndex() + 1) {
                    indiceAttuale = indice;
                    break;
                }
            }
        }

        return automa;

    }

}
