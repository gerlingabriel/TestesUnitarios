package br.ce.wcaquino.servicos;

import org.junit.Assert;
import org.junit.Test;

public class CalculadoraTest {

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
    
}
