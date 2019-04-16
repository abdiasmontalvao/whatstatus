package br.com.hbird.whatstatus.dominio.classes;

import java.io.File;
import java.io.Serializable;

public class Media implements Serializable {

    private File arquivo;
    private boolean selecionado;

    public Media() {
    }

    public Media(File arquivo) {
        this.arquivo = arquivo;
    }

    public File getArquivo() {
        return arquivo;
    }

    public void setArquivo(File arquivo) {
        this.arquivo = arquivo;
    }

    public boolean isSelecionado() {
        return selecionado;
    }

    public void setSelecionado(boolean selecionado) {
        this.selecionado = selecionado;
    }
}
