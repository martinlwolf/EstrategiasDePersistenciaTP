package ar.edu.unq.eperdemic.model

import ar.edu.unq.eperdemic.exceptions.CampoInvalidoExcepcion
import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class EspecieTest {

    lateinit var especie: Especie

    @BeforeEach
    fun setup() {
        especie = Especie(
            Patogeno(
                40,
                50,
                60,
                10,
                50,
                "virus"
            )
            ,"gripe", "arg"
        )
    }

    @Test
    fun testConstructorSinNombre(){
        val exception = Assertions.assertThrows(
            CampoInvalidoExcepcion::class.java
        ) { Especie(
            Patogeno(
                40,
                50,
                60,
                10,
                50,
                "virus"
            )
            ,"", "arg"
        )}

        Assertions.assertEquals("El nombre de la especie no puede ser vacio", exception.message)
    }

    @Test
    fun testConstructorSinPais(){
        val exception = Assertions.assertThrows(
            CampoInvalidoExcepcion::class.java
        ) { Especie(
            Patogeno(
                40,
                50,
                60,
                10,
                50,
                "virus"
            )
            ,"gripe", ""
        )}

        Assertions.assertEquals("El pais de origen no puede ser vacio", exception.message)
    }

    @Test
    fun testGetCapacidadContagio(){
        Assertions.assertEquals((40+50-10),especie.getCapacidadContagio("Humano"))
        Assertions.assertEquals((50+50-10),especie.getCapacidadContagio("Animal"))
        Assertions.assertEquals((60+50-10),especie.getCapacidadContagio("Insecto"))
    }
}