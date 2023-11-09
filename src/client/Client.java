/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Date;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.Mensagem;
import util.Status;

/**
 *
 * @author elder
 */
public class Client {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        try {
            int id = 0;
            boolean execucao = true;
            Random random = new Random();


            Scanner scanner = new Scanner(System.in);

            System.out.println("Estabelecendo conexão...");
            Socket socket = new Socket("localhost", 5555);
            System.out.println("Conexão estabelecida.");

            // criação dos streams de entrada e saída
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            while (execucao){
                Mensagem msgEnvio;
                Mensagem msgResposta;

                System.out.println("Menu:");
                System.out.println("1 - Sair");
                System.out.println("2 - Login");
                System.out.println("3 - Logout");
                System.out.println("4 - Cadastrar LED");
                System.out.println("5 - Remover LED");
                System.out.println("6 - Ligar LED");
                System.out.println("7 - Desligar LED");
                System.out.println("8 - Listar LED");
                System.out.print("Escolha uma opção: ");

                int escolha = scanner.nextInt();
                scanner.nextLine(); // Limpar a nova linha após a leitura do número

                switch (escolha) {
                    case 1:
                        msgEnvio = new Mensagem("SAIR");
                        output.writeObject(msgEnvio);
                        output.flush();
                        msgResposta= (Mensagem) input.readObject();
                        System.out.println("Resposta: " + msgResposta);
                        execucao=false;
                        break;
                    case 2:
                        msgEnvio = new Mensagem("LOGIN");

                        System.out.println("Insira o usuario:");
                        String user = scanner.next();
                        scanner.nextLine();
                        System.out.println("Insira a senha:");

                        String pass = scanner.next();
                        scanner.nextLine();

                        msgEnvio.setParam("user", user);
                        msgEnvio.setParam("pass", pass);

                        output.writeObject(msgEnvio);
                        output.flush();

                        msgResposta = (Mensagem) input.readObject();
                        System.out.println("Resposta: " + msgResposta);

                        break;
                    case 3:
                        msgEnvio = new Mensagem("LOGOUT");

                        output.writeObject(msgEnvio);
                        output.flush();

                        msgResposta = (Mensagem) input.readObject();
                        System.out.println("Resposta: " + msgResposta);
                        break;
                    case 4:
                        msgEnvio = new Mensagem("CADASTRALED");
                        msgEnvio.setParam("id" , (Integer)  random.nextInt(20)+ id++);
                        output.writeObject(msgEnvio);
                        output.flush();
                        msgResposta= (Mensagem) input.readObject();
                        System.out.println("Resposta: " + msgResposta);
                        break;
                    case 5:
                        System.out.println("Qual led deseja REMOVER: ");
                        int IdRemover = scanner.nextInt();
                        scanner.nextLine();
                        msgEnvio = new Mensagem("REMOVELED");
                        msgEnvio.setParam("id" , IdRemover);
                        output.writeObject(msgEnvio);
                        output.flush();
                        msgResposta= (Mensagem) input.readObject();
                        System.out.println("Resposta: " + msgResposta);
                        break;
                    case 6:
                        System.out.println("Qual led deseja Ligar: ");
                        int IdLigar = scanner.nextInt();
                        scanner.nextLine();
                        msgEnvio = new Mensagem("LIGARLED");
                        msgEnvio.setParam("id", IdLigar);
                        output.writeObject(msgEnvio);
                        output.flush();
                        msgResposta = (Mensagem) input.readObject();
                        System.out.println("Resposta: " + msgResposta);
                        break;
                    case 7:
                        System.out.println("Qual led deseja Desligar: ");
                        int IdDesligar = scanner.nextInt();
                        scanner.nextLine();
                        msgEnvio = new Mensagem("DESLIGARLED");
                        msgEnvio.setParam("id", IdDesligar);
                        output.writeObject(msgEnvio);
                        output.flush();
                        msgResposta = (Mensagem) input.readObject();
                        System.out.println("Resposta: " + msgResposta);
                        break;
                    case 8:
                        msgEnvio = new Mensagem("LISTARLEDS");
                        output.writeObject(msgEnvio);
                        output.flush();
                        msgResposta = (Mensagem) input.readObject();
                        System.out.println("Resposta: " + msgResposta);
                        break;
                    default:
                        System.out.println("Opção inválida.");
                }
            }

            input.close();
            output.close();
            socket.close();

        } catch (IOException ex) {
            System.out.println("Erro no cliente: " + ex);
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            System.out.println("Erro no cast: " + ex.getMessage());
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
