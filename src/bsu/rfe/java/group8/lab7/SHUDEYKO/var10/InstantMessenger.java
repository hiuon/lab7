package bsu.rfe.java.group8.lab7.SHUDEYKO.var10;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class InstantMessenger implements MessageListener {

    private  MainFrame frame;
    private String sender;
    private ArrayList<MessageListener> listeners = new ArrayList<MessageListener>(10);

    public InstantMessenger(final MainFrame f){
        this.frame = f;
        startServer();
    }

    public void setSender(String sender){
        this.sender = sender;
    }

    public String getSender(){
        return sender;
    }

    public void sendMessage(){
        try{
            final String senderName = textFieldFrom.getText();
            final String destinationAddress = textFieldTo.getText();
            final String message = frame.getTextAreaOutgoing().getText();

            if (senderName.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Введите имя отправителя", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (destinationAddress.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Введите адрес узла-получателя", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (message.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Введите текст сообщения", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            final Socket socket = new Socket(destinationAddress, frame.getServerPort());

            final DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            out.writeUTF(senderName);
            out.writeUTF(message);

            socket.close();

            frame.getTextAreaIncoming().append("Я -> " + destinationAddress + ": " + message + "\n");
            frame.getTextAreaOutgoing().setText("");

        } catch (UnknownHostException E){
            E.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Не удалось отправить сообщение: узел-адресат не найден","Ошибка", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Не удалось отправить сообщение", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void startServer(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final ServerSocket serverSocket = new ServerSocket(frame.getServerPort());

                    while (!Thread.interrupted()) {
                        final Socket socket = serverSocket.accept();
                        final DataInputStream in = new DataInputStream(socket.getInputStream());

                        final String senderName = in.readUTF();
                        final String message = in.readUTF();

                        socket.close();

                        final String address = ((InetSocketAddress) socket.getRemoteSocketAddress()).getAddress().getHostAddress();
                        frame.getTextAreaIncoming().append(senderName + " (" + address + "): " + message + "\n");
                    }

                } catch (IOException E) {
                    E.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Ошибка в работе сервера", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        }).start();
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
