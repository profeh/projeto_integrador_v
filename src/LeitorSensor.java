import com.fazecast.jSerialComm.SerialPort;
import java.util.Random;
import java.util.Scanner;

public class LeitorSensor {

    public static void main(String[] args) {
        boolean modoSimulacao = false;
        Scanner scanner = null;
        SerialPort porta = null;

        try {
            SerialPort[] portas = SerialPort.getCommPorts();
            if (portas.length == 0) {
                System.out.println("Nenhuma porta serial encontrada. Alternando para modo de simulação...");
                modoSimulacao = true;
            } else {
                porta = portas[0];
                porta.setBaudRate(9600);
                if (porta.openPort()) {
                    System.out.println("Porta " + porta.getSystemPortName() + " aberta com sucesso!");
                    scanner = new Scanner(porta.getInputStream());
                } else {
                    System.out.println("Erro ao abrir a porta. Alternando para modo de simulação...");
                    modoSimulacao = true;
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao acessar porta serial: " + e.getMessage());
            modoSimulacao = true;
        }

        Random random = new Random();
        while (true) {
            String linha;
            if (!modoSimulacao && scanner.hasNextLine()) {
                linha = scanner.nextLine();
            } else {
                float temperatura = 15 + random.nextFloat() * 30;
                int umidade = random.nextInt(101);
                float luz = random.nextFloat() * 1000;
                linha = String.format("Temperatura: %.1f °C\nUmidade: %d %%\nLuminosidade: %.0f lux", temperatura, umidade, luz);
            }

            System.out.println("Sensor na sala de estar:\n" + linha);

            if (linha.contains("Temperatura")) {
                String[] partes = linha.split("\n");
                float temperatura = Float.parseFloat(partes[0].split(":")[1].trim().split(" ")[0].replace(",", "."));
                int umidade = Integer.parseInt(partes[1].replaceAll("[^\\d]", ""));
                float luz = Float.parseFloat(partes[2].replaceAll("[^\\d.]", ""));

                System.out.print("Ambiente: ");
                System.out.print((temperatura < 18) ? "Frio, ligue o aquecedor. " : (temperatura > 33) ? "Quente, ligue o ar-condicionado. " : "Agradável. ");
                System.out.print((umidade < 30) ? "Seco. " : (umidade > 70) ? "Úmido. " : "Umidade Normal. ");
                System.out.println((luz < 200) ? "Escuro." : (luz > 800) ? "Claro." : "Iluminação Normal.");
                System.out.println("---------------------------------------------------");
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
        }

        if (scanner != null) scanner.close();
        if (porta != null) porta.closePort();
    }
}