package br.ce.wcaquino.servicos;

import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import br.ce.wcaquino.builders.FilmeBuilder;
import br.ce.wcaquino.builders.LocacaoBuilder;
import br.ce.wcaquino.builders.UsuarioBuilder;
import br.ce.wcaquino.dao.LocacaoDao;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exception.FilmeSemEstoqueException;
import br.ce.wcaquino.exception.LocacaoException;
import br.ce.wcaquino.matchers.MatchersProprios;
import br.ce.wcaquino.utils.DataUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({LocacaoService.class, DataUtils.class})
@PowerMockIgnore("jdk.internal.reflect.*")
public class LocacaoServiceTest {

	@InjectMocks
	private LocacaoService service;

	@Mock
	private LocacaoDao dao;
	@Mock
	private SPCService spcService;
	@Mock
	private EmailService emailService;

	@Rule
	public ErrorCollector error = new ErrorCollector();

	//Métoddo depreciado (nome())
	// @Rule
	// public ExpectedException exp = ExpectedException.none();


	@Before
	public void inicarIntanciaLocacaoService(){
		MockitoAnnotations.initMocks(this);
		service = PowerMockito.spy(service);
	}

    @Test
    public void deveAlugarFilme() throws Exception {
		//Cenário
		Usuario usuario = UsuarioBuilder.umUsuario().instancia();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().alterandoValorLocacao(10.50).instacia() );
		Double valorEsperado = 10.5;

		// PowerMockito quando usar date para pegar data
		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(1, 07, 2022));

		//Quando instancia de tempo for Calendar
		// Calendar diaEscolhido = Calendar.getInstance();
		// diaEscolhido.set(Calendar.DAY_OF_MONTH, 1);
		// diaEscolhido.set(Calendar.MONTH, 7);
		// diaEscolhido.set(Calendar.YEAR, 2022);
		// PowerMockito.mockStatic(Calendar.class);
		// PowerMockito.when(Calendar.getInstance()).thenReturn(diaEscolhido);

		Date novaData = new Date();

		//Ação
		Locacao alugarFilme = service.alugarFilmes(usuario, filmes);

		// testes para ver se variaveis na class JAVA poderia ser vista aqui
		service.vDefauit= "";
		service.vPublica= "";
		service.vProtected="";
		//service.vPrivada=""; // Essa não é possivel enchergar

		//verificar - três formas de se realizar os testes
		Assert.assertEquals(alugarFilme.getValor(), valorEsperado, 0.01);
		error.checkThat(alugarFilme.getValor(), CoreMatchers.is(10.50));

		Assert.assertTrue(DataUtils.isMesmaData(alugarFilme.getDataLocacao(), novaData) );
		error.checkThat(DataUtils.isMesmaData(alugarFilme.getDataLocacao(), novaData),  CoreMatchers.is(true) );

		// Com PowerMockito estava dando erro as duas linhas abaixo, para isto precisou que PowerMockito também gerencia-se essa classe
		Assert.assertTrue(DataUtils.isMesmaData(alugarFilme.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)) );
		error.checkThat(DataUtils.isMesmaData(alugarFilme.getDataRetorno(), DataUtils.obterData(2, 7, 2022)), CoreMatchers.is(true) );
    }

	@Test(expected = Exception.class) // forma elegante
	public void deveLancarExcecaoAoAlugarFilmeSemEstoque() throws Exception{

		//Cenário
		Usuario usuario = UsuarioBuilder.umUsuario().instancia();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().filmeSemEstoque().instacia());
		
		//Ação
		service.alugarFilmes(usuario, filmes);
	}

	@Test
	public void deveDevolverNaSegundaQuandoAlugarSabado() throws Exception{
		// Cenário
		Usuario usuario = UsuarioBuilder.umUsuario().instancia();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().alterandoValorLocacao(10.50).instacia() );

		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(2, 07, 2022));

		// Calendar diaEscolhido = Calendar.getInstance();
		// diaEscolhido.set(Calendar.DAY_OF_MONTH, 2);
		// diaEscolhido.set(Calendar.MONTH, Calendar.JULY);
		// diaEscolhido.set(Calendar.YEAR, 2022);
		// PowerMockito.mockStatic(Calendar.class);
		// PowerMockito.when(Calendar.getInstance()).thenReturn(diaEscolhido);

		//Ação
		Locacao alugarFilme = service.alugarFilmes(usuario, filmes);

		//Verificação
		error.checkThat(alugarFilme.getDataRetorno(), MatchersProprios.caiNumaSegunda());
		
	}

	@Test() // forma Robusta
	public void deveLancarExcecaoAoUsuarioNull() throws FilmeSemEstoqueException{

		//Cenário
		Usuario usuario = null;
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().instacia() );
		String textoEsperado = "Usuario não existe!";
		
		//Ação
		try {
			service.alugarFilmes(usuario, filmes);
			Assert.fail("Erro não chamado como esperado");
		} catch (LocacaoException e) {
			Assert.assertTrue(e.getMessage().equalsIgnoreCase(textoEsperado));
		}
	}

	@Test(expected = LocacaoException.class) // forme "nova" -- exp está depreciado, então pode usar "expected = LocacaoException.class"
	public void deveLancarExcecaoQuandoFilmeVierNull() throws LocacaoException, FilmeSemEstoqueException{

		//Cenário;
		Usuario usuario = UsuarioBuilder.umUsuario().instancia();
		List<Filme> filmes = null;
		// fica dentro do cenário, pois ele irá memorizar o que deve acontecer na parte de "AÇÃO"
		// exp está depreciado
		// exp.expect(LocacaoException.class);
		// exp.expectMessage("Filme não existe!");
		
		//Ação
		service.alugarFilmes(usuario, filmes);
	}

	@Test(expected = LocacaoException.class)
	public void naoDeveAlugarFilmeParaUsuarioNegativado() throws Exception {

		//Cenário
		Usuario usuario = UsuarioBuilder.umUsuario().instancia();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().instacia() );

		Mockito.when(spcService.nomeNegativado(usuario)).thenReturn(true);

		// exp.expect(LocacaoException.class);
		// exp.expectMessage("Usuario Negativado!");

		//Ação
		service.alugarFilmes(usuario, filmes);
	}

	@Test
	public void deveEnviarEmailParaUsuariosComAtraso() {
		
		//Cenário
		Usuario usuario = UsuarioBuilder.umUsuario().instancia();
		// teste para verficar erro - |Cara caso atraso
		Usuario usuario2 = UsuarioBuilder.umUsuario().mudarNomeDoUusuario("Usuario 2").instancia();
		Usuario usuario3 = UsuarioBuilder.umUsuario().mudarNomeDoUusuario("Usuario 3").instancia();

		List<Locacao> locacoesDeUsuarios = 
			Arrays.asList(LocacaoBuilder.umaLocacao().alterarOUsuario(usuario).alterandodataDaEntraga().instancia(),
						LocacaoBuilder.umaLocacao().alterarOUsuario(usuario3).alterandodataDaEntraga().instancia(),
						LocacaoBuilder.umaLocacao().alterarOUsuario(usuario3).alterandodataDaEntraga().instancia(),
						LocacaoBuilder.umaLocacao().alterarOUsuario(usuario2).instancia());

		Mockito.when(dao.listaDeUsuarios()).thenReturn(locacoesDeUsuarios);
		
		//Ação
		service.notificarUsuarioComAtrasoFilme();

		//Verificação
		Mockito.verify(emailService).notificarUsuario(usuario);
		// Mockito.verify(emailService, Mockito.times(2)).notificarUsuario(usuario3);
		// Acima tem que colocar a quantidade exata e abaixo avisando que pelo menos uma vez
		Mockito.verify(emailService, Mockito.atLeastOnce()).notificarUsuario(usuario3);
		Mockito.verify(emailService, Mockito.never()).notificarUsuario(usuario2);

		// Modo generico - quantidade de vezes que Usuario chamou a emailService
		Mockito.verify(emailService, Mockito.times(3)).notificarUsuario(Mockito.any(Usuario.class));

		//Verifica as casos acima, caso sobre ele irá dar erro avisando que faltou usuario X
		Mockito.verifyNoMoreInteractions(emailService);

	}

	@Test(expected = LocacaoException.class)
	public void deveLancarExcecaoSeOServicoSPC() throws Exception {
		//Cenário
		Usuario usuario = UsuarioBuilder.umUsuario().instancia();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().instacia() );

		Mockito.when(spcService.nomeNegativado(usuario)).thenThrow(new Exception("Falha Sistem"));

		// exp.expect(LocacaoException.class);
		// exp.expectMessage("Problemas no sistema, favor refazer a pesquisa!");

		//Ação
		service.alugarFilmes(usuario, filmes);

		//Verificação
	}

	@Test
	public void deveProrrogarUmaLocacao() {

		//Cenário
		Locacao locacao = LocacaoBuilder.umaLocacao().instancia();

		//Ação
		service.prorrogarUmaLocacao(locacao, 3);

		//Verificação
		ArgumentCaptor<Locacao> argCapt = ArgumentCaptor.forClass(Locacao.class); // Capturar obj que iŕa capiturar 
		Mockito.verify(dao).salvar(argCapt.capture()); // Capitura o obj criado no método e compara com o que foi salvo no DAO
		Locacao novaLocacao = argCapt.getValue();

		// Assert.assertThat(novaLocacao.getValor(), CoreMatchers.is(12.0) );
		// Assert.assertThat(novaLocacao.getDataLocacao(), CoreMatchers.is(MatchersProprios.ehHoje()) );
		// Assert.assertThat(novaLocacao.getDataRetorno(), CoreMatchers.is(MatchersProprios.ehHojeComDiferencaDias(3)));

		//Essa opção pega mais de um erro ao final, o de cima para no primeiro erro
		error.checkThat(novaLocacao.getValor(), CoreMatchers.is(12.0) );
		error.checkThat(novaLocacao.getDataLocacao(), CoreMatchers.is(MatchersProprios.ehHoje()) );
		error.checkThat(novaLocacao.getDataRetorno(), CoreMatchers.is(MatchersProprios.ehHojeComDiferencaDias(3)));

	}

	@Test
	public void deveAlufarUmFilmeSemCalcularValor() throws Exception {
		//Cenário
		Usuario usuario = UsuarioBuilder.umUsuario().instancia();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().instacia() );
		Double valorEsperado = 1.0;

		PowerMockito.doReturn(1.0).when(service, "valorDaLocacao", filmes);

		//Ação
		Locacao alugarFilmes = service.alugarFilmes(usuario, filmes);

		//Verificação
		Assert.assertEquals(alugarFilmes.getValor(), valorEsperado, 0.01);
		PowerMockito.verifyPrivate(service).invoke("valorDaLocacao", filmes);
	}
    
	@Test
	public void deveCalcularValorLocacao() throws Exception {
		//Cenário
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().instacia() );
		Double valorEsperado = 4.0;

		//Ação
		Double valor = Whitebox.invokeMethod(service, "valorDaLocacao", filmes);

		//Verificação
		Assert.assertEquals(valor, valorEsperado, 0.01);	
	}
}
