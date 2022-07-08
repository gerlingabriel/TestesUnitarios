package br.ce.wcaquino.builders;

import java.util.Arrays;
import java.util.Date;

import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoBuilder {

    private Locacao locacao;

    public LocacaoBuilder() {

    }

    public static LocacaoBuilder umaLocacao(){
        LocacaoBuilder builder = new LocacaoBuilder();
        builder.locacao = new Locacao();
        builder.locacao.setFilmes(Arrays.asList(FilmeBuilder.umFilme().instacia()));
        builder.locacao.setUsuario(UsuarioBuilder.umUsuario().instancia());
        builder.locacao.setDataLocacao(new Date());
        builder.locacao.setDataRetorno(DataUtils.adicionarDias(new Date(), 1));
        builder.locacao.setValor(4.0);
        return builder;
    }

    public LocacaoBuilder alterandodataDaEntraga(){
        locacao.setDataLocacao(DataUtils.obterDataComDiferencaDias(-4));
        locacao.setDataRetorno(DataUtils.obterDataComDiferencaDias(-2));
        return this;
    }

    public LocacaoBuilder alterarOUsuario(Usuario usuario){
        locacao.setUsuario(usuario);
        return this;
    }

    public Locacao instancia(){
        return this.locacao;
    }

    
    
}
