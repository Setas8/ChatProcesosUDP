package clases;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Cliente_UDP implements Runnable {

    private DatagramSocket scUDP;
    private DatagramPacket paquete;//Cambiar a envío
    private Server_UDP server;
    private String nombreCliente = "";

    public Cliente_UDP(DatagramSocket scUDP, DatagramPacket paquete, Server_UDP server) {

        this.scUDP = scUDP;
        this.paquete = paquete;
        this.server = server;
    }
    public String getNombreCliente() {
        return nombreCliente;
    }

    public void enviarMensaje(String mensaje) {

        try {
            byte[] buffer = mensaje.getBytes();
            InetAddress direccionCliente = paquete.getAddress();
            int puertoCliente = paquete.getPort();

            DatagramPacket respuesta = new DatagramPacket(buffer, buffer.length, direccionCliente, puertoCliente);
            scUDP.send(respuesta);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void enviarListaClientes(String listaClientes) {
        enviarMensaje(listaClientes);
    }
    @Override
    public void run() {
        try {
            nombreCliente = new String(paquete.getData(), 0, paquete.getLength());
            System.out.println("Nuevo cliente conectado: " + nombreCliente);

            server.mandarListaClientes();

            while (true) {
                byte[] buffer = new byte[1024];
                DatagramPacket recibe = new DatagramPacket(buffer, buffer.length);

                scUDP.receive(recibe);

                String mensaje = new String(recibe.getData(), 0, recibe.getLength());
                server.mandarMensaje(mensaje, this);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}