package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.exceptions.NoVectoresException
import ar.edu.unq.eperdemic.exceptions.NombreInvalidoException
import ar.edu.unq.eperdemic.helper.service.DataService
import ar.edu.unq.eperdemic.modelo.Distrito
import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.utils.Randomizador
import ar.edu.unq.eperdemic.modelo.vectores.Animal
import ar.edu.unq.eperdemic.modelo.vectores.Humano
import ar.edu.unq.eperdemic.modelo.vectores.Insecto
import ar.edu.unq.eperdemic.modelo.vectores.Vector
import ar.edu.unq.eperdemic.services.impl.EstadisticaServiceImpl
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
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
class EstadisticaServiceImplTest {

    @Autowired lateinit var estadisticaService: EstadisticaServiceImpl
    @Autowired lateinit var patogenoService: PatogenoService
    @Autowired lateinit var vectorService: VectorService
    @Autowired lateinit var ubicacionService: UbicacionService
    @Autowired lateinit var dataService: DataService
    @Autowired lateinit var distritoService: DistritoService

    lateinit var distrito: Distrito
    lateinit var patogeno: Patogeno
    lateinit var quilmes: Ubicacion
    lateinit var humano: Vector
    lateinit var insecto: Vector
    lateinit var gripe: Especie;

    @BeforeEach
    fun setup() {
        Randomizador.cambiarAmbiente("Test")

        patogeno = Patogeno(
            50,
            50,
            50,
            10,
            50,
            "virus"
        )
        patogenoService.crearPatogeno(patogeno)

        val coordenadasDistrito = listOf(
            Pair(1.0, 1.0),
            Pair(2.0, 2.0),
            Pair(4.0, 4.0),
            Pair(5.0, 5.0),
            Pair(6.0, 6.0),
            Pair(2.0, 0.0),
            Pair(1.0, 1.0)
        )
        distrito = Distrito(
            "Quilmes",
            GeoJsonPolygon(coordenadasDistrito.map { it -> Point(it.first, it.second) })
        )
        distrito = distritoService.crear(distrito)


        quilmes = Ubicacion("quilmes", Pair(1.0,1.0))
        ubicacionService.crearUbicacion(quilmes)

        humano = Humano(quilmes)
        insecto = Insecto(quilmes)
        vectorService.crearVector(humano)
        vectorService.crearVector(insecto)

        gripe = patogenoService.agregarEspecie(patogeno.id!!, "gripe", "Arg", quilmes.id!!)
        humano = vectorService.recuperarVector(humano.id!!)
        insecto = vectorService.recuperarVector(insecto.id!!)

    }

    @Test
    fun especieLiderTest() {
        val viruela = patogenoService.agregarEspecie(patogeno.id!!, "viruela", "Arg", quilmes.id!!)

        val humano2: Vector = vectorService.crearVector(Humano(quilmes))
        val humano3: Vector = vectorService.crearVector(Humano(quilmes))
        val animal: Vector = vectorService.crearVector(Animal(quilmes))

        vectorService.infectar(humano2.id!!, viruela.id!!)
        vectorService.infectar(animal.id!!, gripe.id!!)
        vectorService.infectar(insecto.id!!, gripe.id!!)

        assertEquals(viruela, estadisticaService.especieLider())
    }

    @Test
    fun reporteDeContagiosTest() {

        val reporteDeContagios = estadisticaService.reporteDeContagios("quilmes")
        assertEquals(2, reporteDeContagios.vectoresPresentes)
        assertEquals(1, reporteDeContagios.vectoresInfectados)
        assertEquals(gripe, reporteDeContagios.especieDominante)
    }

    @Test
    fun reporteDeContagiosUbicacionInexistenteTest() {

        val exception = assertThrows(NombreInvalidoException::class.java) {
            estadisticaService.reporteDeContagios("ezpeleta")
        }
        assertEquals("No se encontró ninguna ubicación con el nombre 'ezpeleta'.", exception.message)
    }

    @Test
    fun reporteDeContagiosUbicacionSinVectoresTest() {
        ubicacionService.crearUbicacion(Ubicacion("berazategui", Pair(6.0,6.0)))
        val exception = assertThrows(NoVectoresException::class.java) {
            estadisticaService.reporteDeContagios("berazategui")
        }
        assertEquals("No existen vectores en la ubicación con el nombre 'berazategui'.", exception.message)
    }


    @Test
    fun lideresTest() {
        var bernal = Ubicacion("bernal", Pair(5.0,5.0))
        ubicacionService.crearUbicacion(bernal)

        var insecto2: Vector = Insecto(bernal)
        var humano2: Vector = Humano(bernal)
        var animal: Vector = Animal(bernal)
        vectorService.crearVector(insecto2)
        vectorService.crearVector(humano2)
        vectorService.crearVector(animal)


        bernal = ubicacionService.recuperarUbicacion(bernal.id!!)

        var gripeA = patogenoService.agregarEspecie(patogeno.id!!, "gripeA", "arg", bernal.id!!)
        val viruela = patogenoService.agregarEspecie(patogeno.id!!, "viruela", "Arg", quilmes.id!!)

        insecto2 = vectorService.recuperarVector(insecto2.id!!)
        humano2 = vectorService.recuperarVector(humano2.id!!)
        animal = vectorService.recuperarVector(animal.id!!)
        humano = vectorService.recuperarVector(humano.id!!)


        assertEquals(listOf(gripe, viruela), estadisticaService.lideres())
    }

    @AfterEach
    fun cleanup() {
        dataService.cleanAll()
        ubicacionService.deleteAll()
    }


}