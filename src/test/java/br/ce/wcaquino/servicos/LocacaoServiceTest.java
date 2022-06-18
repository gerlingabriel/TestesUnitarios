package br.ce.wcaquino.servicos;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exception.FilmeSemEstoqueException;
import br.ce.wcaquino.exception.LocacaoException;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoServiceTest {

	private LocacaoService service;
	@Rule
	public ErrorCollector error = new ErrorCollector();

	@Rule
	public ExpectedException exp = ExpectedException.none();

	@Before
	public void inicarIntanciaLocacaoService(){
		service = new LocacaoService();
	}

    @Test
    public void testeAlugarFilme() throws Exception {

		//Cenário
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Anjos da noite", 0, 10.50);
		List<Filme> filmes = Arrays.asList(filme);

		//Ação
		Locacao alugarFilme = service.alugarFilme(usuario, filmes);

		// testes para ver se variaveis na class JAVA poderia ser vista aqui
		service.vDefauit= "";
		service.vPublica= "";
		service.vProtected="";
		//service.vPrivada=""; // Essa não é possivel enchergar

		//verificar - três formas de se realizar os testes
		Assert.assertEquals(alugarFilme.getValor(), 10.50, 0.01);
		Assert.assertThat(alugarFilme.getValor(), CoreMatchers.is(10.50) );
		error.checkThat(alugarFilme.getValor(), CoreMatchers.is(10.50));

		Assert.assertTrue(DataUtils.isMesmaData(alugarFilme.getDataLocacao(), new Date()) );
		Assert.assertThat(DataUtils.isMesmaData(alugarFilme.getDataLocacao(), new Date()),  CoreMatchers.is(true) );
		error.checkThat(DataUtils.isMesmaData(alugarFilme.getDataLocacao(), new Date()),  CoreMatchers.is(true) );

		Assert.assertTrue(DataUtils.isMesmaData(alugarFilme.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)) );
		Assert.assertThat(DataUtils.isMesmaData(alugarFilme.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)), CoreMatchers.is(true) );
		error.checkThat(DataUtils.isMesmaData(alugarFilme.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)), CoreMatchers.is(true) );
    }

	@Test(expected = FilmeSemEstoqueException.class) // forme elegante
	public void testeLocalcao_ErroAoALugarFilmeSemEstoque() throws Exception{

		//Cenário
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Anjos da noite", 0, 10.50);
		List<Filme> filmes = Arrays.asList(filme);
		
		//Ação
		service.alugarFilme(usuario, filmes);
	}

	@Test() // forme Robusta
	public void testeLocalcao_ErroUsuarioNull() throws FilmeSemEstoqueException{

		//Cenário
		Usuario usuario = null;
		Filme filme = new Filme("Anjos da noite", 0, 10.50);
		List<Filme> filmes = Arrays.asList(filme);
		
		//Ação
		try {
			service.alugarFilme(usuario, filmes);
			Assert.fail("Erro não chamado como esperado");
		} catch (LocacaoException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("Usuario não existe!"));
		}
	}

	@Test() // forme "nova"
	public void testeLocalcao_ErroFilmeNull() throws LocacaoException, FilmeSemEstoqueException{

		//Cenário;
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = null;
		// fica dentro do cenário, pois ele irá memorizar o que deve acontecer na parte de "AÇÃO"
		exp.expect(LocacaoException.class);
		exp.expectMessage("Filme não existe!");
		
		//Ação
		service.alugarFilme(usuario, filmes);
	}
    
}