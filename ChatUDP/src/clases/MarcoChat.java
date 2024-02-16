package clases;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

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
    private boolean desconectar = false;
    private String nombreUser;
    private Socket sc;
    private PrintWriter out;

    private final int PUERTO_SERVIDOR = 6001;
    private final String HOST = "localhost";

    public void lanzarChat(){
        this.setContentPane(mainPanel);
        this.setVisible(true);
        this.setTitle("CHAT DE  " + nombreUser.toUpperCase());
        conectarServidor();
        mandarUsuariosConectados();

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

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        //Enviar mensajes al panel del chat   ///MAndarlo por UDP
        btnEnviar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String user = nombreUsuario + "$-> ";

                String mensaje = user + tfChat.getText() + "\n";
                //Mandar el mensaje al otro
                out.println(mensaje);

                //Limpiar el área de texto
                tfChat.setText("");

            }
        });


        //Desconectar usuario
        btnDesconect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                out.close();
                try {
                    sc.close();
                } catch (IOException ioe) {
                    throw new RuntimeException(ioe);
                }
                System.exit(0);
            }
        });


    }

    private void conectarServidor() {

        try {
            sc = new Socket(HOST, PUERTO_SERVIDOR);

            out = new PrintWriter(sc.getOutputStream(), true);
            out.println(nombreUser);

            ClienteHilo ch = new ClienteHilo();
            Thread hiloCli = new Thread(ch);
            hiloCli.start();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private class ClienteHilo implements Runnable {

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(sc.getInputStream()));
                String texto = "";
                while ((texto = in.readLine()) != null){
                    taTextoChat.append(texto + "\n");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    private String darNombreChat(String nombre) {

        this.lblChat.setText(nombre);

        return this.lblChat.getText();
    }

    private void mandarUsuariosConectados() {

       this.taUsers.append(nombreUser);

    }


}
