package bsu.rfe.java.group8.lab7.SHUDEYKO.var10;

import java.util.ArrayList;

public class InstantMessenger implements MessageListener {

    private String sender;
    private ArrayList<MessageListener> listeners = new ArrayList<MessageListener>(10);

    public InstantMessenger(){

    }

    public void setSender(String sender){
        this.sender = sender;
    }

    public String getSender(){
        return sender;
    }

    public void sendMessage(){

    }

    private void startServer(){

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
