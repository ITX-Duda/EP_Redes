// Maria Eduarda Brito
// GitHub: ITX-Duda
// Redes de Computadores - UFABC

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Sender {

    private static DatagramSocket socket;
    private static SegmentoConfiavel pacoteRetido = null;
    private static InetAddress ipDestino;
    private static int portaReceiver = 9876;
    private static int portaSender = 9875; 

    private static ConcurrentHashMap<Integer, SegmentoConfiavel> janela = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Integer, Timer> timers = new ConcurrentHashMap<>();

    private static final int TIMEOUT_MS = 3000; 
    private static int proximoId = 1;

    // --------------------------------------------------------------------------------------------------
    // FUNÇÕES INCIAIS

    public static void exibirMenu() {
        System.out.println("\nBem vindo(a)!\nMenu de Opções do Sender:");
        System.out.println("1 - Envio Normal");
        System.out.println("2 - Envio Duplicado");
        System.out.println("3 - Simular Perda");
        System.out.println("4 - Simular Entrega Lenta");
        System.out.println("5 - Simular Chegada Fora de Ordem");
        System.out.println("0 - Sair");
    }

    public static String menuEscolha(Scanner scanner) {
        while (true) {
            System.out.print("Digite o número da opção desejada: ");
            String escolha = scanner.nextLine().trim();
            if (Arrays.asList("0", "1", "2", "3", "4", "5").contains(escolha)) {
                return escolha;
            } else {
                System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    private static void rodarModoEnvio(Scanner scanner, String tipoEnvio) {
        String contador = "s";
        while (contador.equalsIgnoreCase("s")) {
            System.out.print("Digite a mensagem a ser enviada: ");
            String mensagem = scanner.nextLine();
            
            int idAtual = proximoId++;
            SegmentoConfiavel segmento = new SegmentoConfiavel(idAtual, mensagem, tipoEnvio);
            
            System.out.printf("Mensagem \"%s\" enviada como %s com ID %d.\n", mensagem, tipoEnvio, idAtual);
            simularEnvio(segmento);

            System.out.print("Deseja enviar outra mensagem nesse modo? (s/n): ");
            contador = scanner.nextLine().trim().toLowerCase();
        }
    }

    // --------------------------------------------------------------------------------------------------
    // LÓGICA E TIMERS

    private static void simularEnvio(SegmentoConfiavel segmento) {
        String tipo = segmento.getTipoEnvio();
        
        if (tipo.equals("normal")) {
            enviarPacote(segmento);
            if (pacoteRetido != null) {
                enviarPacote(pacoteRetido);
                pacoteRetido = null;
            }
        } 
        else if (tipo.equals("duplicado")) {
            enviarPacote(segmento);
            enviarPacote(segmento); 
        }
        else if (tipo.equals("perda")) {
            janela.put(segmento.getId(), segmento);
            iniciarTemporizador(segmento.getId());
        }
        else if (tipo.equals("lenta")) {
            janela.put(segmento.getId(), segmento);
            iniciarTemporizador(segmento.getId());

            new Thread(() -> {
                try {
                    Thread.sleep(TIMEOUT_MS + 2000);
                    dispararPacoteRede(segmento);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
        else if (tipo.equals("fora de ordem")) {
            pacoteRetido = segmento;
            System.out.println("-> [Aviso] Pacote retido localmente. Faça um 'Envio Normal' para descarregá-lo junto.");
        }
    }

    private static void enviarPacote(SegmentoConfiavel segmento) {
        try {
            dispararPacoteRede(segmento);
            janela.put(segmento.getId(), segmento);
            iniciarTemporizador(segmento.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void dispararPacoteRede(SegmentoConfiavel segmento) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(segmento);
        byte[] dados = baos.toByteArray();
        DatagramPacket pacote = new DatagramPacket(dados, dados.length, ipDestino, portaReceiver);
        socket.send(pacote);
    }

    private static void iniciarTemporizador(int id) {
        Timer timer = new Timer();
        timer.schedule(new TemporizadorPacote(id), TIMEOUT_MS);
        timers.put(id, timer);
    }

    private static class RecebedorDeAck implements Runnable {
        public void run() {
            byte[] bufferEntrada = new byte[1024];
            while (true) {
                try {
                    DatagramPacket pacoteRecebido = new DatagramPacket(bufferEntrada, bufferEntrada.length);
                    socket.receive(pacoteRecebido);

                    ByteArrayInputStream bais = new ByteArrayInputStream(pacoteRecebido.getData());
                    ObjectInputStream ois = new ObjectInputStream(bais);
                    SegmentoConfiavel ack = (SegmentoConfiavel) ois.readObject();

                    if (ack.isAck()) {
                        int idAck = ack.getId();
                        if (janela.containsKey(idAck)) {
                            janela.remove(idAck);
                            Timer timer = timers.remove(idAck);
                            if (timer != null) timer.cancel();
                            System.out.printf(">>> \nMensagem id %d recebida pelo receiver.\n", idAck);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class TemporizadorPacote extends TimerTask {
        private int idPacote;
        public TemporizadorPacote(int idPacote) { this.idPacote = idPacote; }

        @Override
        public void run() {
            if (janela.containsKey(idPacote)) {
                System.out.printf("\nMensagem id %d deu timeout, reenviando...\n", idPacote);
                SegmentoConfiavel segmento = janela.get(idPacote);
                segmento.setTipoEnvio("normal"); 
                timers.remove(idPacote);
                enviarPacote(segmento); 
            }
        }
    }

    // --------------------------------------------------------------------------------------------------
    // MAIN
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            socket = new DatagramSocket(portaSender);
            System.out.print("Digite o IP do Receiver (Enter para 127.0.0.1): ");
            String ipInput = scanner.nextLine();
            ipDestino = ipInput.trim().isEmpty() ? InetAddress.getByName("127.0.0.1") : InetAddress.getByName(ipInput);

            new Thread(new RecebedorDeAck()).start();

            while (true) {
                exibirMenu();
                String escolha = menuEscolha(scanner);

                if (escolha.equals("1")) {
                    rodarModoEnvio(scanner, "normal");
                } else if (escolha.equals("2")) {
                    rodarModoEnvio(scanner, "duplicado");
                } else if (escolha.equals("3")) {
                    rodarModoEnvio(scanner, "perda");
                } else if (escolha.equals("4")) {
                    rodarModoEnvio(scanner, "lenta");
                } else if (escolha.equals("5")) {
                    rodarModoEnvio(scanner, "fora de ordem");
                } else if (escolha.equals("0")) {
                    System.out.println("\nObrigado por usar o programa!\nSaindo...");
                    System.exit(0);
                    break;
                }
            System.out.print("\nDeseja continuar usando o programa? (s/n): ");
            String continuar = scanner.nextLine().trim().toLowerCase();

            if (!continuar.equals("s")) {
            System.out.println("\nEncerrando o programa...");
            break;
            }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}