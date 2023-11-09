package server;
class Led {
    private Integer id;
    private Boolean ligado ;
     private Integer dono;
    public Led(Integer id, Integer dono){
        this.id = id;
        this.dono = dono;
        this.ligado = false;
    }

    public Integer getId() {
        return id;
    }

    public Boolean isLigado() {
        return ligado;
    }

    public void setLigado(boolean ligado) {
        this.ligado = ligado;
    }

    public Integer getDono() {
        return dono;
    }

    public void setDono(Integer dono) {
        this.dono = dono;
    }
}
