package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.adicionarDias;

import java.util.Date;
import java.util.List;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exception.FilmeSemEstoqueException;
import br.ce.wcaquino.exception.LocacaoException;

public class LocacaoService {

	//Teste para ver se as variaveis são visiveis nos testes
	public String vPublica;
	private String vPrivada;
	protected String vProtected;
	String vDefauit;
	
	public Locacao alugarFilme(Usuario usuario, List<Filme> filmes) throws LocacaoException {

		if (usuario == null ) {
			throw new LocacaoException("Usuario não existe!");
		} else if (filmes == null) {
			throw new LocacaoException("Filme não existe!");
		}

		filmes.stream()
			.forEach(filme-> {
                try {
                    extracted(filme);
                } catch (FilmeSemEstoqueException e) {
                    e.printStackTrace();
                }
            });

		Locacao locacao = new Locacao();
		locacao.setFilmes(filmes);
		locacao.setUsuario(usuario);
		locacao.setDataLocacao(new Date());

		Double valorDaLocacao = valorDaLocacao(filmes);

		locacao.setValor(valorDaLocacao);

		//Entrega no dia seguinte
		Date dataEntrega = new Date();
		dataEntrega = adicionarDias(dataEntrega, 1);
		locacao.setDataRetorno(dataEntrega);
		
		//Salvando a locacao...	
		//TODO adicionar método para salvar
		
		return locacao;
	}

	private void extracted(Filme filme) throws FilmeSemEstoqueException {
		if(filme.getEstoque() == 0) {
			throw new FilmeSemEstoqueException();
		}
	}

	private Double valorDaLocacao(List<Filme> filmes) {
		return filmes
			.stream().mapToDouble(Filme::getPrecoLocacao).sum();
	}

}