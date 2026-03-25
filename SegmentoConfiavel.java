// Maria Eduarda Brito
// GitHub: ITX-Duda
// Redes de Computadores - UFABC

import java.io.Serializable;

public class SegmentoConfiavel implements Serializable {
    
    private static final long serialVersionUID = 1L; 
    
    private int id; 
    private boolean isAck;
    private String payload;
    private String tipoEnvio;

    // Construtor para enviar dados
    public SegmentoConfiavel(int id, String payload, String tipoEnvio) {
        this.id = id;
        this.payload = payload;
        this.tipoEnvio = tipoEnvio;
        this.isAck = false;
    }

    // Construtor para enviar ACK
    public SegmentoConfiavel(int id) {
        this.id = id;
        this.isAck = true;
        this.payload = null;
        this.tipoEnvio = "normal"; 
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public boolean isAck() { return isAck; }
    public void setAck(boolean isAck) { this.isAck = isAck; }

    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }

    public String getTipoEnvio() { return tipoEnvio; }
    public void setTipoEnvio(String tipoEnvio) { this.tipoEnvio = tipoEnvio; }

    @Override
    public String toString() {
        if (isAck) {
            return "[ACK id=" + id + "]";
        } else {
            return "[DADO id=" + id + " | payload=" + payload + " | tipo=" + tipoEnvio + "]";
        }
    }
}