package clases;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Set;

public class Server_UDP {

    public static void main(String[] args) {
        Server_UDP server = new Server_UDP();
        server.lanzar();
    }
    private static final int PUERTO_SERVER = 6001;
    private Set<Cliente_UDP> clientes = new HashSet<>();


    public void lanzar() {


        DatagramSocket sc;

        try {
            sc = new DatagramSocket(PUERTO_SERVER);
            System.out.println("sERVIDOR ESCUCHANDO EN EL PUERTO " + PUERTO_SERVER);

            while(true){
                byte[] buffer = new byte[1024];
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
                sc.receive(paquete);

                //Crear cliente
                Cliente_UDP cli = new Cliente_UDP(sc, paquete, this);
                clientes.add(cli);
                Thread hiloCli = new Thread(cli);
                hiloCli.start();

            }



        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void mandarMensaje(String mensaje, Cliente_UDP cliente){

        for(Cliente_UDP cl : clientes){
            if (cl != cliente){
                cl.enviarMensaje(mensaje);
            }
        }
    }
    //Método eliminar cliente

}
