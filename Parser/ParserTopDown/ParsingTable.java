package Implementazione.Parser.ParserTopDown;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

import Implementazione.Domain.NonTerminale;
import Implementazione.Domain.Produzione;
import Implementazione.Domain.Terminale;
import Implementazione.Parser.ParserUtility;

public class ParsingTable extends LinkedHashMap<NonTerminale, LinkedHashMap<Terminale, LinkedList<Produzione>>> {

    @Override
    public String toString() {

        Set<NonTerminale> nonTerminali = this.keySet();
        Set<Terminale> terminaliGrammatica = this.get(nonTerminali.iterator().next()).keySet();
        LinkedHashSet<Terminale> terminali = new LinkedHashSet<>();

        // Faccio una copia della lista dei terminali, quindi rimuovo epsilon e aggiungo
        // FINESTRINGA
        // Devo fare per forza una copia perché sennò vado a modificare la grammatica
        // originale
        for (Terminale terminale : terminaliGrammatica) {
            if (!terminale.toString().equals("eps")) {
                terminali.add(terminale);
            }
        }
        terminali.add(ParserUtility.FINESTRINGA);
        StringBuilder result = new StringBuilder();

        int numRighe = nonTerminali.size() * 2 + 1;
        int numColonne = terminali.size() + 1;

        String[][] table = new String[numRighe][numColonne];

        // Riempimento delle intestazioni
        table[0][0] = "";
        int col = 1;
        for (Terminale terminale : terminali) {
            table[0][col] = terminale.toString();
            col++;
        }

        int row = 1;
        for (NonTerminale nonTerminale : nonTerminali) {
            for (int i = 0; i < numColonne; i++) {
                table[row][i] = "-------------------------------------";
            }
            row++;
            table[row][0] = nonTerminale.toString();

            col = 1;
            for (Terminale terminale : terminali) {
                LinkedList<Produzione> produzioni = this.get(nonTerminale).get(terminale);
                if (produzioni.size() == 0) {
                    table[row][col] = "Err.";
                } else {
                    String entry = "";
                    for (Produzione produzione : produzioni) {
                        entry += "-> " + produzione.getCorpo() + ", ";
                    }
                    table[row][col] = entry.substring(0, entry.length() - 2);;
                }
                col++;
            }
            row++;
        }

        // Costruzione della tabella formattata
        for (int r = 0; r < numRighe; r++) {
            for (int c = 0; c < numColonne; c++) {
                String entry = table[r][c].replace("[", "").replace("]", "");
                result.append(entry);
                // formattazione della tabella: aggiungo un numero di spazi dipendente dalla

                for (int k = entry.length(); k < 35; k++) {
                    if (k == 33)
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
