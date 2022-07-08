package br.ce.wcaquino.servicos;

import static org.junit.Assert.assertThat;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import br.ce.wcaquino.builders.FilmeBuilder;
import br.ce.wcaquino.builders.UsuarioBuilder;
import br.ce.wcaquino.dao.LocacaoDao;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.matchers.MatchersProprios;
import br.ce.wcaquino.runners.ParaleloRunner;
import br.ce.wcaquino.utils.DataUtils;

@RunWith(ParaleloRunner.class)
public class LocacaoServiceSemPoweMockTest {

	@InjectMocks
	@Spy
	private LocacaoService service;

	@Mock
	private LocacaoDao dao;
	@Mock
	private SPCService spcService;
	@Mock
	private EmailService emailService;

	@Rule
	public ErrorCollector error = new ErrorCollector();

	@Before
	public void inicarIntanciaLocacaoService(){
		MockitoAnnotations.initMocks(this);
		System.out.println("Iniciando 2... ");
	}

	@After
    public void shundonw() {
        System.out.println("Finalizando 2... ");
    }

    @Test
    public void deveAlugarFilme() throws Exception {
		//Cenário
		Usuario usuario = UsuarioBuilder.umUsuario().instancia();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().alterandoValorLocacao(10.50).instacia() );

		Mockito.doReturn(DataUtils.obterData(1, 7, 2022)).when(service).buscarDataDeHoje();

		//Ação
		Locacao alugarFilme = service.alugarFilmes(usuario, filmes);

		//verificar
		error.checkThat(alugarFilme.getValor(), CoreMatchers.is(10.50));

		error.checkThat(DataUtils.isMesmaData(alugarFilme.getDataLocacao(), 
			DataUtils.obterData(1, 7, 2022)),  CoreMatchers.is(true) );

		error.checkThat(DataUtils.isMesmaData(alugarFilme.getDataRetorno(), 
			DataUtils.obterData(2,  7, 2022)), CoreMatchers.is(true) );
    }

	@Test
	public void deveDevolverNaSegundaQuandoAlugarSabado() throws Exception{
		// Cenário
		Usuario usuario = UsuarioBuilder.umUsuario().instancia();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().instacia() );

		Mockito.doReturn(DataUtils.obterData(2, 7, 2022)).when(service).buscarDataDeHoje();

		//Ação
		Locacao alugarFilme = service.alugarFilmes(usuario, filmes);

		//Verificação
		assertThat(alugarFilme.getDataRetorno(), MatchersProprios.caiNumaSegunda());
		
	}
    
	@Test
	public void deveCalcularValorLocacao() throws Exception {

		//Cenário
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().instacia() );

		//Ação 
		//Mesmo tirando PowerMockito ainda funcionou essa chama
		//Double valor = Whitebox.invokeMethod(service, "valorDaLocacao", filmes);

		Class<LocacaoService> clazz = LocacaoService.class;
		Method metodo = clazz.getDeclaredMethod("valorDaLocacao", List.class);
		metodo.setAccessible(true);
		Double valor = (Double) metodo.invoke(service, filmes);

		//Verificação
		Assert.assertThat(valor, CoreMatchers.is(4.0));		
	}
}
