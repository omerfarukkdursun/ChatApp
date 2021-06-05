/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jankenponclient;

import game.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import static jankenponclient.Client.sInput;
import game.Game;
import game.OzelMesaj;
import game.SohbetOdasi;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import javax.swing.DefaultListModel;

/**
 *
 * @author INSECT
 */
// serverdan gelecek mesajları dinleyen thread
class Listen extends Thread {

    public void run() {
        //soket bağlı olduğu sürece dön
        while (Client.socket.isConnected()) {
            try {
                //mesaj gelmesini bloking olarak dinyelen komut
                Message received = (Message) (sInput.readObject());
                

                
                //mesaj gelirse bu satıra geçer
                //mesaj tipine göre yapılacak işlemi ayır.
                switch (received.type) {
                    case Name:
                        break;
                    case RivalConnected:
                        String name = received.content.toString();

                        Game.ThisGame.btn_send_message.setEnabled(true);
                        Game.ThisGame.tmr_slider.start();
                        break;
                    case Disconnect:
                        break;
                    case Text:
                        Game.ThisGame.reciveText((String) received.content);

                        break;
                    case Selected:
                        Game.ThisGame.RivalSelection = (int) received.content;

                        break;

                    case NewUser:
                        Game.ThisGame.getNewUser((DefaultListModel) received.content);
                        break;
                    case OzelMesaj:
                        Game.ThisGame.OzelMesajiAl((OzelMesaj) received.content);
                        break;
                    case DosyaGonder:
                        System.out.println("DosyaGonder");
                        String home = System.getProperty("user.home"); //dosya kaydı için gerekli kullanıcı adını alır

                       
                        OzelMesaj m2 = (OzelMesaj) received.content;

                        File file = new File(home + "/Downloads/" + m2.dosyaAdi);
                        OutputStream os = new FileOutputStream(file);
                        byte[] bdizi = (byte[]) m2.content;// geleni cast eder.
                        os.write(bdizi);
                        System.out.println(m2.dosyaAdi+" geldi");
                        os.close();
                        break;

                    case Bitis:
                        break;
                    case OdaOlustur:
                        Game.ThisGame.getOdalar((ArrayList<String>)received.content);
                        System.out.println("Oda listesi geldi.");
                        break;
                    case OdayaKatil:
                        ArrayList<Object> odaBilgisi = (ArrayList<Object>) received.content;
                        SohbetOdasi oda = Game.ThisGame.odaBul((String)odaBilgisi.get(0));
                        ArrayList<String> names = (ArrayList<String>)(odaBilgisi.get(1));
                        oda.getOdadakiler(names);
                        break;
                    case odaChat:
                        ArrayList<String> msjBilgisi = (ArrayList<String>) received.content;
                        SohbetOdasi so = Game.ThisGame.odaBul(msjBilgisi.get(1));
                        so.getOdaMsj(msjBilgisi.get(0), msjBilgisi.get(2));
                        break;
                }

            } catch (IOException ex) {

                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                //Client.Stop();
                break;
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                //Client.Stop();
                break;
            }
        }

    }
}

public class Client {

    //her clientın bir soketi olmalı
    public static Socket socket;

    //verileri almak için gerekli nesne
    public static ObjectInputStream sInput;
    //verileri göndermek için gerekli nesne
    public static ObjectOutputStream sOutput;
    //serverı dinleme thredi 
    public static Listen listenMe;

    public static void Start(String ip, int port) {
        try {
            // Client Soket nesnesi
            Client.socket = new Socket(ip, port);
            Client.Display("Servera bağlandı");
            // input stream
            Client.sInput = new ObjectInputStream(Client.socket.getInputStream());
            // output stream
            Client.sOutput = new ObjectOutputStream(Client.socket.getOutputStream());
            Client.listenMe = new Listen();
            Client.listenMe.start();

            //ilk mesaj olarak isim gönderiyorum
            Message msg = new Message(Message.Message_Type.Name);
            msg.content = Game.ThisGame.txt_name.getText();
            Client.Send(msg);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //client durdurma fonksiyonu
    public static void Stop() {
        try {
            if (Client.socket != null) {
                Client.listenMe.stop();
                Client.socket.close();
                Client.sOutput.flush();
                Client.sOutput.close();

                Client.sInput.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void Display(String msg) {

        System.out.println(msg);

    }

    //mesaj gönderme fonksiyonu
    public static void Send(Message msg) {
        try {
            Client.sOutput.writeObject(msg);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
