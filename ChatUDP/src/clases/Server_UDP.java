package clases;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
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
            System.out.println("SERVIDOR ESCUCHANDO EN EL PUERTO " + PUERTO_SERVER);

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
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void mandarMensaje(String mensaje, Cliente_UDP cliente){

        for(Cliente_UDP cl : clientes){
            if (cl != cliente){
                cl.enviarMensaje(mensaje);
            }
        }
    }
    public void mandarListaClientes() {
        StringBuilder listaClientes = new StringBuilder("Usuarios conectados:\n");
        for (Cliente_UDP cliente : clientes) {
            listaClientes.append("- ").append(cliente.getNombreCliente()).append("\n");
        }

        for (Cliente_UDP cliente : clientes) {
            cliente.enviarListaClientes(listaClientes.toString());
        }
    }
    //Método eliminar cliente
    public void eliminarCliente(Cliente_UDP client) {
        clientes.remove(client);
    }
}
