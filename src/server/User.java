package server;

public class User {
    
    String nome, senha;
    Led led;

    public User(String nome, String senha){
        this.nome = nome;
        this.senha = senha;
    }

    public String getPass(){
        return senha;
    }

}
