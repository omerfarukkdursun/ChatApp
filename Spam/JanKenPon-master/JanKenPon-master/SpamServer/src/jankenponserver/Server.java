/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jankenponserver;

import game.Message;
import game.OzelMesaj;
import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;

/**
 *
 * @author INSECT
 */
//client gelişini dinleme threadi
class ServerThread extends Thread {

    public void run() {
        //server kapanana kadar dinle
        while (!Server.serverSocket.isClosed()) {
            try {
                Server.Display("Client Bekleniyor...");
                // clienti bekleyen satır
                //bir client gelene kadar bekler
                Socket clientSocket = Server.serverSocket.accept();
                //client gelirse bu satıra geçer
                Server.Display("Client Geldi...");
                //gelen client soketinden bir sclient nesnesi oluştur
                //bir adet id de kendimiz verdik
                SClient nclient = new SClient(clientSocket, Server.IdClient);

                Server.IdClient++;
                //clienti listeye ekle.
                Server.Clients.add(nclient);
                //client mesaj dinlemesini başlat
                nclient.listenThread.start();

            } catch (IOException ex) {
                Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

public class Server {

    //server soketi eklemeliyiz
    public static ServerSocket serverSocket;
    public static int IdClient = 0;
    // Serverın dileyeceği port
    public static int port = 0;
    //Serverı sürekli dinlemede tutacak thread nesnesi
    public static ServerThread runThread;
    //public static PairingThread pairThread;

    public static ArrayList<SClient> Clients = new ArrayList<>();
    public static ArrayList<Oda> odalar = new ArrayList<>();

    //semafor nesnesi
    public static Semaphore pairTwo = new Semaphore(1, true);

    // başlaşmak için sadece port numarası veriyoruz
    public static void Start(int openport) {
        try {
            Server.port = openport;
            Server.serverSocket = new ServerSocket(Server.port);

            Server.runThread = new ServerThread();
            Server.runThread.start();

        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void Display(String msg) {

        System.out.println(msg);

    }

    // serverdan clietlara mesaj gönderme
    //clieti alıyor ve mesaj olluyor
    public static void Send(SClient cl, Message msg) {

        try {
            cl.sOutput.writeObject(msg);
        } catch (IOException ex) {
            Logger.getLogger(SClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void AllSend(Message msg) {
        for (SClient c : Clients) {
            Server.Send(c, msg);

        }

    }

    public static void Baglandi(Message msg) {

        if (Server.Clients.size() > 0) {

            DefaultListModel userList = new DefaultListModel();

            for (SClient client : Clients) {
                userList.addElement(client.name);
            }

            Message msg2 = new Message(Message.Message_Type.NewUser);
            msg2.content = userList;

            for (SClient c : Clients) {
                Server.Send(c, msg2);

            }

        }

    }

    public static void OzelGonder(Message msg) {

        OzelMesaj gelenMesaj = (OzelMesaj) msg.content;

        for (SClient client : Clients) {
            if (client.name.equals(gelenMesaj.alici)) {
                Message gidecekMesaj = new Message(Message.Message_Type.OzelMesaj);
                gidecekMesaj.content = gelenMesaj;
                Server.Send(client, gidecekMesaj);

            }
        }

    }
    public static void odalariGonder(){
        ArrayList<String> odaList = new ArrayList<>();
        for (Oda o : odalar) {
            odaList.add(o.odaAdi);
        }
        Message msg = new Message(Message.Message_Type.OdaOlustur);
        msg.content = odaList;
        for (SClient c : Clients) {
            Server.Send(c, msg);
        }
    }
    
    public static Oda odaBul(String odaAdi){
        for (Oda oda : odalar) {
            if(oda.odaAdi.equalsIgnoreCase(odaAdi)){
                return oda;
            }
        }
        System.out.println("Bulamadı!!!");
        return null;
    }
    
    public static void odaMsjDagit(ArrayList<String> msjBilgisi){
        Oda oda = odaBul(msjBilgisi.get(1));
        Message msj = new Message(Message.Message_Type.odaChat);
        msj.content = msjBilgisi;
        for (SClient theMsj : oda.clients) { 
                Send(theMsj, msj);
        }
    }

}
