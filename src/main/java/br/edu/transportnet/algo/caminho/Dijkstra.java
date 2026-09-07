package br.edu.transportnet.algo.caminho;

import br.edu.transportnet.model.Aresta;
import br.edu.transportnet.model.Grafo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

// Caminho de menor tempo com Dijkstra. Usa PriorityQueue com lazy deletion:
// entradas obsoletas ficam na fila e sao descartadas ao serem retiradas.
// O caminho e reconstruido por um vetor de pais. Complexidade O(E log V).
public final class Dijkstra implements BuscaCaminho {

    private record Entrada(int no, double dist) {
    }

    @Override
    public ResultadoCaminho executar(Grafo g, int origem, int destino) {
        long inicio = System.nanoTime();

        int n = g.numVertices();
        double[] dist = new double[n];
        int[] pai = new int[n];
        boolean[] visitado = new boolean[n];
        Arrays.fill(dist, Double.POSITIVE_INFINITY);
        Arrays.fill(pai, -1);

        int nosExpandidos = 0;
        int operacoesFila = 0;

        PriorityQueue<Entrada> fila =
                new PriorityQueue<>((a, b) -> Double.compare(a.dist(), b.dist()));

        dist[origem] = 0.0;
        fila.add(new Entrada(origem, 0.0));
        operacoesFila++;

        while (!fila.isEmpty()) {
            Entrada atual = fila.poll();
            int u = atual.no();
            if (visitado[u]) {
                continue;
            }
            visitado[u] = true;
            nosExpandidos++;
            if (u == destino) {
                break;
            }
            for (Aresta a : g.arestasDe(u)) {
                int v = a.destino();
                if (visitado[v]) {
                    continue;
                }
                double novo = dist[u] + a.pesoSegundos();
                if (novo < dist[v]) {
                    dist[v] = novo;
                    pai[v] = u;
                    fila.add(new Entrada(v, novo));
                    operacoesFila++;
                }
            }
        }

        boolean encontrado = visitado[destino];
        List<Integer> caminho = encontrado ? reconstruir(pai, origem, destino) : List.of();
        double custo = encontrado ? dist[destino] : Double.POSITIVE_INFINITY;
        long tempo = System.nanoTime() - inicio;

        return new ResultadoCaminho(caminho, custo, nosExpandidos, operacoesFila, tempo, encontrado);
    }

    private static List<Integer> reconstruir(int[] pai, int origem, int destino) {
        List<Integer> caminho = new ArrayList<>();
        for (int v = destino; v != -1; v = pai[v]) {
            caminho.add(v);
            if (v == origem) {
                break;
            }
        }
        Collections.reverse(caminho);
        return caminho;
    }
}
