package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.exceptions.CoordenadasNoValidasExcepcion
import ar.edu.unq.eperdemic.exceptions.NoDistritosException
import ar.edu.unq.eperdemic.exceptions.NoEnfermosException
import ar.edu.unq.eperdemic.exceptions.NombreDuplicadoExcepcion
import ar.edu.unq.eperdemic.helper.service.DataService
import ar.edu.unq.eperdemic.modelo.Distrito
import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.utils.Randomizador
import ar.edu.unq.eperdemic.modelo.vectores.Humano
import ar.edu.unq.eperdemic.modelo.vectores.Vector
import ar.edu.unq.eperdemic.persistencia.dao.DistritoDAO
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionMongoDAO
import ar.edu.unq.eperdemic.services.impl.DistritoServiceImpl
import ar.edu.unq.eperdemic.services.impl.UbicacionServiceImpl
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
class DistritoServiceImplTest {

    @Autowired
    private lateinit var distritoDAO: DistritoDAO


    @Autowired
    lateinit var ubicacionService: UbicacionServiceImpl

    @Autowired
    lateinit var patogenoService: PatogenoService

    @Autowired
    lateinit var vectorService: VectorService

    @Autowired lateinit var distritoService: DistritoServiceImpl

    @Autowired
    lateinit var dataService: DataService

    lateinit var quilmes: Ubicacion
    lateinit var avellaneda: Ubicacion
    lateinit var bernal: Ubicacion
    lateinit var ezpeleta: Ubicacion

    lateinit var humano: Vector
    lateinit var humano2: Vector

    lateinit var virus: Patogeno
    lateinit var gripe: Especie

    lateinit var distritoQuilmes: Distrito
    lateinit var distritoAvellaneda: Distrito
    lateinit var coordenadasDistritoQuilmes: List<Pair<Double, Double>>

    @BeforeEach
    fun setup() {
        Randomizador.cambiarAmbiente("Test")

        coordenadasDistritoQuilmes = listOf(Pair(1.0, 1.0), Pair(2.0, 2.0), Pair(2.0, 0.0), Pair(1.0, 1.0))
        distritoQuilmes = Distrito(
            "Quilmes",
            GeoJsonPolygon(this.coordenadasDistritoQuilmes.map { it -> Point(it.first, it.second) })
        )
        distritoAvellaneda = Distrito(
            "avellaneda",
            GeoJsonPolygon(listOf(Point(3.0, 3.0), Point(4.0, 4.0), Point(4.0, 0.0), Point(3.0, 3.0)))
        )

        distritoQuilmes = distritoService.crear(distritoQuilmes)
        distritoAvellaneda = distritoService.crear(distritoAvellaneda)

        quilmes = ubicacionService.crearUbicacion(Ubicacion("Quilmes", coordenadasDistritoQuilmes[0]))
        avellaneda = ubicacionService.crearUbicacion(Ubicacion("Avellaneda", Pair(4.0,4.0)))
        bernal = ubicacionService.crearUbicacion(Ubicacion("Bernal", coordenadasDistritoQuilmes[1]))
        ezpeleta = ubicacionService.crearUbicacion(Ubicacion("Ezpeleta", coordenadasDistritoQuilmes[2]))

        humano = Humano(quilmes)
        humano2 = Humano(bernal)

        humano = vectorService.crearVector(humano)
        humano2 = vectorService.crearVector(humano2)

        this.virus = patogenoService.crearPatogeno(
            Patogeno(
                95,
                95,
                95,
                10,
                10,
                "Virus"
            )
        )
    }

    @Test
    fun testNoSePuedeCrearUnDistritoQueIntersecteAOtro() {

        val exception = assertThrows(CoordenadasNoValidasExcepcion::class.java) {
            distritoService.crear(
                Distrito(
                    "quilmesoeste",
                    GeoJsonPolygon(listOf(Point(1.0, 1.0), Point(4.0, 4.0), Point(4.0, 0.0), Point(1.0, 1.0)))
                )
            )
        }

        assertEquals("Las coordenadas proporcionadas intersectan a un distrito existente", exception.message)
    }

    @Test
    fun testNoSePuedeCrearUnDistritoConUnNombreYaExistente() {

        val exception = assertThrows(NombreDuplicadoExcepcion::class.java) {
            distritoService.crear(
                Distrito(
                    "Quilmes",
                    GeoJsonPolygon(listOf(Point(1.0, 1.0), Point(4.0, 4.0), Point(4.0, 0.0), Point(1.0, 1.0)))
                )
            )
        }

        assertEquals("ya existe un distrito con el nombre quilmes", exception.message)
    }

    @Test
    fun testDistritoMasEnfermo() {
        gripe = patogenoService.agregarEspecie(virus.id!!, "gripe", "arg", bernal.id!!)
        vectorService.infectar(humano2.id!!, gripe.id!!)

        val humano3= vectorService.crearVector(Humano(avellaneda))
        vectorService.infectar(humano3.id!!, gripe.id!!)

        assertEquals(distritoQuilmes, distritoService.distritoMasEnfermo())
    }

    @Test
    fun testDistritoMasEnfermoSinEnfermos() {
        distritoService.deleteAll()

        distritoQuilmes = distritoService.crear(distritoQuilmes)

        val exception = assertThrows(NoEnfermosException::class.java) {
            distritoService.distritoMasEnfermo()
        }

        assertEquals("No existen enfermos en los distritos", exception.message)
    }

    @Test
    fun testDistritoMasEnfermoSinUbicaciones() {

        distritoService.deleteAll()

        distritoQuilmes = distritoService.crear(distritoQuilmes)

        val exception = assertThrows(NoEnfermosException::class.java) {
            distritoService.distritoMasEnfermo()
        }

        assertEquals("No existen enfermos en los distritos", exception.message)

    }

    @Test
    fun testFindDistritoPorCoordenadas() {

        assertEquals(
            distritoQuilmes,
            distritoDAO.findDistritoPorCoordenadas(
                quilmes.coordenada!!.first,
                quilmes.coordenada!!.second
            )!!
        )
    }

    @AfterEach
    fun cleanup() {
        dataService.cleanAll()
        ubicacionService.deleteAll()
        distritoService.deleteAll()
    }
}