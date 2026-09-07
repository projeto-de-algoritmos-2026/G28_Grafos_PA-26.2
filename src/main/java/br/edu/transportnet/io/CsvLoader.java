package br.edu.transportnet.io;

import br.edu.transportnet.geo.EstimadorTempo;
import br.edu.transportnet.model.Aresta;
import br.edu.transportnet.model.Estacao;
import br.edu.transportnet.model.Grafo;
import br.edu.transportnet.model.Modal;
import br.edu.transportnet.model.Sentido;
import br.edu.transportnet.model.TipoAresta;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Le os dois CSVs e devolve o grafo direcionado. Aqui vive o unico mapa do id
// do CSV para o indice interno; o nucleo de algoritmos trabalha so com int.
// Uma linha BI gera duas arestas opostas; uma linha UNI gera uma.
public final class CsvLoader {

    private static final String HEADER_ESTACOES = "id,nome,linha,modal,lat,lon";
    private static final String HEADER_TRECHOS = "origem,destino,linha,tipo,sentido";

    private CsvLoader() {
    }

    public static Grafo carregar(Path estacoesCsv, Path trechosCsv) {
        List<Estacao> estacoes = lerEstacoes(estacoesCsv);
        List<TrechoCsv> trechos = lerTrechos(trechosCsv);

        ValidadorDataset.validar(estacoes, trechos);

        Map<Integer, Integer> idParaIndice = new HashMap<>();
        for (int i = 0; i < estacoes.size(); i++) {
            idParaIndice.put(estacoes.get(i).id(), i);
        }

        Grafo g = new Grafo(estacoes, true);
        for (TrechoCsv t : trechos) {
            int io = idParaIndice.get(t.origemId());
            int id = idParaIndice.get(t.destinoId());
            Estacao origem = estacoes.get(io);
            Estacao destino = estacoes.get(id);

            g.adicionarAresta(new Aresta(io, id, peso(t.tipo(), origem, destino), t.tipo(), t.linha()));
            if (t.sentido() == Sentido.BI) {
                g.adicionarAresta(new Aresta(id, io, peso(t.tipo(), destino, origem), t.tipo(), t.linha()));
            }
        }
        return g;
    }

    private static double peso(TipoAresta tipo, Estacao de, Estacao para) {
        return switch (tipo) {
            case VIA -> EstimadorTempo.pesoViaSegundos(de, para);
            case BALDEACAO -> EstimadorTempo.pesoBaldeacaoSegundos();
        };
    }

    private static List<Estacao> lerEstacoes(Path caminho) {
        List<String> linhas = lerLinhas(caminho);
        exigirCabecalho(linhas, HEADER_ESTACOES, caminho);

        List<Estacao> estacoes = new ArrayList<>();
        for (int i = 1; i < linhas.size(); i++) {
            String linha = linhas.get(i).strip();
            if (linha.isEmpty()) {
                continue;
            }
            String[] c = linha.split(",", -1);
            if (c.length != 6) {
                throw new DatasetInvalidoException(erro(caminho, i,
                        "esperava 6 colunas, veio " + c.length + ": " + linha));
            }
            try {
                int id = Integer.parseInt(c[0].strip());
                String nome = c[1].strip();
                String nomeLinha = c[2].strip();
                Modal modal = Modal.valueOf(c[3].strip());
                double lat = Double.parseDouble(c[4].strip());
                double lon = Double.parseDouble(c[5].strip());
                estacoes.add(new Estacao(id, nome, nomeLinha, modal, lat, lon));
            } catch (IllegalArgumentException e) {
                throw new DatasetInvalidoException(erro(caminho, i, "valor invalido: " + e.getMessage()));
            }
        }
        if (estacoes.isEmpty()) {
            throw new DatasetInvalidoException("nenhuma estacao lida de " + caminho);
        }
        return estacoes;
    }

    private static List<TrechoCsv> lerTrechos(Path caminho) {
        List<String> linhas = lerLinhas(caminho);
        exigirCabecalho(linhas, HEADER_TRECHOS, caminho);

        List<TrechoCsv> trechos = new ArrayList<>();
        for (int i = 1; i < linhas.size(); i++) {
            String linha = linhas.get(i).strip();
            if (linha.isEmpty()) {
                continue;
            }
            String[] c = linha.split(",", -1);
            if (c.length != 5) {
                throw new DatasetInvalidoException(erro(caminho, i,
                        "esperava 5 colunas, veio " + c.length + ": " + linha));
            }
            try {
                int origem = Integer.parseInt(c[0].strip());
                int destino = Integer.parseInt(c[1].strip());
                String nomeLinha = c[2].strip();
                TipoAresta tipo = TipoAresta.valueOf(c[3].strip());
                Sentido sentido = Sentido.valueOf(c[4].strip());
                trechos.add(new TrechoCsv(origem, destino, nomeLinha, tipo, sentido));
            } catch (IllegalArgumentException e) {
                throw new DatasetInvalidoException(erro(caminho, i, "valor invalido: " + e.getMessage()));
            }
        }
        if (trechos.isEmpty()) {
            throw new DatasetInvalidoException("nenhum trecho lido de " + caminho);
        }
        return trechos;
    }

    private static void exigirCabecalho(List<String> linhas, String esperado, Path caminho) {
        if (linhas.isEmpty() || !linhas.get(0).strip().equalsIgnoreCase(esperado)) {
            throw new DatasetInvalidoException("cabecalho invalido em " + caminho
                    + " (esperado: " + esperado + ")");
        }
    }

    private static List<String> lerLinhas(Path caminho) {
        try {
            return Files.readAllLines(caminho, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("falha ao ler " + caminho, e);
        }
    }

    private static String erro(Path caminho, int indiceLinha, String detalhe) {
        return caminho + ":" + (indiceLinha + 1) + " " + detalhe;
    }
}
