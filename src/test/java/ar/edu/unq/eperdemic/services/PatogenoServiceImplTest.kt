package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.exceptions.CampoInvalidoExcepcion
import ar.edu.unq.eperdemic.exceptions.EntidadNoPersistidaException
import ar.edu.unq.eperdemic.exceptions.IdInvalidoExcepcion
import ar.edu.unq.eperdemic.exceptions.NoVectoresException
import ar.edu.unq.eperdemic.helper.service.DataService
import ar.edu.unq.eperdemic.modelo.Distrito
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.utils.Randomizador
import ar.edu.unq.eperdemic.modelo.vectores.Humano
import ar.edu.unq.eperdemic.services.impl.PatogenoServiceImpl
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.geo.Point
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PatogenoServiceImplTest {

    @Autowired lateinit var patogenoService: PatogenoServiceImpl
    @Autowired lateinit var ubicacionService: UbicacionService
    @Autowired lateinit var vectorService: VectorService
    @Autowired lateinit var dataService: DataService
    @Autowired lateinit var distritoService: DistritoService

    lateinit var distrito: Distrito
    lateinit var virus: Patogeno
    lateinit var bacteria: Patogeno
    lateinit var quilmes: Ubicacion
    lateinit var bernal: Ubicacion
    lateinit var virusCreado: Patogeno

    @BeforeEach
    fun setup() {
        Randomizador.cambiarAmbiente("Test")

        val coordenadasDistrito = listOf(
            Pair(1.0, 1.0),
            Pair(2.0, 2.0),
            Pair(4.0, 4.0),
            Pair(5.0, 5.0),
            Pair(6.0, 6.0),
            Pair(10.0, 10.0),
            Pair(1.0, 1.0)
        )
        distrito = Distrito(
            "Quilmes",
            GeoJsonPolygon(coordenadasDistrito.map { it -> Point(it.first, it.second) })
        )
        distrito = distritoService.crear(distrito)

        virus = Patogeno(
            40,
            50,
            50,
            10,
            50,
            "virus"
        )
        bacteria = Patogeno(
            40,
            50,
            50,
            10,
            50,
            "bacteria"
        )


        virusCreado = patogenoService.crearPatogeno(virus)
        patogenoService.crearPatogeno(bacteria)

        quilmes = ubicacionService.crearUbicacion(Ubicacion("quilmes", Pair(1.0,1.0)))
        bernal = ubicacionService.crearUbicacion(Ubicacion("bernal", Pair(10.0,10.0)))
        vectorService.crearVector(Humano(bernal))
        bernal = ubicacionService.recuperarUbicacion(bernal.id!!)
    }

    @Test
    fun testCrearPatogeno() {

        assertNotNull(virus.id!!)

        assertEquals(virus, virusCreado)
    }

    @Test
    fun testRecuperarPatogeno() {
        val patogenoRecuperado = patogenoService.recuperarPatogeno(virus.id!!)
        assertEquals(virus, patogenoRecuperado)
    }

    @Test
    fun testRecuperarATodosLosPatogenos() {

        val patogenos = patogenoService.recuperarATodosLosPatogenos()

        assertEquals(2, patogenos.size)
        assertEquals(virus, patogenos[0])
        assertEquals(bacteria, patogenos[1])
    }


    @Test
    fun testRecPatogenosEmpty() {
        cleanup()

        val patogenos = patogenoService.recuperarATodosLosPatogenos()

        assertTrue(patogenos.isEmpty())
    }

    @Test
    fun testActualizar() {

        assertEquals(50, virus.capacidadDeBiomecanizacion)
        virus.capacidadDeBiomecanizacion = 100
        patogenoService.actualizar(virus)

        assertEquals(100, virus.capacidadDeBiomecanizacion)
    }

    @Test
    fun testActualizarPatogenoNoPersistido() {
        val patogeno = Patogeno(
            40,
            50,
            50,
            10,
            50,
            "patogeno"
        )
        val exception = assertThrows(EntidadNoPersistidaException::class.java) {
            patogenoService.actualizar(patogeno)
        }
        assertEquals("Patogeno no esta persistido", exception.message)
    }


    @Test
    fun testAgregarEspecieDePatogenoInexistente() {
        val exception = assertThrows(IdInvalidoExcepcion::class.java) {
            patogenoService.agregarEspecie(1000000000, "patogeno", "argentina", bernal.id!!)
        }
        assertEquals("Patogeno con id: 1000000000 no existe", exception.message)
    }

    @Test
    fun testAgregarEspecieSinNombre() {
        val exception = assertThrows(CampoInvalidoExcepcion::class.java) {
            patogenoService.agregarEspecie(virus.id!!, "", "argentina", bernal.id!!)
        }
        assertEquals("El nombre de la especie no puede ser vacio", exception.message)
    }

    @Test
    fun testAgregarEspecieSinPaisDeOrigen() {
        val exception = assertThrows(CampoInvalidoExcepcion::class.java) {
            patogenoService.agregarEspecie(virus.id!!, "gripe", "", bernal.id!!)
        }
        assertEquals("El pais de origen no puede ser vacio", exception.message)
    }

    @Test
    fun testAgregarEspecieSinVectores() {

        val exception = assertThrows(NoVectoresException::class.java) {
            patogenoService.agregarEspecie(virus.id!!, "gripe", "argentina", quilmes.id!!)
        }
        assertEquals("no existen vectores en la ubicacion dada", exception.message)


    }

    @Test
    fun testAgregarEspecie() {


        assertTrue(ubicacionService.vectoresInfectados("bernal").isEmpty())

        val especie = patogenoService.agregarEspecie(virus.id!!, "gripe", "argentina", bernal.id!!)
        val virusRecuperado = patogenoService.recuperarPatogeno(virus.id!!)

        assertEquals(1, virusRecuperado?.especies?.size)
        assertEquals(especie, virusRecuperado?.especies?.elementAt(0))
        assertFalse(ubicacionService.vectoresInfectados("bernal").isEmpty())

    }



    @Test
    fun testEspeciesDePatogenoVACIA() {
        val especies = patogenoService.especiesDePatogeno(virus.id!!)
        assertTrue(especies.isEmpty())
    }

    @Test
    fun testEspeciesDePatogeno() {
        patogenoService.agregarEspecie(virus.id!!, "gripe", "argentina", bernal.id!!)
        val especies = patogenoService.especiesDePatogeno(virus.id!!)
        assertFalse(especies.isEmpty())
        assertEquals("gripe", especies[0].nombre)
    }


    @Test
    fun testEsPandemiaFALSE() {
        val especie = patogenoService.agregarEspecie(virus.id!!, "gripe", "argentina", bernal.id!!)
        assertFalse(patogenoService.esPandemia(especie.id!!))
    }

    @Test
    fun testEsPandemiaSinEspecie() {
        val exception = assertThrows(IdInvalidoExcepcion::class.java) {
            patogenoService.esPandemia(1)
        }
        assertEquals("Especie con id: 1 no existe",exception.message)
    }

    @Test
    fun testEsPandemia() {
        ubicacionService.eliminarUbicacion(quilmes.id!!)
        val especie = patogenoService.agregarEspecie(virus.id!!, "gripe", "argentina", bernal.id!!)
        assertTrue(patogenoService.esPandemia(especie.id!!))
    }

    @AfterEach
    fun cleanup() {
        dataService.cleanAll()
        ubicacionService.deleteAll()
    }


}



