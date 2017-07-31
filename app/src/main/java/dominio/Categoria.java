package dominio;

import java.io.Serializable;

/**
 * Created by Firzen on 17/06/2017.
 */

public class Categoria implements Serializable{
    private String id;
    private String descricao;
    private Integer count = 0;
    private String unidade;

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public boolean isEnumeravel() {
        return enumeravel;
    }

    public void setEnumeravel(boolean enumeravel) {
        this.enumeravel = enumeravel;
    }

    private boolean enumeravel;

    public Categoria(){

    }
    public Categoria(String descricao){
        this.descricao = descricao;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
