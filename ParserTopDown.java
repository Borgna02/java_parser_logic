package Implementazione;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Stack;

public class ParserTopDown {

    private Grammatica grammatica;
    private ParserUtility parserUtility;
    private Terminale epsilon;

    public ParserTopDown(Grammatica grammatica) {
        this.grammatica = grammatica;
        this.parserUtility = new ParserUtility(grammatica);
        this.epsilon = grammatica.getTermSeEsiste("eps");
    }

    public class TopDownIndice {
        private Terminale terminale;
        private NonTerminale nonTerminale;

        TopDownIndice(Terminale terminale, NonTerminale nonTerminale) {
            this.terminale = terminale;
            this.nonTerminale = nonTerminale;
        }

        public Terminale getTerminale() {
            return terminale;
        }

        public void setTerminale(Terminale terminale) {
            this.terminale = terminale;
        }

        public NonTerminale getNonTerminale() {
            return nonTerminale;
        }

        public void setNonTerminale(NonTerminale nonTerminale) {
            this.nonTerminale = nonTerminale;
        }

        @Override
        public String toString() {
            return "[" + terminale + ", " + nonTerminale + "]";
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getEnclosingInstance().hashCode();
            result = prime * result + ((terminale == null) ? 0 : terminale.hashCode());
            result = prime * result + ((nonTerminale == null) ? 0 : nonTerminale.hashCode());
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
            TopDownIndice other = (TopDownIndice) obj;
            if (!getEnclosingInstance().equals(other.getEnclosingInstance()))
                return false;
            if (terminale == null) {
                if (other.terminale != null)
                    return false;
            } else if (!terminale.equals(other.terminale))
                return false;
            if (nonTerminale == null) {
                if (other.nonTerminale != null)
                    return false;
            } else if (!nonTerminale.equals(other.nonTerminale))
                return false;
            return true;
        }

        private ParserTopDown getEnclosingInstance() {
            return ParserTopDown.this;
        }

    }

    public LinkedHashMap<TopDownIndice, Produzione> getParsingTable() throws Exception {
        LinkedHashMap<TopDownIndice, Produzione> parsingTable = new LinkedHashMap<>();

        for (NonTerminale nonTerminale : grammatica.getNonTerminali()) {
            for (Terminale terminale : grammatica.getTerminali()) {
                TopDownIndice indice = new TopDownIndice(terminale, nonTerminale);
                parsingTable.put(indice, null);
            }
            TopDownIndice indice = new TopDownIndice(this.parserUtility.FINESTRINGA, nonTerminale);
            parsingTable.put(indice, null);
        }

        // Per ogni produzione A -> alpha in G
        for (Produzione produzione : grammatica.getProduzioni()) {
            // Per ogni terminale a in First(alpha) diverso da epsilon inserisco A -> alpha
            // in M[A, a]
            NonTerminale testa = produzione.getTesta();
            LinkedHashSet<Terminale> firstAlpha = this.parserUtility.getFirst(produzione.getCorpo().toStrings());
            for (Terminale terminale : firstAlpha) {
                if (!terminale.equals(this.epsilon)) {
                    if (parsingTable.get(new TopDownIndice(terminale, testa)) != null) {
                        throw new Exception("La grammatica ha conflitti, impossibile eseguire il parsing");
                    }
                    parsingTable.put(new TopDownIndice(terminale, testa), produzione);
                }
            }
            // Se epsilon in First(alpha), per ogni terminale b in Follow(A)
            // diverso da FINESTRINGA inserisco A -> alpha in M[A, b]
            if (firstAlpha.contains(this.epsilon)) {
                LinkedHashSet<Terminale> followTesta = this.parserUtility.getFollow(testa.toString());
                for (Terminale terminale : followTesta) {
                    if (!terminale.equals(this.parserUtility.FINESTRINGA)) {
                        if (parsingTable.get(new TopDownIndice(terminale, testa)) != null) {
                            throw new Exception("La grammatica ha conflitti, impossibile eseguire il parsing");
                        }
                        parsingTable.put(new TopDownIndice(terminale, testa), produzione);
                    }
                }
                // Se epsilon in First(alpha) e FINESTRINGA in Follow(A), inserisco A -> alpha
                // in M[A,FINESTRINGA]
                if (followTesta.contains(this.parserUtility.FINESTRINGA)) {
                    if (parsingTable.get(new TopDownIndice(this.parserUtility.FINESTRINGA, testa)) != null) {
                        throw new Exception("La grammatica ha conflitti, impossibile eseguire il parsing");
                    }
                    parsingTable.put(new TopDownIndice(this.parserUtility.FINESTRINGA, testa), produzione);
                }
            }

        }
        return parsingTable;
    }

    public String getParsingTableToString() throws Exception {
        LinkedHashMap<TopDownIndice, Produzione> parsingTable;
        try {
            parsingTable = getParsingTable();

            LinkedHashSet<Terminale> terminaliGrammatica = this.grammatica.getTerminali();
            LinkedHashSet<Terminale> terminali = new LinkedHashSet<>();

            // Faccio una copia della lista dei terminali, quindi rimuovo epsilon e aggiungo
            // FINESTRINGA
            // Devo fare per forza una copia perché sennò vado a modificare la grammatica
            // originale
            for (Terminale terminale : terminaliGrammatica) {
                if (!terminale.equals(this.epsilon)) {
                    terminali.add(terminale);
                }
            }
            terminali.add(this.parserUtility.FINESTRINGA);

            LinkedHashSet<NonTerminale> nonTerminali = this.grammatica.getNonTerminali();
            StringBuilder result = new StringBuilder();

            int numNonTerminali = nonTerminali.size();
            int numTerminali = terminali.size();

            String[][] table = new String[numNonTerminali + 1][numTerminali + 1];

            // Riempimento delle intestazioni
            table[0][0] = "";
            int col = 1;
            for (Terminale terminale : terminali) {
                table[0][col] = terminale.toString();
                col++;
            }

            int row = 1;
            for (NonTerminale nonTerminale : nonTerminali) {
                table[row][0] = nonTerminale.toString();

                col = 1;
                for (Terminale terminale : terminali) {
                    Produzione produzione = parsingTable.get(new TopDownIndice(terminale, nonTerminale));
                    if (produzione == null) {
                        table[row][col] = "Err.";
                    } else {
                        table[row][col] = produzione.toString();
                    }
                    col++;
                }
                row++;
            }

            // Costruzione della tabella formattata
            for (int r = 0; r <= numNonTerminali; r++) {
                for (int c = 0; c <= numTerminali; c++) {
                    result.append(table[r][c]);
                    // formattazione della tabella: aggiungo un numero di spazi dipendente dalla

                    for (int k = table[r][c].length(); k < 20; k++) {
                        if (k == 15)
                            result.append("|");
                        result.append(" ");
                    }
                }
                result.append("\n");
            }

            result.append("\n");

            return result.toString();
        } catch (Exception e) {
            throw e;
        }
    }

    public List<Produzione> parsing(String... strings) throws Exception {
        ArrayList<Terminale> input = new ArrayList<Terminale>();
        ArrayList<Produzione> result = new ArrayList<Produzione>();
        final Terminale FINESTRINGA = this.parserUtility.FINESTRINGA;

        // Controllo che l'input sia insieme di terminali
        for (String str : strings) {
            Terminale term = this.grammatica.getTermSeEsiste(str);
            if (term != null) {
                input.add(term);
            } else {
                throw new IllegalArgumentException(
                        "L'input deve essere un insieme di terminali esistenti, " + str + " non valido");
            }
        }

        // Aggiungo il FINESTRINGA per definire la fine dell'input
        input.add(FINESTRINGA);

        // Definisco lo stack e inserisco il simbolo di partenza e FINESTRINGA
        Stack<Simbolo> stack = new Stack<Simbolo>();
        stack.push(FINESTRINGA);
        stack.push(this.grammatica.getPartenza());

        // Definisco matched per vedere la parte di stringa finora matchata
        ArrayList<Terminale> matched = new ArrayList<Terminale>();

        // Recupero la parsing table
        LinkedHashMap<TopDownIndice, Produzione> parsingTable = this.getParsingTable();

        // Finché il top dello stack è diverso da FINESTRINGA
        while (!stack.peek().equals(FINESTRINGA)) {
            System.out.println("Matched: " + matched + ", Stack: " + stack + ",Input: " + input + "\n");
            // 1: se peek è uguale al primo elemento della stringa
            if (stack.peek().equals(input.get(0))) {
                // input contiene solo terminali, quindi se sono uguali vuol dire che l'elemento
                // che rimuovo dallo stack è un terminale
                matched.add((Terminale) stack.pop());
                List<Terminale> newInput = input.subList(1, input.size() - 1);
                input = new ArrayList<>();
                for (Terminale terminale : newInput) {
                    input.add(terminale);
                }
                input.add(FINESTRINGA);
            }
            // 2: se peek è un terminale ma non ha fatto matching nell'if precedente, allora
            // errore
            else if (stack.peek() instanceof Terminale) {
                throw new Exception("Errore: il parsing non può essere eseguito");
            }
            // 3: se peek è un terminale, se non c'è una produzione in M[peek, input[0]],
            // allora errore
            else if (parsingTable.get(new TopDownIndice(input.get(0), (NonTerminale) stack.peek())) == null) {
                throw new Exception("Errore: il parsing non può essere eseguito");
            }
            // 4: se c'è una produzione la aggiungo al risultato ed aggiungo il corpo allo
            // stack
            else {
                Produzione produzione = parsingTable.get(new TopDownIndice(input.get(0), (NonTerminale) stack.peek()));
                result.add(produzione);
                stack.pop();
                ArrayList<Simbolo> simboliCorpo = produzione.getCorpo().getSimboli();

                for (int i = simboliCorpo.size() - 1; i >= 0; i--) {
                    if (!simboliCorpo.get(i).equals(this.epsilon))
                        stack.push(simboliCorpo.get(i));
                }
            }

        }

        return result;

    }
}
