// Maria Eduarda Brito
// GitHub: ITX-Duda
// Redes de Computadores - UFABC

import java.io.*;
import java.net.*;
import java.util.*;

public class Receiver {

    private static DatagramSocket socket;
    private static int portaLocal = 9876; 
    private static HashMap<Integer, SegmentoConfiavel> bufferJanela = new HashMap<>();
    private static int proximoIdEsperado = 1;
    private static int maiorIdRecebido = 0;

    // --------------------------------------------------------------------------------------------------
    // GERENCIAMENTO DE PACOTES

    private static void gerenciarPacoteChegando(SegmentoConfiavel segmento, DatagramPacket pacoteOrigem) {
        int idRecebido = segmento.getId();
        
        if (idRecebido > maiorIdRecebido) {
            maiorIdRecebido = idRecebido;
        }

        enviarAck(idRecebido, pacoteOrigem.getAddress(), pacoteOrigem.getPort());

        if (idRecebido < proximoIdEsperado || bufferJanela.containsKey(idRecebido)) {
            System.out.printf("Mensagem id %d recebida de forma duplicada.\n", idRecebido);
        } 
        else if (idRecebido > proximoIdEsperado) {
            bufferJanela.put(idRecebido, segmento);
            List<Integer> faltantes = verificarFaltantes();
            System.out.printf("Mensagem id %d recebida fora de ordem, ainda não recebidos os indentificadores: %s\n", idRecebido, faltantes.toString());
        }
        else if (idRecebido == proximoIdEsperado) {
            System.out.printf("Mensagem id %d recebida na ordem, entregando para a camada de aplicação.\n", idRecebido);
            proximoIdEsperado++;
            esvaziarBufferPronto();
        }
    }

    private static void esvaziarBufferPronto() {
        while (bufferJanela.containsKey(proximoIdEsperado)) {
            SegmentoConfiavel segBuffer = bufferJanela.remove(proximoIdEsperado);
            System.out.printf("Mensagem id %d retirada do buffer e entregue na ordem correta.\n", segBuffer.getId());
            proximoIdEsperado++;
        }
    }

    private static List<Integer> verificarFaltantes() {
        List<Integer> idsFaltantes = new ArrayList<>();
        for (int i = proximoIdEsperado; i <= maiorIdRecebido; i++) {
            if (i != proximoIdEsperado && !bufferJanela.containsKey(i)) {
                idsFaltantes.add(i);
            }
        }
        return idsFaltantes;
    }

    private static void enviarAck(int id, InetAddress ipSender, int portaSender) {
        try {
            SegmentoConfiavel ack = new SegmentoConfiavel(id);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(ack);
            byte[] dadosAck = baos.toByteArray();

            DatagramPacket pacoteAck = new DatagramPacket(dadosAck, dadosAck.length, ipSender, portaSender);
            socket.send(pacoteAck);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --------------------------------------------------------------------------------------------------
    // THREAD DE ESCUTA

    private static class EscutaRede implements Runnable {
        public void run() {
            byte[] bufferEntrada = new byte[4096];
            while (true) {
                try {
                    DatagramPacket pacoteRecebido = new DatagramPacket(bufferEntrada, bufferEntrada.length);
                    socket.receive(pacoteRecebido);

                    ByteArrayInputStream bais = new ByteArrayInputStream(pacoteRecebido.getData());
                    ObjectInputStream ois = new ObjectInputStream(bais);
                    SegmentoConfiavel segmento = (SegmentoConfiavel) ois.readObject();

                    gerenciarPacoteChegando(segmento, pacoteRecebido);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // --------------------------------------------------------------------------------------------------
    // MAIN

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.println("=".repeat(45));
            System.out.println("  📡 SERVIDOR RECEIVER PRONTO PARA INICIAR");
            System.out.println("=".repeat(45));
            
            System.out.print("Pressione [Enter] para abrir a porta " + portaLocal + " e começar a escutar...");
            scanner.nextLine(); 

            socket = new DatagramSocket(portaLocal);
            System.out.println("\n[Aguardando pacotes do Sender...]\n");

            new Thread(new EscutaRede()).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}