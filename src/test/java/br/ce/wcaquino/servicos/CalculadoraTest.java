package br.ce.wcaquino.servicos;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import br.ce.wcaquino.runners.ParaleloRunner;

@RunWith(ParaleloRunner.class)
public class CalculadoraTest {

    @Mock
    private Calculadora calc;

    @Spy
    private Calculadora calcSpy;

    // @Spy só funciona com a imprementação
    // utilizando com interface dá erro

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        System.out.println("Iniciando ....");
    }

    @After
    public void shundonw() {
        System.out.println("Finalizando ... ");
    }

    @Test
    public void calcularSomaDeDoisNumeros(){

        //Cenário
        int a = 5;
        int b = 5;
        Calculadora calc = new Calculadora();

        //Ação
        int resultado = calc.somar(a, b);

        //Verificação
        Assert.assertEquals(10, resultado);
    }

    @Test
    public void calcularSubtracaoDeDoisNumeros(){

        //Cenário
        int a = 5;
        int b = 5;  
        Calculadora calc = new Calculadora();

        //Ação
        int resultado = calc.subtrair(a, b);

        //Verificação
        Assert.assertEquals(0, resultado);
    }


    @Test
    public void mostraDiferenteEntreMockE_Spy() {

        //Cenário
        Mockito.when(calc.somar(1, 2)).thenReturn(7);
        //Mockito.when(calcSpy.somar(1, 2)).thenReturn(7);
        Mockito.doReturn(7).when(calcSpy).somar(1, 2);

        // Mockito.when(calc.somar(1, 2)).thenCallRealMethod(); -- Aqui não ficou igual da aula, deu erro
        Mockito.doNothing().when(calcSpy).imprimi(); // Não exibir o métodod imprimi

        System.out.println("Mock: " + calc.somar(1, 2));
        System.out.println("Spy: " + calcSpy.somar(1, 2));

        System.out.println("Mock");
        calc.imprimi();
        System.out.println("Spy");
        calcSpy.imprimi();
        
    }
    
}
