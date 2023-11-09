/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import util.Mensagem;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import javax.lang.model.util.ElementScanner6;

import util.Estados;
import util.Status;

/**
 *
 * @author elder
 */
public class Server {

    private ServerSocket serverSocket;
    private int cont;

    private ArrayList<User> usuarios;
  
    public Server(){
        usuarios = new ArrayList<>();
        leds = new ArrayList<>();

        usuarios.add(new User("Bianca", "159645"));
        usuarios.add(new User("Luiz", "12345"));
    }
    


    private void criarServerSocket(int porta) throws IOException {
        serverSocket = new ServerSocket(porta);
        cont = 0;
    }

    protected User getUser(String nome){
        for (User u : usuarios) {
            if(u.nome.equals(nome))
             return u;
            
        }
        return null;
    }

    private Socket esperaConexao() throws IOException {
        Socket socket = serverSocket.accept();
        return socket;
    }


   
    public void connectionLoop() throws IOException{
        int id = 0;
        leds = new ArrayList<>();
        while (true) {
            System.out.println("Aguardando conexão...");
            Socket socket = esperaConexao();//protocolo
            System.out.println("Cliente conectado.");
            //Outro processo
            TrataConexao tc = new TrataConexao(this, socket, id++);
            Thread th = new Thread(tc);
            th.start();
            System.out.println("Cliente finalizado.");
        }

    }
    private ArrayList<Led> leds;

    protected Led cadastraLed(Integer id, Integer dono){
        //descobrir se já existe led
        if(procuraLed(id) != null){
            return null;
        }
        else{
            //faz cadastro do led
            Led c = new Led(id, dono);
            leds.add(c);
            return c;
        }
    }

    protected Boolean removeLed(Integer id){
        //descobrir se já existe led
        if(procuraLed(id) != null) {
            leds.remove(id);
            return true;
        }
        else{
            return false;
        }
    }


    protected Boolean getEstatusLed(Integer id){
        for (Led c : leds) {
            if(c.getId().equals(id))
                return c.isLigado();
        }
        return false;
    }

    protected Boolean ligar(Integer id){
        for (Led c : leds) {
            if(c.getId().equals(id))
                c.setLigado(true);
            return true;
        }
        return false;
    }



    protected Boolean desligar(Integer id){
        for (Led c : leds) {
            if(c.getId().equals(id))
                c.setLigado(false);
            return true;
        }
        return false;
    }

    protected String listarLeds() {
        String listaled = "";
        for (Led led : leds) {
        listaled += "\n|ID: " + led.getId() + ", Ligado: " + led.isLigado() +"|" ;
        }
        return listaled;
    }


    protected Led procuraLed(int id){
        for (Led c : leds) {
            if(c.getId().equals(id))
                return c;
        }
        return null;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ClassNotFoundException {
        
        try {
            //1
            Server server = new Server();
            server.criarServerSocket(5555);

            server.connectionLoop();
        } catch (IOException e) {
            //trata exceção
            System.out.println("Erro no servidor: " + e.getMessage());
        }
    }

}
