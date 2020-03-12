package bsu.rfe.java.group8.lab7.SHUDEYKO.var10;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class InstantMessenger implements MessageListener {
    private  MainFrame frame;
    private String sender;
    private ArrayList<MessageListener> listeners = new ArrayList<MessageListener>(10);
    private int port = 4567;

    public InstantMessenger(final MainFrame f){
        this.frame = f;
        while(!availablePort("127.0.0.1", port)){
            port++;
        }
        startServer();
    }

    public void setSender(String sender){
        this.sender = sender;
    }

    public String getSender(){
        return sender;
    }

    public void sendMessage(Peer peer){
        try{
            final String senderName = this.getSender();
            final String[] destinationAddress = peer.getAddress().split("::");
            final String message = frame.getTextAreaOutgoing().getText() + "::" + Integer.toString(port);

            if (senderName.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Введите имя отправителя", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (destinationAddress[0].isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Введите адрес узла-получателя", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (message.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Введите текст сообщения", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            final Socket socket = new Socket(destinationAddress[0], Integer.parseInt(destinationAddress[1]));

            final DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            out.writeUTF(senderName);
            out.writeUTF(message);

            socket.close();

            append("Я -> " + destinationAddress[0]+"::"+destinationAddress[1] + ": " + frame.getTextAreaOutgoing().getText() +"\n", frame.getTextAreaIncoming());
            frame.getTextAreaOutgoing().setText("");

        } catch (UnknownHostException E){
            E.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Не удалось отправить сообщение: узел-адресат не найден","Ошибка", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Не удалось отправить сообщение", "Ошибка", JOptionPane.ERROR_MESSAGE);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }


    private boolean availablePort(String host, int port) {
        boolean result = true;

        try {
            (new Socket(host, port)).close();

            result = false;
        }
        catch(IOException e) {
        }
        return result;
    }

    private void startServer(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    final ServerSocket serverSocket = new ServerSocket(port);

                    while (!Thread.interrupted()) {
                        final Socket socket = serverSocket.accept();
                        final DataInputStream in = new DataInputStream(socket.getInputStream());

                        final String senderName = in.readUTF();
                        final String message = in.readUTF();

                        socket.close();

                        final String address = ((InetSocketAddress) socket.getRemoteSocketAddress()).getAddress().getHostAddress();
                        String[] mes = message.split("::");

                        append(senderName + "(", frame.getTextAreaIncoming());
                        appendHyperlink(address+"::"+ mes[1], frame);
                        append("):"+mes[0]+"\n",frame.getTextAreaIncoming());
                    }

                } catch (IOException | BadLocationException E) {
                    //E.printStackTrace();
                    //JOptionPane.showMessageDialog(frame, "Ошибка в работе сервера", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    startServer();
                }
            }
        }).start();
    }

    public void append(String s, JTextPane pane) throws IOException, BadLocationException {
        StyledDocument doc = pane.getStyledDocument();
        doc.insertString(doc.getLength(), s, null);
        pane.setDocument(doc);
    }

    public void appendHyperlink(String s, MainFrame frame) throws IOException, BadLocationException {
        StyledDocument doc = frame.getTextAreaIncoming().getStyledDocument();
        Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        Style regularBlue = doc.addStyle("regularBlue", def);
        StyleConstants.setForeground(regularBlue, Color.BLUE);
        StyleConstants.setUnderline(regularBlue,true);
        regularBlue.addAttribute("link",frame.getURLLinkAction(s));
        doc.insertString(doc.getLength(), s, regularBlue);
        frame.getTextAreaIncoming().setDocument(doc);
    }

    public Integer getPort(){
        return port;
    }

    public void addMessageListener(MessageListener listener){
        synchronized (listeners){
            listeners.add(listener);
        }
    }

    public void removeMessageListener(MessageListener listener){
        synchronized (listeners){
            listeners.remove(listener);
        }
    }

    private void notifyListeners(Peer sender, String message){
        synchronized (listeners){
            for (MessageListener listener : listeners){
                listener.messageReceived(sender, message);
            }
        }
    }

    @Override
    public void messageReceived(Peer senderName, String message) {
        
    }
}
