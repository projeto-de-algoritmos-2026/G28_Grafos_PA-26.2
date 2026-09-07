package br.edu.transportnet.io;

import br.edu.transportnet.model.Estacao;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

// Valida o dataset antes de montar o grafo. Verifica id duplicado, id
// inexistente em trecho, self loop, aresta duplicada e estacao orfa (grau zero).
public final class ValidadorDataset {

    private ValidadorDataset() {
    }

    public static void validar(List<Estacao> estacoes, List<TrechoCsv> trechos) {
        Set<Integer> ids = new HashSet<>();
        for (Estacao e : estacoes) {
            if (!ids.add(e.id())) {
                throw new DatasetInvalidoException(
                        "id de estacao duplicado: " + e.id() + " (" + e.nome() + ")");
            }
        }

        Set<Long> paresVistos = new HashSet<>();
        Set<Integer> idsReferenciados = new HashSet<>();
        for (TrechoCsv t : trechos) {
            if (!ids.contains(t.origemId())) {
                throw new DatasetInvalidoException(
                        "trecho referencia estacao de origem inexistente: id " + t.origemId());
            }
            if (!ids.contains(t.destinoId())) {
                throw new DatasetInvalidoException(
                        "trecho referencia estacao de destino inexistente: id " + t.destinoId());
            }
            if (t.origemId() == t.destinoId()) {
                throw new DatasetInvalidoException(
                        "self loop nao permitido no trecho da estacao id " + t.origemId());
            }
            int lo = Math.min(t.origemId(), t.destinoId());
            int hi = Math.max(t.origemId(), t.destinoId());
            long chave = (((long) lo) << 32) | (hi & 0xffffffffL);
            if (!paresVistos.add(chave)) {
                throw new DatasetInvalidoException(
                        "aresta duplicada entre as estacoes id " + lo + " e id " + hi);
            }
            idsReferenciados.add(t.origemId());
            idsReferenciados.add(t.destinoId());
        }

        Map<Integer, String> nomePorId = new HashMap<>();
        for (Estacao e : estacoes) {
            nomePorId.put(e.id(), e.nome() + "/" + e.linha());
        }
        for (Estacao e : estacoes) {
            if (!idsReferenciados.contains(e.id())) {
                throw new DatasetInvalidoException(
                        "estacao orfa (grau 0): id " + e.id() + " (" + nomePorId.get(e.id()) + ")");
            }
        }
    }
}
