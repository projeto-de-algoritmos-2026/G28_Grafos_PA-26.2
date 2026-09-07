package br.edu.transportnet.io;

import br.edu.transportnet.model.Sentido;
import br.edu.transportnet.model.TipoAresta;

// Linha bruta de trechos.csv, referenciando as estacoes pelo id do CSV.
public record TrechoCsv(int origemId, int destinoId, String linha,
                        TipoAresta tipo, Sentido sentido) {
}
