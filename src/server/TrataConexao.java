package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import util.Estados;
import util.Mensagem;
import util.Status;

public class TrataConexao implements Runnable {

    private Server server;
    private Socket socket;
    private int id;
    private String user;
    User auth;

    ObjectOutputStream output;
    ObjectInputStream input;

    private Estados estado;

    public TrataConexao(Server server, Socket socket, int id) {
        this.server = server;
        this.socket = socket;
        this.id = id;
        estado = Estados.CONECTADO;
    }

    private void fechaSocket(Socket s) throws IOException {
        s.close();
    }

    private void enviaMsg(Object o, ObjectOutputStream out) throws IOException {
        out.writeObject(o);
        out.flush();
    }

    private void trataConexao() throws IOException, ClassNotFoundException {


        try {



            // Gerar um número aleatório até 20 (exclusivo)

            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
            System.out.println("Tratando...");

            estado = Estados.CONECTADO;

            while (estado != Estados.SAIR) {

                Mensagem m = (Mensagem) input.readObject();
                System.out.println("Mensagem do cliente:\n" + m);

                String operacao = m.getOperacao();
                Mensagem reply = new Mensagem(operacao + "REPLY");


                switch (estado) {
                    case CONECTADO:
                        switch (operacao) {

                            case "LOGIN":
                                try {
                                    String user = (String) m.getParam("user");
                                    String pass = (String) m.getParam("pass");

                                    auth = server.getUser(user);

                                    if (auth.nome.equals(user) && auth.senha.equals(pass)) {
                                        reply.setStatus(Status.OK);
                                        this.user = user;
                                        estado = Estados.AUTENTICADO;
                                    } else {
                                        reply.setStatus(Status.ERROR);
                                    }

                                } catch (Exception e) {
                                    reply.setStatus(Status.PARAMERROR);
                                    reply.setParam("msg", "Erro nos parâmetros do protocolo.");
                                }
                                break;
                            case "SAIR":
                                reply.setStatus(Status.OK);
                                estado = Estados.SAIR;
                                break;
                            default:
                                reply.setStatus(Status.ERROR);
                                reply.setParam("msg", "MENSAGEM NÃO AUTORIZADA OU INVÁLIDA!");

                                break;
                        }
                        break;
                    case AUTENTICADO:
                        switch (operacao) {
                            case "LOGOUT":
                                    reply.setStatus(Status.OK);
                                    estado =Estados.CONECTADO;
                                break;
                            case "LIGARLED":
                                try {
                                    Integer id = (Integer) m.getParam("id");
                                    if (id == null){
                                        reply.setStatus(Status.PARAMERROR);
                                        reply.setParam("msg","Erro:parametros incorretos");
                                        break;
                                    }else if(server.procuraLed(id) == null){
                                        reply.setStatus(Status.PARAMERROR);
                                        reply.setParam("msg","Erro: led nao cadastrado");
                                        break;
                                    }else if(server.getEstatusLed(id)){
                                        reply.setStatus(Status.PARAMERROR);
                                        reply.setParam("msg","Erro: led ja ligado");
                                        break;
                                    }
                                    server.ligar(id);

                                    reply.setStatus(Status.OK);
                                    reply.setParam("msg","Led ligado");

                                }catch (Exception e){
                                    reply.setStatus(Status.PARAMERROR);
                                    reply.setParam("msg", "Erro: parâmetros do protocolo.");

                                }
                                break;
                            case "DESLIGARLED":
                                try {
                                    Integer id = (Integer) m.getParam("id");
                                    if (id == null){
                                        reply.setStatus(Status.PARAMERROR);
                                        reply.setParam("msg","Erro: parametros");
                                        break;
                                    }else if(server.procuraLed(id) == null){
                                        reply.setStatus(Status.PARAMERROR);
                                        reply.setParam("msg","Erro: led nao cadastrado");
                                        break;
                                    }else if(!server.getEstatusLed(id)){
                                        reply.setStatus(Status.PARAMERROR);
                                        reply.setParam("msg","Erro: led ja desligado");
                                        break;
                                    }
                                    server.desligar(id);

                                    reply.setStatus(Status.OK);
                                    reply.setParam("msg","Led desligado");

                                }catch (Exception e){
                                    reply.setStatus(Status.PARAMERROR);
                                    reply.setParam("msg", "Erro: parâmetros do protocolo.");

                                }
                                break;
                            case "CADASTRALED":
                                try {
                                    Integer id = (Integer) m.getParam("id");

                                    if (id == null){
                                        reply.setStatus(Status.PARAMERROR);
                                        reply.setParam("msg","Erro: parametros");
                                        break;
                                    }else if (server.cadastraLed(id,this.id) == null){
                                        reply.setStatus(Status.PARAMERROR);
                                        reply.setParam("msg","Erro: led já cadastrado");
                                        break;
                                    }

                                    server.cadastraLed(id, this.id);

                                    reply.setStatus(Status.OK);
                                    reply.setParam("msg","Led cadastrado");


                                }catch (Exception e){
                                    reply.setStatus(Status.PARAMERROR);
                                    reply.setParam("msg", "Erro: parâmetros do protocolo.");
                                }
                                break;
                            case "REMOVELED":
                                try {
                                    Integer id = (Integer) m.getParam("id");

                                    if (id == null){
                                        reply.setStatus(Status.PARAMERROR);
                                        reply.setParam("msg","Erro: parametros");
                                        break;
                                    }else if (server.cadastraLed(id,this.id) != null){
                                        reply.setStatus(Status.PARAMERROR);
                                        reply.setParam("msg","Erro: led nao cadastrado");
                                        break;
                                    }

                                    server.removeLed(id);

                                    reply.setStatus(Status.OK);
                                    reply.setParam("msg","Led removido");


                                }catch (Exception e){
                                    reply.setStatus(Status.PARAMERROR);
                                    reply.setParam("msg", "Erro: parâmetros do protocolo.");
                                }
                                break;
                            case "LISTARLEDS":
                                    String leds;
                                    leds = server.listarLeds();

                                    if (leds.isEmpty()){
                                        reply.setStatus(Status.ERROR);
                                        reply.setParam("msg","Nenhum led cadastrado");
                                        break;
                                    }

                                reply.setStatus(Status.OK);
                                reply.setParam("msg",leds);
                                break;
                            case "SAIR":
                                // DESIGN PATTERN STATE
                                reply.setStatus(Status.OK);
                                estado = Estados.SAIR;
                                break;
                            default:
                                reply.setStatus(Status.ERROR);
                                reply.setParam("msg", "MENSAGEM NÃO AUTORIZADA OU INVÁLIDA!");
                                break;
                        }
                    case SAIR: // ESTADP
                        break;

                }

                output.writeObject(reply);
                output.flush();// cambio do rádio amador
            }
            // 4.2 - Fechar streams de entrada e saída
            input.close();
            output.close();
        } catch (IOException e) {
            // tratamento de falhas
            System.out.println("Problema no tratamento da conexão com o cliente: " + socket.getInetAddress());
            System.out.println("Erro: " + e.getMessage());
            // throw e; //FOI REMOVIDO
        } finally {
            // final do tratamento do protocolo
            /* 4.1 - Fechar socket de comunicação entre servidor/cliente */
            fechaSocket(socket);
        }

    }

    @Override
    public void run() {
        try {
            trataConexao();
        } catch (ClassNotFoundException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("Erro no tratamento de conexão" + e.getMessage());
        }
    }

}
