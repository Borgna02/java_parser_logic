package Implementazione.Parser.ParserBottomUp.ParserSLR;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import Implementazione.Domain.Simbolo;
import Implementazione.Parser.ParserUtility;
import Implementazione.Parser.ParserBottomUp.Action;
import Implementazione.Parser.ParserBottomUp.ParserSLR.ParserSLR.SLRindice;

public class ParsingTableSLR extends LinkedHashMap<SLRindice, LinkedHashMap<Simbolo, LinkedHashSet<Action>>>{

    public ParsingTableSLR(LinkedHashSet<Simbolo> alfabeto, Set<SLRindice> indici) {
        for(SLRindice indice : indici) {
            LinkedHashMap<Simbolo, LinkedHashSet<Action>> entry = new LinkedHashMap<>();
            for(Simbolo simbolo : alfabeto) {
                entry.put(simbolo, new LinkedHashSet<>());
            }
            entry.put(ParserUtility.FINESTRINGA, new LinkedHashSet<>());
            this.put(indice, entry);
        }
    }

}
