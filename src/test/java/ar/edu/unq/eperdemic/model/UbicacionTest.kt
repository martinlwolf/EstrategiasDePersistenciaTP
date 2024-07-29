package ar.edu.unq.eperdemic.model

import ar.edu.unq.eperdemic.modelo.Ubicacion
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UbicacionTest {

    lateinit var quilmes: Ubicacion

    @BeforeEach
    fun setup() {
        quilmes = Ubicacion("Quilmes", Pair(1.0,1.0))
    }

    @Test
    fun testConstructorDeUbicacion() {
        Assertions.assertEquals(null, quilmes.id)
        Assertions.assertEquals("quilmes", quilmes.nombre)
    }

    @Test
    fun testEqualsDeUbicacion() {
        val bernal = Ubicacion("Bernal", Pair(10.0,10.0))

        quilmes.id = 1
        bernal.id = 2

        Assertions.assertFalse(quilmes.equals(null))
        Assertions.assertFalse(quilmes.equals(bernal))
        Assertions.assertTrue(quilmes.equals(quilmes))
    }
}