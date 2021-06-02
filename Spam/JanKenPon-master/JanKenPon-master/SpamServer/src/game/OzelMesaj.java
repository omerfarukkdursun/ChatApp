/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

/**
 *
 * @author Fatih
 */
public class OzelMesaj implements java.io.Serializable {

    public OzelMesaj(String gonderen, String alici, String mesaj) {
        this.gonderen = gonderen;
        this.alici = alici;
        this.mesaj = mesaj;
    }
    
    public OzelMesaj(String gonderen, String alici, Object content, String dosyaAdi) {
        this.gonderen = gonderen;
        this.alici = alici;
        this.content = content;
        this.dosyaAdi = dosyaAdi;

    }
    
    public Object content;
    public String dosyaAdi;

    public String gonderen;
    public String alici;
    public String mesaj;
}
