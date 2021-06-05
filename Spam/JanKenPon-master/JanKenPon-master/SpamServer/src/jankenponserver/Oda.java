/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jankenponserver;

import java.util.ArrayList;

/**
 *
 * @author Fatih
 */
public class Oda {
    String odaAdi;
    ArrayList<SClient> clients = new ArrayList<>();

    public Oda(String odaAdi) {
        this.odaAdi = odaAdi;
    }
    
    
}
