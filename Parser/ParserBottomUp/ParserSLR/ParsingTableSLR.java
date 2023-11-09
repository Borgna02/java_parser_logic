package Implementazione.Parser.ParserBottomUp.ParserSLR;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import Implementazione.Domain.Simbolo;
import Implementazione.Parser.ParserUtility;
import Implementazione.Parser.ParserBottomUp.Action;

public class ParsingTableSLR extends LinkedHashMap<IndiceSLR, LinkedHashMap<Simbolo, LinkedHashSet<Action>>>{

    public ParsingTableSLR(LinkedHashSet<Simbolo> alfabeto, Set<IndiceSLR> indici) {
        for(IndiceSLR indice : indici) {
            LinkedHashMap<Simbolo, LinkedHashSet<Action>> entry = new LinkedHashMap<>();
            for(Simbolo simbolo : alfabeto) {
                entry.put(simbolo, new LinkedHashSet<>());
            }
            entry.put(ParserUtility.FINESTRINGA, new LinkedHashSet<>());
            this.put(indice, entry);
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        Set<Simbolo> alfabeto = this.get(this.keySet().iterator().next()).keySet();
        Set<IndiceSLR> indici = this.keySet();

        // Devo costruire una tabella che ha per colonne i simboli e per righe gli
        // indici degli itemSet
        int numColonne = alfabeto.size() + 1;
        // Raddoppio indici.size() per inserire delle righe di separazione 
        int numRighe = indici.size() * 2 + 1;

        String[][] table = new String[numRighe][numColonne];
        table[0][0] = "itemSet";
         
        int col = 1;
        for (Simbolo simbolo : alfabeto) {
            table[0][col] = simbolo.toString();
            col++;
        }
        int row = 1;
        for(IndiceSLR indice : indici) {
            for (int i = 0; i < numColonne; i++) {
                table[row][i] = "----------------";
            }
            row++;
            table[row][0] = Integer.toString(indice.getItemSetIndex());
            int i = 1;
            for(Simbolo simbolo : alfabeto) {
                table[row][i] = this.get(indice).get(simbolo).toString();
                i++;
            }
            row++;

        }

        
        // Costruzione della tabella formattata
        result.append("\n");
        for (int r = 0; r < numRighe; r++) {
            for (int c = 0; c < numColonne; c++) {
                // result.append(table[r][c].replace("[", "").replace("]",""));
                String entry = table[r][c].replace("[", "").replace("]","").replace(", ","/");
                result.append(entry);
                // formattazione della tabella: aggiungo un numero di spazi dipendente dalla
                // lunghezza della stringa che ho inserito
                for (int k = entry.length(); k < 15; k++) {
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
