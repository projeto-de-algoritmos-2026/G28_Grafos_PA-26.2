package br.edu.transportnet.algo.caminho;

import br.edu.transportnet.model.Grafo;

// Contrato comum de busca de caminho. Dijkstra e A* implementam esta interface.
public interface BuscaCaminho {

    ResultadoCaminho executar(Grafo g, int origem, int destino);
}
