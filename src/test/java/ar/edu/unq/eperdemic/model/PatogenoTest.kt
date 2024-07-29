package ar.edu.unq.eperdemic.model

import ar.edu.unq.eperdemic.exceptions.TipoDeVectorDesconocido
import ar.edu.unq.eperdemic.exceptions.ValorDeCapacidadNoValido
import ar.edu.unq.eperdemic.modelo.Patogeno
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PatogenoTest {

    lateinit var patogeno: Patogeno;

    @BeforeEach
    fun setup() {
        patogeno = Patogeno(
            40,
            50,
            60,
            10,
            50,
            "virus"
        )
    }

    @Test
    fun constructorTest() {
        assertEquals(40, patogeno.capacidadDeContagioHumanos)
        assertEquals(50, patogeno.capacidadDeContagioAnimales)
        assertEquals(60, patogeno.capacidadDeContagioInsectos)
        assertEquals(10, patogeno.defensaMicroorganismos)
        assertEquals(50, patogeno.capacidadDeBiomecanizacion)
        assertEquals("virus", patogeno.tipo)
        assertEquals("virus", patogeno.toString())
    }

    @Test
    fun patogenoConValoresInvalidosTest() {
        val exception = Assertions.assertThrows(ValorDeCapacidadNoValido::class.java) {
            patogeno = Patogeno(
                -40,
                50,
                60,
                10,
                50,
                "virus"
            )
        }
        assertEquals("Error: el porcentaje ingresado no es valido",exception.message)
    }

    @Test
    fun crearEspecieTest() {
        val especie = patogeno.crearEspecie("gripe", "argentina")
        assertEquals("gripe", especie.nombre)
        assertEquals("argentina", especie.paisDeOrigen)
        assertEquals(0, especie.cantidadDeInfectados)
        assertTrue(patogeno.equals(especie.patogeno))
        assertEquals(patogeno.hashCode(),especie.patogeno.hashCode())
    }

    @Test
    fun patogenoEqualsNullFALSE(){
        assertFalse(patogeno.equals(null))
    }
    @Test
    fun patogenoEqualsDistintaClaseFALSE(){
        assertFalse(patogeno.equals("patogeno"))
    }

    @Test
    fun patogenoEqualsDistintoIDFALSE(){
        patogeno.id=1
        val patogeno2 = Patogeno(
            40,
            50,
            60,
            10,
            50,
            "virus"
        )
        patogeno2.id = 2
        assertFalse(patogeno.equals(patogeno2))
    }

    @Test
    fun getCapacidadContagioHumanoTest() {

        assertEquals(80, patogeno.getCapacidadContagio("Humano"))
    }

    @Test
    fun getCapacidadContagioAnimalTest() {

        assertEquals(90, patogeno.getCapacidadContagio("Animal"))
    }

    @Test
    fun getCapacidadContagioInsectoTest() {

        assertEquals(100, patogeno.getCapacidadContagio("Insecto"))
    }



    @Test
    fun getCapacidadContagioExceptionTest() {
        val exception = assertThrows(
            TipoDeVectorDesconocido::class.java
        ) { patogeno.getCapacidadContagio("aaa") }

        assertEquals("Error: tipo de vector desconocido", exception.message)
    }

}