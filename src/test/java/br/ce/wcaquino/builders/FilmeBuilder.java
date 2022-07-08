package br.ce.wcaquino.builders;

import br.ce.wcaquino.entidades.Filme;

public class FilmeBuilder {

    private Filme filme;

    public FilmeBuilder() {
    }

    public static FilmeBuilder umFilme(){
        FilmeBuilder builder = new FilmeBuilder();
        builder.filme = new Filme();
        builder.filme.setEstoque(1);
        builder.filme.setNome("Filme 1");
        builder.filme.setPrecoLocacao(4.0);
        return builder;
    }

    public FilmeBuilder filmeSemEstoque(){
        filme.setEstoque(0);
        return this;
    }

    public FilmeBuilder alterandoValorLocacao(Double outroValor){
        filme.setPrecoLocacao(outroValor);
        return this;
    }

    public Filme instacia(){
        return this.filme;
    }
    
}
