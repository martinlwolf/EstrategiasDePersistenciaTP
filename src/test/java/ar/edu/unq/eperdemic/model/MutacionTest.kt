package ar.edu.unq.eperdemic.model

import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.mutaciones.BioalteracionGenetica
import ar.edu.unq.eperdemic.modelo.mutaciones.Mutacion
import ar.edu.unq.eperdemic.modelo.mutaciones.SupresionBiomecanica
import ar.edu.unq.eperdemic.modelo.vectores.Humano
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MutacionTest {
    lateinit var supresionBiomecanica: Mutacion
    lateinit var bioalteracion: Mutacion
    lateinit var humano: Humano
    lateinit var quilmes: Ubicacion


    @BeforeEach
    fun setUp() {
        quilmes = Ubicacion("quilmes", Pair(1.0,1.0))
        humano = Humano(quilmes)

        supresionBiomecanica = SupresionBiomecanica(30)
        bioalteracion = BioalteracionGenetica(humano.getTipoVector())
    }

    @Test
    fun testEsComodinParaConSupresionSiempreFalso(){
        Assertions.assertFalse(supresionBiomecanica.esComodinPara(humano.getTipoVector()))
    }

    @Test
    fun testEsComodinParaConBioalteracionCuandoEsVerdadero(){
        Assertions.assertTrue(bioalteracion.esComodinPara(humano.getTipoVector()))
    }

    @Test
    fun testEsComodinParaConBioalteracionCuandoEsFalso(){
        Assertions.assertFalse(bioalteracion.esComodinPara("Animal"))
    }

    @Test
    fun testEsMasFuerteQueConBioalteracionSiempreFalso(){
        Assertions.assertFalse(bioalteracion.esMasFuerteQue(10))
    }

    @Test
    fun testEsMasFuerteQueConSupresionEsVerdadero(){
        Assertions.assertTrue(supresionBiomecanica.esMasFuerteQue(10))
    }

    @Test
    fun testEsMasFuerteQueConSupresionEsFalso(){
        Assertions.assertFalse(supresionBiomecanica.esMasFuerteQue(50))
    }

    @Test
    fun testGetTipoMutacion(){
        Assertions.assertEquals("SupresionBiomecanica", supresionBiomecanica.getTipoMutacion())
    }

}