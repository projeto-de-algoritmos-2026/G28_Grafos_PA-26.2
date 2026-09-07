package br.edu.transportnet.io;

// Erro de integridade do dataset. A mensagem descreve o problema encontrado.
public class DatasetInvalidoException extends RuntimeException {

    public DatasetInvalidoException(String message) {
        super(message);
    }
}
