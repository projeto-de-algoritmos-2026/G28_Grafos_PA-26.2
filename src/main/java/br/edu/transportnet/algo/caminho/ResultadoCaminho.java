package br.edu.transportnet.algo.caminho;

import java.util.List;

// Resultado de uma busca de caminho. Guarda tambem as metricas usadas no
// experimento: nos expandidos, operacoes de fila e tempo em nanossegundos.
public record ResultadoCaminho(List<Integer> caminho, double custoTotalSegundos,
                               int nosExpandidos, int operacoesFila,
                               long tempoNanos, boolean encontrado) {
}
