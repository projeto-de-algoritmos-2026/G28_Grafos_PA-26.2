package br.edu.transportnet.model;

// Estacao da rede. Ha um no por par (estacao, linha); estacoes de integracao
// viram nos distintos ligados por baldeacao. O id e o identificador do CSV.
public record Estacao(int id, String nome, String linha, Modal modal,
                      double lat, double lon) {
}
