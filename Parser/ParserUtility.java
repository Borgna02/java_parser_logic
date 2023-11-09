package Implementazione.Parser;

import java.util.LinkedList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;

import Implementazione.Domain.*;

public final class ParserUtility {

    private class StrutturaFollow {
        // parziale contiene i terminali aggiunti alla follow finora
        Follow parziale;
        // follows contiene i non terminali la cui follow fa parte di quella che stiamo
        // calcolando
        LinkedHashSet<NonTerminale> follows;

        public StrutturaFollow(Grammatica grammatica, NonTerminale nonTerminale, Terminale FINESTRINGA) {
            this.parziale = new Follow();
            this.follows = new LinkedHashSet<NonTerminale>();
            if (grammatica.getPartenza().equals(nonTerminale)) {
                this.parziale.add(FINESTRINGA);
            }
        }

        public void addToParziale(First terminali) {
            this.parziale.addAll(terminali);
        }

        public void addFollow(NonTerminale nonTerminale) {
            this.follows.add(nonTerminale);
        }

        public Follow getParziale() {
            return this.parziale;
        }

        public LinkedHashSet<NonTerminale> getFollows() {
            return this.follows;
        }
    }
    public static final Terminale FINESTRINGA = new Terminale("$");
    private Grammatica grammatica;
    private Terminale epsilon;
    private LinkedHashMap<Simbolo, First> firsts = new LinkedHashMap<>();
    private LinkedHashMap<NonTerminale, Follow> follows;

    private LinkedHashSet<NonTerminale> calculatingFirst = new LinkedHashSet<>();

    public ParserUtility(Grammatica grammatica) {
        this.grammatica = grammatica;
        this.epsilon = grammatica.getTermSeEsiste("eps");
        calculateFirsts();
        this.follows = calculateFollows();
    }

    // ! PARTE DI RESTITUIZIONE DELLE FIRST E FOLLOW GIÀ CALCOLATE
    public LinkedHashMap<Simbolo, First> getFirsts() {
        return firsts;
    }

    public First getSymbolFirst(Simbolo simbolo) {
        return this.firsts.get(simbolo);
    }

    

    public LinkedHashMap<NonTerminale, Follow> getFollows() {
        return follows;
    }

    public Follow getFollow(NonTerminale nonTerminale) {
        return this.follows.get(nonTerminale);
    }

    // ! STAMPA DELLA TABELLA FIRST/FOLLOW
    public String firstFollowTable() {

        List<NonTerminale> nonTerminali = new LinkedList<>(this.grammatica.getNonTerminali());
        List<Terminale> terminali = new LinkedList<>(this.grammatica.getTerminali());
        // Non calcolo first e follow di epsilon
        if (terminali.contains(this.epsilon))
            terminali.remove(this.epsilon);
        int numNonTerminali = nonTerminali.size();
        int numTerminali = terminali.size();

        // Costruisci una tabella vuota di altezza 3 (header, first e follow) e
        // larghezza ...
        String[][] table = new String[3][numTerminali +
                numNonTerminali + 1];

        table[0][0] = "simbolo";
        table[1][0] = "first";
        table[2][0] = "follow";

        // Popola la tabella con le intestazioni delle colonne
        int i = 0;
        for (i = 0; i < numNonTerminali; i++) {
            table[0][i + 1] = nonTerminali.get(i).toString();
            NonTerminale nonTerminale = nonTerminali.get(i);
            String first = this.getSymbolFirst(nonTerminale).toString();
            String follow = this.getFollow(nonTerminale).toString();
            table[1][i + 1] = first;
            table[2][i + 1] = follow;
        }

        for (int j = i; j < numNonTerminali + numTerminali; j++) {
            table[0][j + 1] = terminali.get(j - numNonTerminali).toString();
            Terminale terminale = terminali.get(j - numNonTerminali);
            String first = "[" + terminale.toString() + "]";
            table[1][j + 1] = first;
            table[2][j + 1] = "-";
        }

        // Costruisci una stringa rappresentante la tabella
        StringBuilder result = new StringBuilder();
        result.append("\n");
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < numTerminali + numNonTerminali + 1; col++) {
                result.append(table[row][col]);
                // formattazione della tabella: aggiungo un numero di spazi dipendente dalla
                for (int k = table[row][col].length(); k < 20; k++) {
                    if (k == 17)
                        result.append("|");
                    result.append(" ");
                }

            }
            result.append("\n");
        }
        result.append("\n");

        return result.toString();

    }

    public First calculateStringFirst(LinkedList<Simbolo> simboli) {
        // Quando chiamo questo metodo, sono sicuro che tutte le first di tutti i
        // simboli della grammatica sono già conosciute. Devo quindi soltanto
        // recuperarle finché non trovo un simbolo terminale o uno con epsilon nella
        // first
        First first = new First();
        Iterator<Simbolo> iterator = simboli.iterator();
        while (iterator.hasNext()) {
            Simbolo simbolo = iterator.next();
            First firstAttuale = new First(this.firsts.get(simbolo));

            if (!firstAttuale.contains(this.epsilon)) {
                first.addAll(firstAttuale);
                break;
            } else {
                if (iterator.hasNext()) {
                    firstAttuale.remove(this.epsilon);
                }
                first.addAll(firstAttuale);
            }
        }
        return first;
    }

    // ! PARTE DI CALCOLO DELLE FIRST
    private void calculateFirsts() {
        // Per le first dei terminali, basta inserire i terminali stessi
        for (Terminale terminale : this.grammatica.getTerminali()) {
            if (!terminale.equals(this.epsilon)) {
                First first = new First();
                first.add(terminale);
                this.firsts.putIfAbsent(terminale, first);
            }
        }
        for (NonTerminale nonTerminale : this.grammatica.getNonTerminali()) {
            if (!this.firsts.containsKey(nonTerminale)) {
                calculateNonTerminaleFirst(nonTerminale);
            }
        }
    }

    /**
     * Passo base per il calcolo della first di un simbolo
     */
    private void calculateNonTerminaleFirst(NonTerminale simbolo) {
        this.calculatingFirst.add(simbolo);
        First first = new First();
        // Se il simbolo è un non terminale, calcolo le first di tutti i corpi delle
        // produzioni

        // Cerco le produzioni che hanno il simbolo in testa e non sono ricorsive
        LinkedHashSet<Produzione> produzioni = this.grammatica.getProduzioniByTestaNonRicorsive(simbolo);

        for (Produzione produzione : produzioni) {

            // Se il corpo è composto da un solo simbolo
            if (produzione.getCorpo().size() == 1) {
                Simbolo corpo = produzione.getCorpo().get(0);
                if (corpo.equals(this.epsilon)) {
                    first.add(this.epsilon);
                }
                // Se corpo è un terminale entro sempre in questo if, quindi nell'else sono
                // sicuro che ho un non terminale
                else {
                    // Se non ho già calcolato quella first, allora la calcolo e la inserisco
                    if (!this.firsts.containsKey(corpo)) {
                        calculateNonTerminaleFirst((NonTerminale) corpo);
                    }
                    // La aggiungo alla first che sto calcolando
                    first.addAll(this.firsts.get(corpo));
                }
            }
            // Se il corpo è composto da più simboli
            else {
                Iterator<Simbolo> iterator = produzione.getCorpo().iterator();
                while (iterator.hasNext()) {
                    Simbolo simboloDelCorpo = iterator.next();
                    // Se il simbolo è un non terminale per cui già stavo calcolando la first,
                    // allora esco dal ciclo. Ciò mi permette di evitare ricorsioni infinite.
                    if (this.calculatingFirst.contains(simboloDelCorpo)) {
                        break;
                    }
                    // Se il simbolo è un terminale, lo aggiungo alla first ed esco dal ciclo
                    if (simboloDelCorpo instanceof Terminale) {
                        first.add((Terminale) simboloDelCorpo);
                        break;
                    } else {
                        // Se non ho già calcolato quella first, allora la calcolo e la inserisco
                        if (!this.firsts.containsKey(simboloDelCorpo)) {
                            this.calculateNonTerminaleFirst((NonTerminale) simboloDelCorpo);
                        }
                        // Recupero la first di cui ho bisogno e la aggiungo
                        First firstAttuale = new First(this.firsts.get(simboloDelCorpo));
                        // Se il simbolo è annullabile e non è l'ultimo del corpo, allora rimuovo
                        // epsilon e passo al simbolo successivo
                        if (firstAttuale.contains(this.epsilon) && iterator.hasNext()) {
                            firstAttuale.remove(this.epsilon);
                            first.addAll(firstAttuale);

                        }
                        // Se invece non è annullabile, inserisco la first e termino
                        else {
                            first.addAll(firstAttuale);
                            break;
                        }
                    }

                }
            }
        }

        this.firsts.put(simbolo, first);
        this.calculatingFirst.remove(simbolo);
    }

    // ! PARTE DI CALCOLO DELLE FOLLOW
    private LinkedHashMap<NonTerminale, Follow> calculateFollows() {
        // resultParziali contiene, per ogni nonTerminale, il parziale attuale e la
        // lista di nonTerminali la cui follow appartiene alla follow del nonTerminale
        LinkedHashMap<NonTerminale, StrutturaFollow> resultParziali = new LinkedHashMap<>();
        LinkedHashMap<NonTerminale, Follow> result = new LinkedHashMap<>();

        // Inizializzo la mappa aggiungendo come chiavi i terminali della grammatica
        for (NonTerminale nonTerminale : this.grammatica.getNonTerminali()) {
            // Devo calcolare quali elementi compongono la follow
            // Passo 1: se nonTerminale è starting, contiene $ (fatto nel costruttore)
            StrutturaFollow struttura = new StrutturaFollow(grammatica, nonTerminale, FINESTRINGA);

            // Per ogni produzione il cui corpo contiene nonTerminale:
            for (Produzione produzione : grammatica.getProduzioniIfCorpoContains(nonTerminale)) {
                // Se dopo nonTerminale c'è una stringa calcolo la first della stringa e la
                // aggiungo
                // Se dopo nonTerminale c'è una stringa annullabile, oppure nonTerminale è
                // l'ultimo simbolo del corpo, inserisco Follow(testa)
                Corpo corpo = produzione.getCorpo();
                int nonTerminaleIndex = 0;
                for (Simbolo simbolo : corpo) {
                    if (simbolo.equals(nonTerminale)) {
                        LinkedList<Simbolo> betaList = new LinkedList<>(
                                corpo.subList(nonTerminaleIndex + 1,
                                        corpo.size()));
                        // Se il simbolo sta alla fine del corpo
                        if (betaList.isEmpty()) {
                            // Aggiungo la testa solo se è diversa dal nonTerm che sto trattando attualmente
                            if (!produzione.getTesta().equals(nonTerminale)) {
                                struttura.addFollow(produzione.getTesta());
                            }
                        } else {
                            First firstBeta = new First(
                                    calculateStringFirst(betaList));
                            // Se la stringa dopo nonTerminale è annullabile
                            if (firstBeta.contains(this.epsilon)) {
                                // Aggiungo la testa solo se è diversa dal nonTerm che sto trattando attualmente
                                if (!produzione.getTesta().equals(nonTerminale)) {
                                    struttura.addFollow(produzione.getTesta());
                                }
                                firstBeta.remove(this.epsilon);
                            }
                            // Aggiungo beta (senza epsilon se c'era)
                            struttura.addToParziale(firstBeta);
                        }
                    }
                    nonTerminaleIndex++;
                }

            }
            resultParziali.put(nonTerminale, struttura);

        }

        boolean cambiato = true;
        while (cambiato) {
            cambiato = false;

            for (Entry<NonTerminale, StrutturaFollow> entry : resultParziali.entrySet()) {
                Follow parziale = entry.getValue().getParziale();
                int parzialePrima = parziale.size();
                LinkedHashSet<NonTerminale> follows = entry.getValue().getFollows();
                for (NonTerminale nonTermToAdd : follows) {
                    parziale.addAll(resultParziali.get(nonTermToAdd).getParziale());
                }
                cambiato = !(parzialePrima == parziale.size());
            }
        }

        // Trasferisco i risultati ottenuti nel risultato
        for (NonTerminale nonTerminale : grammatica.getNonTerminali()) {
            result.put(nonTerminale, resultParziali.get(nonTerminale).getParziale());
        }
        return result;
    }

}
