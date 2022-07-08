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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.ce.wcaquino.builders.FilmeBuilder;
import br.ce.wcaquino.builders.UsuarioBuilder;
import br.ce.wcaquino.dao.LocacaoDao;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;

@RunWith(Parameterized.class)
public class CalcularValoresDasLocacoesTest {

	@InjectMocks
    public LocacaoService service;

	@Mock
	public LocacaoDao dao;
	@Mock
	private SPCService spcService;

	@Parameter
    public List<Filme> filmes;

	@Parameter(value = 1)
    public Double valorDaLocacao;

	@Parameter(value = 2)
    public String cenario;

    @Before
	public void inicarIntanciaLocacaoService(){
		MockitoAnnotations.initMocks(this);
	}

	private static Filme filme1 = FilmeBuilder.umFilme().instacia();
	private static Filme filme2 = FilmeBuilder.umFilme().instacia();
	private static Filme filme3 = FilmeBuilder.umFilme().instacia();
	private static Filme filme4 = FilmeBuilder.umFilme().instacia();
	private static Filme filme5 = FilmeBuilder.umFilme().instacia();
	private static Filme filme6 = FilmeBuilder.umFilme().instacia();

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
	public void deveCalcularValorDaLocacaoDeAcordoComQuantidadeDeFilmes() throws Exception{

		//Cenário
		Usuario usuario = UsuarioBuilder.umUsuario().instancia();
		
		//Ação
		Locacao alugarFilmes = service.alugarFilmes(usuario, filmes);

		//Verificação
		// 4 + 4 + 3 (25%) =  11
		Assert.assertThat(alugarFilmes.getValor(), CoreMatchers.is(valorDaLocacao));
	}
    
}
