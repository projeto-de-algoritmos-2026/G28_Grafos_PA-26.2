package br.edu.transportnet.geo;

import br.edu.transportnet.model.Estacao;
import br.edu.transportnet.model.Modal;

// Converte a geometria da rede em tempo de viagem em segundos.
// peso de uma via e distancia dividida pela velocidade do modal mais o tempo
// de parada. A baldeacao tem peso fixo e nunca zero.
public final class EstimadorTempo {

    public static final double VEL_METRO_TREM_KMH = 32.0;
    public static final double VEL_BRT_KMH = 22.0;
    public static final double VEL_ONIBUS_KMH = 16.0;

    public static final double TEMPO_PARADA_S = 20.0;
    public static final double TEMPO_BALDEACAO_S = 180.0;

    // Velocidade maxima da rede, usada so pela heuristica do A*.
    public static final double VEL_MAX_REDE_KMH = 32.0;

    private static final double SEGUNDOS_POR_HORA = 3600.0;

    private EstimadorTempo() {
    }

    public static double velocidadeComercialKmh(Modal modal) {
        return switch (modal) {
            case METRO, TREM -> VEL_METRO_TREM_KMH;
            case BRT -> VEL_BRT_KMH;
            case ONIBUS -> VEL_ONIBUS_KMH;
        };
    }

    public static double pesoViaSegundos(Estacao origem, Estacao destino) {
        double distKm = Haversine.distanciaKm(
                origem.lat(), origem.lon(), destino.lat(), destino.lon());
        double horas = distKm / velocidadeComercialKmh(origem.modal());
        return horas * SEGUNDOS_POR_HORA + TEMPO_PARADA_S;
    }

    public static double pesoBaldeacaoSegundos() {
        return TEMPO_BALDEACAO_S;
    }
}
