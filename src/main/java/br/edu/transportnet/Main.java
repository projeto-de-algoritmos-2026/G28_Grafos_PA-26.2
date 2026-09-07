package br.edu.transportnet;

import br.edu.transportnet.algo.caminho.Dijkstra;
import br.edu.transportnet.algo.caminho.ResultadoCaminho;
import br.edu.transportnet.io.CsvLoader;
import br.edu.transportnet.model.Estacao;
import br.edu.transportnet.model.Grafo;
import br.edu.transportnet.model.Modal;

import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

// Ponto de entrada. Carrega o dataset, imprime as estatisticas da rede e
// demonstra uma rota de menor tempo com o Dijkstra.
public final class Main {

    private static final Path ESTACOES = Path.of("data", "estacoes.csv");
    private static final Path TRECHOS = Path.of("data", "trechos.csv");

    private Main() {
    }

    public static void main(String[] args) {
        Path estacoes = args.length > 0 ? Path.of(args[0]) : ESTACOES;
        Path trechos = args.length > 1 ? Path.of(args[1]) : TRECHOS;

        System.out.println("TransportNet OK");
        Grafo g = CsvLoader.carregar(estacoes, trechos);
        imprimirEstatisticas(g);
        demonstrarRota(g, "Jabaquara", "L1-AZUL", "Corinthians-Itaquera", "L3-VERMELHA");
    }

    static void imprimirEstatisticas(Grafo g) {
        Map<Modal, Integer> porModal = new EnumMap<>(Modal.class);
        for (int i = 0; i < g.numVertices(); i++) {
            porModal.merge(g.vertice(i).modal(), 1, Integer::sum);
        }

        System.out.println();
        System.out.println("=== Rede de transporte ===");
        System.out.printf(Locale.ROOT, "  |V| (estacoes):       %d%n", g.numVertices());
        System.out.printf(Locale.ROOT, "  |E| (arestas dirig.): %d%n", g.numArestas());
        System.out.printf(Locale.ROOT, "  Grau medio (saida):   %.2f%n", g.grauMedio());
        System.out.println("  Distribuicao por modal:");
        for (Modal m : Modal.values()) {
            System.out.printf(Locale.ROOT, "    %-7s %d%n", m, porModal.getOrDefault(m, 0));
        }
    }

    static void demonstrarRota(Grafo g, String nomeOrigem, String linhaOrigem,
                               String nomeDestino, String linhaDestino) {
        int origem = indiceDe(g, nomeOrigem, linhaOrigem);
        int destino = indiceDe(g, nomeDestino, linhaDestino);
        ResultadoCaminho r = new Dijkstra().executar(g, origem, destino);

        System.out.println();
        System.out.println("=== Rota mais rapida (Dijkstra) ===");
        if (!r.encontrado()) {
            System.out.println("  nenhum caminho encontrado");
            return;
        }
        System.out.printf(Locale.ROOT, "  Tempo total: %s  |  estacoes: %d  |  nos expandidos: %d%n",
                formatarMmSs(r.custoTotalSegundos()), r.caminho().size(), r.nosExpandidos());
        StringBuilder sb = new StringBuilder("  ");
        for (int i = 0; i < r.caminho().size(); i++) {
            Estacao e = g.vertice(r.caminho().get(i));
            sb.append(e.nome()).append(" (").append(e.linha()).append(")");
            if (i < r.caminho().size() - 1) {
                sb.append(" -> ");
            }
        }
        System.out.println(sb);
    }

    private static int indiceDe(Grafo g, String nome, String linha) {
        for (int i = 0; i < g.numVertices(); i++) {
            if (g.vertice(i).nome().equals(nome) && g.vertice(i).linha().equals(linha)) {
                return i;
            }
        }
        throw new IllegalArgumentException("estacao nao encontrada: " + nome + " / " + linha);
    }

    private static String formatarMmSs(double segundos) {
        int total = (int) Math.round(segundos);
        return String.format(Locale.ROOT, "%02d:%02d", total / 60, total % 60);
    }
}
