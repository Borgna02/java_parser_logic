package Implementazione;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

public class ParserSLR {
    private Grammatica grammaticaAumentata;

    private ParserUtility parserUtility;
    private Terminale epsilon;

    public Grammatica getGrammaticaAumentata() {
        return grammaticaAumentata;
    }

    public Produzione getProduzionePartenza() {
        LinkedHashSet<Produzione> produzioni = this.grammaticaAumentata
                .getProduzioniByTesta(this.grammaticaAumentata.getPartenza());
        // Per costruzione della grammatica aumentata, sono sicuro che viene restituita
        // solo la produzione cercatax
        return produzioni.iterator().next();
    }

    public ParserSLR(Grammatica grammatica) {
        // Creo una copia per valore della grammatica
        this.grammaticaAumentata = new Grammatica();
        this.grammaticaAumentata.setProduzioni(grammatica.getProduzioni());
        this.grammaticaAumentata.setTerminali(grammatica.getTerminali());
        this.grammaticaAumentata.setNonTerminali(grammatica.getNonTerminali());
        // Aggiungo il nuovo simbolo e la nuova produzione
        // Lo indico con * per non andare in conflitto con eventuali simboli secondi
        this.grammaticaAumentata.addNonTerminale(grammatica.getPartenza().toString() + "*");
        this.grammaticaAumentata
                .setPartenza(this.grammaticaAumentata.getNonTermSeEsiste(grammatica.getPartenza().toString() + "*"));
        this.grammaticaAumentata.addProduzione(grammaticaAumentata.getPartenza().toString(),
                new Corpo(grammatica.getPartenza()));

        this.parserUtility = new ParserUtility(grammatica);
        this.epsilon = grammatica.getTermSeEsiste("eps");

        System.out.println(grammaticaAumentata);
    }

    public ItemSet closure(ItemSet itemSet) {
        ItemSet result = new ItemSet();
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
                        ItemSet inputNuovaClosure = new ItemSet();
                        inputNuovaClosure.add(newItem);
                        result.addAll(closure(inputNuovaClosure));
                    }
                }
            }
        }

        return result;
    }

    public ItemSet goTo(ItemSet itemSet, Simbolo simbolo) {
        ItemSet result = new ItemSet();

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

    public class SLRindice {
        private ItemSet itemSet;
        private int itemSetIndex;

        public SLRindice(ItemSet itemSet, int itemSetIndex) {
            this.itemSet = itemSet;
            this.itemSetIndex = itemSetIndex;
        }

        public ItemSet getItemSet() {
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

        private ParserSLR getEnclosingInstance() {
            return ParserSLR.this;
        }

        @Override
        public String toString() {
            return "SLRindice [itemSet=" + itemSet + ", itemSetIndex=" + itemSetIndex + "]";
        }

    }

    public LinkedHashMap<SLRindice, LinkedHashMap<Simbolo, SLRindice>> getAutomaLR0() {
        LinkedHashMap<SLRindice, LinkedHashMap<Simbolo, SLRindice>> automa = new LinkedHashMap<>();
        // Recupero tutti i simboli della grammatica esclusi il nuovo simbolo di
        // partenza e epsilon
        LinkedHashSet<Simbolo> alfabeto = new LinkedHashSet<>(this.grammaticaAumentata.getNonTerminali());
        alfabeto.remove(this.grammaticaAumentata.getPartenza());
        alfabeto.addAll(this.grammaticaAumentata.getTerminali());
        alfabeto.remove(this.epsilon);

        // Passo 0: aggiungo nella prima riga la closure della produzione iniziale
        SLRindice indiceAttuale = new SLRindice(closure(new ItemSet(new ItemSLR(this.getProduzionePartenza(), 0))), 0);
        automa.put(indiceAttuale, new LinkedHashMap<Simbolo, SLRindice>());

        int indiceUltimaAggiunta = 0;
        while (indiceAttuale.getItemSetIndex() <= indiceUltimaAggiunta) {
            for (Simbolo simbolo : alfabeto) {
                if (indiceAttuale.getItemSet().getSimboliPuntati().contains(simbolo)) {

                    ItemSet goToAttuale = goTo(indiceAttuale.getItemSet(), simbolo);
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

    public String getAutomaLR0ToString() {
        LinkedHashMap<SLRindice, LinkedHashMap<Simbolo, SLRindice>> automa = getAutomaLR0();
        StringBuilder result = new StringBuilder();
        Set<SLRindice> indici = automa.keySet();

        LinkedHashSet<Simbolo> alfabeto = new LinkedHashSet<>(this.grammaticaAumentata.getNonTerminali());
        alfabeto.remove(this.grammaticaAumentata.getPartenza());
        alfabeto.addAll(this.grammaticaAumentata.getTerminali());
        alfabeto.remove(this.epsilon);

        // Conto il numero di items presenti nella tabella. Per ogni item dovrò avere
        // una riga
        int numItems = 0;
        for (SLRindice indice : automa.keySet()) {
            numItems += indice.getItemSet().size();
        }

        // Devo costruire una tabella che ha per colonne i simboli e per righe gli
        // itemSet dell'automa
        int numColonne = alfabeto.size() + 2;
        // Aggiungo keySet().size() per inserire delle righe di separazione tra gli itemset
        int numRighe = numItems + 1 + automa.keySet().size();

        String[][] table = new String[numRighe][numColonne];
        table[0][0] = "itemSet";
        table[0][1] = "indice";

        // Riempio le intestazioni
        int col = 2;
        for (Simbolo simbolo : alfabeto) {
            table[0][col] = simbolo.toString();
            col++;
        }
        int row = 1;
        for (SLRindice indice : indici) {
            int itemIndex = 0;
            for(int i = 0; i < numColonne; i++) {
                table[row][i] = "----------------";
            }
            row++;
            for (ItemSLR item : indice.getItemSet()) {
                table[row][0] = item.toString();
                if (itemIndex == 0) {
                    table[row][1] = "I"+Integer.toString(indice.getItemSetIndex());
                } else {
                    table[row][1] = " ";
                }
                col = 2;
                for (Simbolo simbolo : alfabeto) {
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
                for (int k = table[r][c].length(); k < 15; k++) {
                    if (k == 12)
                        result.append("|");
                    result.append(" ");
                }
            }
            result.append("\n");
        }

        result.append("\n");

        return result.toString();

    }

}
