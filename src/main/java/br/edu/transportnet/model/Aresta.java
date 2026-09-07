package br.edu.transportnet.model;

// Aresta dirigida com peso em segundos. origem e destino sao indices internos.
public record Aresta(int origem, int destino, double pesoSegundos,
                     TipoAresta tipo, String linha) {
}
