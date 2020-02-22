package bsu.rfe.java.group8.lab7.SHUDEYKO.var10;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class MainFrame extends JFrame {

    private static final String FRAME_TITLE = "Клиент мгновенных сообщений";

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private final JTextArea textAreaIncoming;
    private final JTextArea textAreaOutgoing;

    private static final int INCOMING_AREA_DEFAULT_ROWS = 10;
    private static final int OUTGOING_AREA_DEFAULT_ROWS = 5;

    private static final int FROM_FIELD_DEFAULT_COLUMNS = 10;
    private static final int TO_FIELD_DEFAULT_COLUMNS = 20;

    private final JTextField textFieldFrom;
    private final JTextField textFieldTo;

    private final static int SERVER_PORT = 4567;

    public MainFrame(){
        super(FRAME_TITLE);
        setSize(WIDTH, HEIGHT);
        Toolkit kit = Toolkit.getDefaultToolkit();
        setLocation((kit.getScreenSize().width - WIDTH)/2, (kit.getScreenSize().height - HEIGHT)/2);

        textAreaIncoming = new JTextArea(INCOMING_AREA_DEFAULT_ROWS, 0);
        textAreaIncoming.setEditable(false);

        final JScrollPane scrollPaneIncoming = new JScrollPane(textAreaIncoming);

        final JLabel labelFrom = new JLabel("Подпись");
        final JLabel labelTo = new JLabel("Получатель");

        textFieldFrom = new JTextField(FROM_FIELD_DEFAULT_COLUMNS);
        textFieldTo = new JTextField(TO_FIELD_DEFAULT_COLUMNS);

        textAreaOutgoing = new JTextArea(OUTGOING_AREA_DEFAULT_ROWS, 0);
        final JScrollPane scrollPaneOutgoing = new JScrollPane(textAreaOutgoing);

        final JPanel messagePanel = new JPanel();
        messagePanel.setBorder(BorderFactory.createTitledBorder("Сообщение"));

        final JButton sendButton = new JButton("Отправить");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final ServerSocket serverSocket = new ServerSocket(SERVER_PORT);

                    while(!Thread.interrupted()){
                        final Socket socket = serverSocket.accept();
                        final DataInputStream in = new DataInputStream(socket.getInputStream());

                        final String senderName = in.readUTF();
                        final String message = in.readUTF();

                        socket.close();

                        final String address = ((InetSocketAddress)socket.getRemoteSocketAddress()).getAddress().getHostAddress();
                        textAreaIncoming.append(senderName + " (" + address + "): " + message + "\n");
                    }

                } catch (IOException E){
                    E.printStackTrace();
                    JOptionPane.showMessageDialog(MainFrame.this, "Ошібка в работе сервера", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        }).start();

    }


    public static void main(String args[]){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final MainFrame frame = new MainFrame();
                frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }
}
