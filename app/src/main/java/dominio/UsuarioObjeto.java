package dominio;

/**
 * Created by Firzen on 19/06/2017.
 */

public class UsuarioObjeto {
    private Long id;
    private Usuario usuario;
    private Objeto objeto;
    private int itensPossuidos;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Objeto getObjeto() {
        return objeto;
    }

    public void setObjeto(Objeto objeto) {
        this.objeto = objeto;
    }
    public void setItensPossuidos(int itensPossuidos){
        this.itensPossuidos = itensPossuidos;
    }
    public int getItensPossuidos(){
        return itensPossuidos;
    }

}
