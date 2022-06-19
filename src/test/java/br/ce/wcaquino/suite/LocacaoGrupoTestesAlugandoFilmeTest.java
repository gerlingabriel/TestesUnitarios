package br.ce.wcaquino.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import br.ce.wcaquino.servicos.CalculadoraTest;
import br.ce.wcaquino.servicos.CalcularValoresDasLocacoesTest;
import br.ce.wcaquino.servicos.LocacaoServiceTest;

@RunWith(Suite.class)
@SuiteClasses({
    CalculadoraTest.class,
    CalcularValoresDasLocacoesTest.class,
    LocacaoServiceTest.class
})
public class LocacaoGrupoTestesAlugandoFilmeTest {
    
}
