package Estruturas;

import java.util.ArrayList;
import java.util.List;

public class Automato {

    private List<Transicao> transition = new ArrayList<Transicao>();
    private List<Estado> estado = new ArrayList<Estado>();
    private List<EstadoFinal> estadofinal = new ArrayList<EstadoFinal>();
    private EstadoInicial estadoinicial = new EstadoInicial();
    private String alfabeto;

    public List<Transicao> getTransition() {
        return transition;
    }

    public void setTransition(List<Transicao> transition) {
        this.transition = transition;
    }

    public List<Estado> getEstado() {
        return estado;
    }

    public void setEstado(List<Estado> estado) {
        this.estado = estado;
    }

    public List<EstadoFinal> getEstadofinal() {
        return estadofinal;
    }

    public void setEstadofinal(List<EstadoFinal> estadofinal) {
        this.estadofinal = estadofinal;
    }

    public EstadoInicial getEstadoinicial() {
        return estadoinicial;
    }

    public void setEstadoincial(EstadoInicial estadoincial) {
        this.estadoinicial = estadoincial;
    }

    public String getAlfabeto() {
        return alfabeto;
    }

    public void setAlfabeto(String alfabeto) {
        this.alfabeto = alfabeto;
    }
}