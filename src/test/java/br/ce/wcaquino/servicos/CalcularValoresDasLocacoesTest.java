package br.ce.wcaquino.servicos;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exception.FilmeSemEstoqueException;
import br.ce.wcaquino.exception.LocacaoException;

@RunWith(Parameterized.class)
public class CalcularValoresDasLocacoesTest {

    public LocacaoService service;

	@Parameter
    public List<Filme> filmes;

	@Parameter(value = 1)
    public Double valorDaLocacao;

	@Parameter(value = 2)
    public String cenario;

    @Before
	public void inicarIntanciaLocacaoService(){
		service = new LocacaoService();
	}

	private static Filme filme1 = new Filme("Filme1", 1, 4.0);
	private static Filme filme2 = new Filme("Filme1", 1, 4.0);
	private static Filme filme3 = new Filme("Filme1", 1, 4.0);
	private static Filme filme4 = new Filme("Filme1", 1, 4.0);
	private static Filme filme5 = new Filme("Filme1", 1, 4.0);
	private static Filme filme6 = new Filme("Filme1", 1, 4.0);

	@Parameters(name = "{2} - {1}")
	public static Collection<Object []> getParamCollection(){
		return Arrays.asList(new Object[][]{
			{Arrays.asList(filme1), 4.0, "Alugando 1 Filme"},
			{Arrays.asList(filme1, filme2), 8.0, "Alugando 2 Filme"},
			{Arrays.asList(filme1, filme2, filme3), 11.0, "Alugando 3 Filme"},
			{Arrays.asList(filme1, filme2, filme3, filme4), 13.0, "Alugando 4 Filme"},
			{Arrays.asList(filme1, filme2, filme3, filme4, filme5), 14.0, "Alugando 5 Filme"},
			{Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6), 14.0, "Alugando 6 Filme"},
			{Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6, filme1), 18.0, "Alugando 7 Filme"}
		});
	}

    @Test
	public void deveCalcularValorDaLocacaoDeAcordoComQuantidadeDeFilmes() throws LocacaoException, FilmeSemEstoqueException{

		//Cenário
		Usuario usuario = new Usuario("Usuario 1");
		
		//Ação
		Locacao alugarFilmes = service.alugarFilmes(usuario, filmes);

		//Verificação
		// 4 + 4 + 3 (25%) =  11
		Assert.assertThat(alugarFilmes.getValor(), CoreMatchers.is(valorDaLocacao));
	}
    
}