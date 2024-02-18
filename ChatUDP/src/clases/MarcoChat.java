package clases;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.*;


public class MarcoChat extends JFrame {

    public static void main(String[] args) {


        String nombreUser = "";

        nombreUser = JOptionPane.showInputDialog("Escribe tu nick");
        MarcoChat chat = new MarcoChat(nombreUser);
        chat.lanzarChat();
    }

    private JPanel mainPanel;
    private JButton btnEnviar;
    private JButton btnDesconect;
    private JTextField tfChat;
    private JLabel lblChat;
    private JLabel lblUsers;
    private JTextArea taUsers;
    private JTextArea taTextoChat;
    private String nombreUser;
    private DatagramSocket sc;

    final int PUERTO_SERVER = 6001;


    public void lanzarChat(){
        this.setContentPane(mainPanel);
        this.setVisible(true);
        this.setTitle("CHAT DE  " + nombreUser.toUpperCase());
        conectarServidor();


    }
    public MarcoChat(String nombreUsuario) {

        this.nombreUser = nombreUsuario;

        Toolkit pantalla = Toolkit.getDefaultToolkit();

        // Coger la dimension de la pantalla 1900X1000
        Dimension pantallaSize = pantalla.getScreenSize();

        // Extraer el alto y el ancho (ejes x, y)
        int anchoPantalla = pantallaSize.width;
        int alturaPantalla = pantallaSize.height;

        this.setSize(anchoPantalla / 2, alturaPantalla / 2);
        this.setLocation(anchoPantalla / 4, alturaPantalla / 4);

        // Imagen gif pesa menos
        Image icono = pantalla.getImage("seta.gif");
        this.setIconImage(icono);

        //Impedir que se redimensione
        this.setResizable(false);

        this.darNombreChat(nombreUser.toUpperCase());
        mandarUsuariosConectados();

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        //Enviar mensajes al panel del chat   ///MAndarlo por UDP
        btnEnviar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String user = nombreUsuario + "$-> ";
                String mensaje = user + tfChat.getText() + "\n";

                try {
                    byte[] data = mensaje.getBytes();

                    InetAddress direccionServer = InetAddress.getByName("localhost");
                    DatagramPacket envio = new DatagramPacket(data, data.length, direccionServer, PUERTO_SERVER);
                    sc.send(envio);
                    //Limpiar el campo
                    tfChat.setText("");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
        });

        //Desconectar usuario
        btnDesconect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    sc.close();
                    System.exit(0);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });

    }

    private void conectarServidor() {

        try {
            sc = new DatagramSocket();

            String nombre = nombreUser;
            byte[] buffer = nombre.getBytes();
            InetAddress direccionServer = InetAddress.getByName("localhost");
            DatagramPacket paquete = new DatagramPacket(buffer, buffer.length, direccionServer, PUERTO_SERVER);
            sc.send(paquete);

            ClienteHilo cli = new ClienteHilo();
            Thread hiloCliente = new Thread(cli);
            hiloCliente.start();

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    private class ClienteHilo implements Runnable {

        @Override
        public void run() {
            try {
                while (true){
                    byte[] buffer = new byte[1024];
                    DatagramPacket recibir = new DatagramPacket(buffer, buffer.length);
                    sc.receive(recibir);

                    String texto = new String(recibir.getData(),0, recibir.getLength());

                    taTextoChat.append(texto + "\n");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    private void darNombreChat(String nombre) {

        this.lblChat.setText(nombre);
        //this.taUsers.append(nombreUser);

        //return this.lblChat.getText();
    }

    private void mandarUsuariosConectados() {


       this.taUsers.append(nombreUser);

    }


}
