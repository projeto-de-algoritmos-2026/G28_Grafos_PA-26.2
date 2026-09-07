package br.edu.transportnet.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

// Grafo como lista de adjacencia indexada por inteiro. O indice de um vertice
// e a sua posicao na lista de vertices. adj.get(u) sao as arestas que saem de u.
public final class Grafo {

    private final List<Estacao> vertices;
    private final List<List<Aresta>> adj;
    private final boolean direcionado;

    public Grafo(List<Estacao> vertices, boolean direcionado) {
        this.vertices = List.copyOf(vertices);
        this.direcionado = direcionado;
        this.adj = new ArrayList<>(this.vertices.size());
        for (int i = 0; i < this.vertices.size(); i++) {
            adj.add(new ArrayList<>());
        }
    }

    public void adicionarAresta(Aresta aresta) {
        adj.get(aresta.origem()).add(aresta);
    }

    public int numVertices() {
        return vertices.size();
    }

    public int numArestas() {
        int total = 0;
        for (List<Aresta> lista : adj) {
            total += lista.size();
        }
        return total;
    }

    public List<Aresta> arestasDe(int u) {
        return Collections.unmodifiableList(adj.get(u));
    }

    public List<Aresta> todasArestas() {
        List<Aresta> todas = new ArrayList<>(numArestas());
        for (List<Aresta> lista : adj) {
            todas.addAll(lista);
        }
        return todas;
    }

    public Estacao vertice(int u) {
        return vertices.get(u);
    }

    public List<Estacao> vertices() {
        return vertices;
    }

    public boolean direcionado() {
        return direcionado;
    }

    // Grafo transposto: cada aresta u para v vira v para u. Usado por Kosaraju.
    public Grafo transposto() {
        Grafo t = new Grafo(vertices, true);
        for (List<Aresta> lista : adj) {
            for (Aresta a : lista) {
                t.adicionarAresta(new Aresta(a.destino(), a.origem(),
                        a.pesoSegundos(), a.tipo(), a.linha()));
            }
        }
        return t;
    }

    // Projecao nao direcionada usada por Prim e Kruskal. Cada par de estacoes
    // vira uma aresta guardada nos dois sentidos; arestas UNI entram com o mesmo
    // peso. Em caso de par repetido mantem a de menor peso.
    public Grafo comoNaoDirecionado() {
        Map<Long, Aresta> melhorPorPar = new HashMap<>();
        for (List<Aresta> lista : adj) {
            for (Aresta a : lista) {
                if (a.origem() == a.destino()) {
                    continue;
                }
                int lo = Math.min(a.origem(), a.destino());
                int hi = Math.max(a.origem(), a.destino());
                long chave = (((long) lo) << 32) | (hi & 0xffffffffL);
                Aresta atual = melhorPorPar.get(chave);
                if (atual == null || a.pesoSegundos() < atual.pesoSegundos()) {
                    melhorPorPar.put(chave, a);
                }
            }
        }

        Grafo u = new Grafo(vertices, false);
        for (Aresta a : melhorPorPar.values()) {
            int lo = Math.min(a.origem(), a.destino());
            int hi = Math.max(a.origem(), a.destino());
            u.adicionarAresta(new Aresta(lo, hi, a.pesoSegundos(), a.tipo(), a.linha()));
            u.adicionarAresta(new Aresta(hi, lo, a.pesoSegundos(), a.tipo(), a.linha()));
        }
        return u;
    }

    // Grau medio de saida: numArestas dividido por numVertices.
    public double grauMedio() {
        if (vertices.isEmpty()) {
            return 0.0;
        }
        return (double) numArestas() / vertices.size();
    }

    // Indices de vertices sem nenhuma aresta incidente (grau total zero).
    public Set<Integer> verticesIsolados() {
        boolean[] temIncidencia = new boolean[vertices.size()];
        for (int u = 0; u < adj.size(); u++) {
            for (Aresta a : adj.get(u)) {
                temIncidencia[a.origem()] = true;
                temIncidencia[a.destino()] = true;
            }
        }
        Set<Integer> isolados = new HashSet<>();
        for (int i = 0; i < temIncidencia.length; i++) {
            if (!temIncidencia[i]) {
                isolados.add(i);
            }
        }
        return isolados;
    }
}
