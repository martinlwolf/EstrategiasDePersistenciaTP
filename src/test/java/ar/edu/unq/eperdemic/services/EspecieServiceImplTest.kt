package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.exceptions.EntidadNoPersistidaException
import ar.edu.unq.eperdemic.exceptions.IdInvalidoExcepcion
import ar.edu.unq.eperdemic.helper.service.DataService
import ar.edu.unq.eperdemic.modelo.Distrito
import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.utils.Randomizador
import ar.edu.unq.eperdemic.modelo.vectores.Humano
import ar.edu.unq.eperdemic.services.impl.EspecieServiceImpl
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.geo.Point
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EspecieServiceImplTest {

    @Autowired lateinit var ubicacionService: UbicacionService
    @Autowired lateinit var especieService: EspecieServiceImpl
    @Autowired lateinit var patogenoService: PatogenoService
    @Autowired lateinit var vectorService: VectorService
    @Autowired lateinit var dataService: DataService
    @Autowired lateinit var distritoService: DistritoService

    lateinit var distrito: Distrito
    lateinit var virus: Patogeno
    lateinit var quilmes: Ubicacion

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

        quilmes = ubicacionService.crearUbicacion(Ubicacion("quilmes", Pair(1.0,1.0)))
        virus = Patogeno(
            1,
            2,
            3,
            4,
            5,
            "virus"
        )
        patogenoService.crearPatogeno(virus)
        vectorService.crearVector(Humano(quilmes))
    }

    @Test
    fun testRecuperarEspecie(){
        var idEspecieNueva = patogenoService.agregarEspecie(virus.id!!, "gripe", "arg", quilmes.id!!).id!!
        var especieNueva = especieService.recuperarEspecie(idEspecieNueva)
        assertEquals(virus.tipo, especieNueva.patogeno.tipo)
        assertEquals("gripe", especieNueva.nombre)
        assertEquals("arg", especieNueva.paisDeOrigen)
    }

    @Test
    fun testRecuperarEspecieQueNoEsta(){
        val exception = Assertions.assertThrows(IdInvalidoExcepcion::class.java) {
            especieService.recuperarEspecie(99999)
        }
        assertEquals("Especie con id: 99999 no existe",exception.message)
    }

    @Test
    fun testActualizarEspecie(){
        //Se chequea que esten los primeros datos de la especie
        var idEspecieNueva = patogenoService.agregarEspecie(virus.id!!, "gripe", "arg", quilmes.id!!).id!!
        var especieNueva = especieService.recuperarEspecie(idEspecieNueva)
        assertEquals(virus.tipo, especieNueva.patogeno.tipo)
        assertEquals("gripe", especieNueva.nombre)
        assertEquals("arg", especieNueva.paisDeOrigen)
        //Se cambian los atributos y se actualiza
        especieNueva.paisDeOrigen = "bra"
        especieNueva.nombre = "varicela"
        especieService.actualizarEspecie(especieNueva)
        //Se chequean los cambios
        assertEquals(virus.tipo, especieNueva.patogeno.tipo)
        assertEquals("varicela", especieNueva.nombre)
        assertEquals("bra", especieNueva.paisDeOrigen)
    }

    @Test
    fun testActualizarEspecieInexistente(){
        // este test ahora rompe porque actualizar utiliza save ahora
        val exception = Assertions.assertThrows(EntidadNoPersistidaException::class.java) {
            especieService.actualizarEspecie(Especie(virus,"a","b"))
        }
        Assertions.assertEquals("Especie no esta persistido", exception.message)
    }

    @Test
    fun testRecuperarTodos(){
        patogenoService.agregarEspecie(virus.id!!, "sarampion", "usa", quilmes.id!!)
        patogenoService.agregarEspecie(virus.id!!, "neumonia", "chi", quilmes.id!!)

        assertEquals(2, especieService.recuperarATodasLasEspecies().size)
    }

    @Test
    fun testCantidadDeInfectados(){
        var idEspecieNueva = patogenoService.agregarEspecie(virus.id!!, "gripe", "arg", quilmes.id!!).id!!
        assertEquals(1,especieService.cantidadDeInfectados(idEspecieNueva))
    }



    @AfterEach
    fun cleanup() {
        dataService.cleanAll()
        ubicacionService.deleteAll()
    }
}