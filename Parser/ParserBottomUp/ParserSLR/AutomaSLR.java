package Implementazione.Parser.ParserBottomUp.ParserSLR;

import java.util.LinkedHashMap;
import java.util.Set;

import Implementazione.Domain.Simbolo;

public class AutomaSLR extends LinkedHashMap<IndiceSLR, LinkedHashMap<Simbolo, IndiceSLR>> {
    
    @Override
    public String toString() {
        Set<Simbolo> alfabeto = this.get(this.keySet().iterator().next()).keySet();
        StringBuilder result = new StringBuilder();
        Set<IndiceSLR> indici = this.keySet();

        // Conto il numero di items presenti nella tabella. Per ogni item dovrò avere
        // una riga
        int numItems = 0;
        for (IndiceSLR indice : this.keySet()) {
            numItems += indice.getItemSet().size();
        }

        // Devo costruire una tabella che ha per colonne i simboli e per righe gli
        // item dell'automa
        int numColonne = alfabeto.size() + 2;
        // Aggiungo keySet().size() per inserire delle righe di separazione tra gli
        // itemset
        int numRighe = numItems + 1 + this.keySet().size();

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
        for (IndiceSLR indice : indici) {
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
                for (Simbolo simbolo : alfabeto) {
                    if (itemIndex == 0) {
                        if (this.get(indice).get(simbolo) == null) {
                            table[row][col] = "-";

                        } else {
                            table[row][col] = Integer.toString(this.get(indice).get(simbolo).getItemSetIndex());
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
}
