package br.edu.transportnet.geo;

// Distancia em km entre dois pontos geograficos pela formula de Haversine.
public final class Haversine {

    public static final double RAIO_TERRA_KM = 6371.0;

    private Haversine() {
    }

    public static double distanciaKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double senoLat = Math.sin(dLat / 2.0);
        double senoLon = Math.sin(dLon / 2.0);
        double a = senoLat * senoLat
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * senoLon * senoLon;
        double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0 - a));
        return RAIO_TERRA_KM * c;
    }
}
