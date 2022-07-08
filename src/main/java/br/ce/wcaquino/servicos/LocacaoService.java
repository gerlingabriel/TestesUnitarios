package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.adicionarDias;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.ce.wcaquino.dao.LocacaoDao;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exception.FilmeSemEstoqueException;
import br.ce.wcaquino.exception.LocacaoException;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoService {

	//Teste para ver se as variaveis são visiveis nos testes
	public String vPublica;
	private String vPrivada;
	protected String vProtected;
	String vDefauit;

	private LocacaoDao dao;
	private SPCService spcService;
	private EmailService emailService;
	
	public Locacao alugarFilmes(Usuario usuario, List<Filme> filmes) throws LocacaoException, FilmeSemEstoqueException {

		if (usuario == null ) {
			throw new LocacaoException("Usuario não existe!");
		} else if (filmes == null || filmes.isEmpty() ) {
			throw new LocacaoException("Filme não existe!");
		}

		for (Filme filme : filmes) {
			if(filme.getEstoque() == 0) {
				throw new FilmeSemEstoqueException();
			}			
		}

		boolean negativado;

		try {
			negativado = spcService.nomeNegativado(usuario);
		} catch (Exception e) {
			throw new LocacaoException("Problemas no sistema, favor refazer a pesquisa!");
		}

		if (negativado) {
			throw new LocacaoException("Usuario Negativado!");
		}

		Locacao locacao = new Locacao();
		locacao.setFilmes(filmes);
		locacao.setUsuario(usuario);
		locacao.setDataLocacao(buscarDataDeHoje());

		Double valorDaLocacao = valorDaLocacao(filmes);

		locacao.setValor(valorDaLocacao);

		//Entrega no dia seguinte
		Date dataEntrega = buscarDataDeHoje();
		dataEntrega = adicionarDias(dataEntrega, 1);
		if (DataUtils.verificarDiaSemana(dataEntrega, Calendar.SUNDAY)) {
			dataEntrega = adicionarDias(dataEntrega, 1);
		}
		locacao.setDataRetorno(dataEntrega);
		
		//Salvando a locacao...	
		dao.salvar(locacao);
		
		return locacao;
	}

	protected Date buscarDataDeHoje() {
		return new Date();
	}

	public void notificarUsuarioComAtrasoFilme(){
		List<Locacao> usuariosComLocacao = dao.listaDeUsuarios();
		for (Locacao locacao : usuariosComLocacao) {
			if (locacao.getDataRetorno().before(buscarDataDeHoje())) {
				emailService.notificarUsuario(locacao.getUsuario());
			}
		}
	}

	public void prorrogarUmaLocacao(Locacao locacao, int dias){
		Locacao novaLocacao = new Locacao();

		novaLocacao.setUsuario(locacao.getUsuario());
		novaLocacao.setFilmes(locacao.getFilmes());
		novaLocacao.setValor(locacao.getValor() * dias);

		novaLocacao.setDataLocacao(buscarDataDeHoje());
		novaLocacao.setDataRetorno(DataUtils.obterDataComDiferencaDias(dias));

		dao.salvar(novaLocacao);
	}

	private Double valorDaLocacao(List<Filme> filmes) {

		double valorTotal = 0.0;
		int cont = 1;
		for (Filme filme : filmes) {

			switch (cont) {
				case 3:	valorTotal += (filme.getPrecoLocacao()*0.75); break;

				case 4:	valorTotal += (filme.getPrecoLocacao()*0.50); break;

				case 5:	valorTotal += (filme.getPrecoLocacao()*0.25); break;

				case 6:	valorTotal += 0; break;
			
				default: valorTotal += filme.getPrecoLocacao(); break;
			}
			cont++;			
		}
		return valorTotal;
	}

}