package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.exceptions.IdInvalidoExcepcion
import ar.edu.unq.eperdemic.helper.service.DataService
import ar.edu.unq.eperdemic.modelo.Distrito
import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.mutaciones.BioalteracionGenetica
import ar.edu.unq.eperdemic.modelo.mutaciones.Mutacion
import ar.edu.unq.eperdemic.modelo.mutaciones.SupresionBiomecanica
import ar.edu.unq.eperdemic.modelo.utils.Randomizador
import ar.edu.unq.eperdemic.modelo.vectores.Humano
import ar.edu.unq.eperdemic.modelo.vectores.Insecto
import ar.edu.unq.eperdemic.modelo.vectores.Vector
import ar.edu.unq.eperdemic.persistencia.dao.EspecieDAO
import ar.edu.unq.eperdemic.services.impl.MutacionServiceImpl
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.geo.Point
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MutacionServiceImplTest {

    @Autowired
    lateinit var patogenoService: PatogenoService

    @Autowired
    lateinit var especieDAO: EspecieDAO

    @Autowired
    lateinit var vectorService: VectorService

    @Autowired
    lateinit var ubicacionService: UbicacionService

    @Autowired
    lateinit var especieService: EspecieService

    @Autowired
    lateinit var dataService: DataService

    @Autowired
    lateinit var mutacionService: MutacionServiceImpl
    @Autowired lateinit var distritoService: DistritoService

    lateinit var distrito: Distrito
    lateinit var virus: Patogeno
    lateinit var bacteria: Patogeno

    lateinit var quilmes: Ubicacion
    lateinit var bernal: Ubicacion

    lateinit var humano: Vector
    lateinit var insecto: Vector
    lateinit var mosquito: Vector

    lateinit var gripe: Especie;
    lateinit var fiebre: Especie;

    lateinit var supresion: Mutacion
    lateinit var bioalteracion: Mutacion

    @BeforeEach
    fun setup() {
        Randomizador.cambiarAmbiente("Test")

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


        virus = Patogeno(
            50,
            50,
            50,
            10,
            50,
            "virus"
        )

        bacteria = Patogeno(
            50,
            50,
            50,
            10,
            50,
            "bacteria"
        )

        patogenoService.crearPatogeno(virus)
        patogenoService.crearPatogeno(bacteria)

        quilmes = Ubicacion("quilmes", Pair(1.5,1.5))
        bernal = Ubicacion("bernal", Pair(2.0, 1.5))
        ubicacionService.crearUbicacion(quilmes)
        ubicacionService.crearUbicacion(bernal)

        humano = Humano(quilmes)
        insecto = Insecto(quilmes)
        mosquito = Insecto(bernal)
        vectorService.crearVector(humano)
        vectorService.crearVector(insecto)
        vectorService.crearVector(mosquito)

        gripe = patogenoService.agregarEspecie(virus.id!!, "gripe", "Arg", quilmes.id!!)
        fiebre = patogenoService.agregarEspecie(bacteria.id!!, "fiebre", "Arg", bernal.id!!)

        humano = vectorService.recuperarVector(humano.id!!)
        insecto = vectorService.recuperarVector(insecto.id!!)
        mosquito = vectorService.recuperarVector(mosquito.id!!)

        supresion = SupresionBiomecanica(50)
        bioalteracion = BioalteracionGenetica("Insecto")

        ubicacionService.conectar(bernal.nombre,quilmes.nombre,"Aereo")
        ubicacionService.conectar(quilmes.nombre,bernal.nombre,"Aereo")
    }

    @Test
    fun testAgregarMutacion() {
        mutacionService.agregarMutacion(gripe.id!!, supresion)
        mutacionService.agregarMutacion(gripe.id!!, bioalteracion)

        gripe = especieService.recuperarEspecie(gripe.id!!)
        assertEquals(2, gripe.mutaciones.size)
    }

    @Test
    fun testAgregarMutacionInexistente() {
        val exception = Assertions.assertThrows(IdInvalidoExcepcion::class.java) {
            mutacionService.agregarMutacion(1000, supresion)
        }

        assertEquals("No existe especie con id: 1000", exception.message)
    }

    @Test
    fun testAlExpandirElInsectoSeContagiaDelHumanoYEsteUltimoMutaYSeDefiendeAnteElMosquito() {
        mutacionService.agregarMutacion(gripe.id!!, supresion)

        humano = vectorService.recuperarVector(humano.id!!)
        assertEquals(1, humano.especies.size)
        assertEquals(0, insecto.especies.size)

        ubicacionService.expandir(quilmes.id!!)

        humano = vectorService.recuperarVector(humano.id!!)
        insecto = vectorService.recuperarVector(insecto.id!!)

        // el humano no se contagió a sí mismo y debe tener mutaciones por contagiar al insecto
        assertEquals(1, humano.especies.size)
        assertEquals(1, humano.mutaciones.size)

        // el insecto fue contagiado por el humano
        assertTrue(insecto.estaInfectado())
        assertEquals(1, insecto.especies.size)
        assertEquals(0, insecto.mutaciones.size)


        ubicacionService.mover(mosquito.id!!, quilmes.id!!)

        // al mover al mosquito a quilmes el humano no se contagia de fiebre ya que la supresion lo defiende
        humano = vectorService.recuperarVector(humano.id!!)
        assertTrue(humano.estaInfectado())
        assertEquals(1, humano.especies.size)
        assertEquals(1, humano.mutaciones.size)
    }


    @Test
    fun alMoverAMosquitoAQuilmesElHumanoSeContagia() {

        ubicacionService.mover(mosquito.id!!, quilmes.id!!)

        humano = vectorService.recuperarVector(humano.id!!)
        assertTrue(humano.estaInfectado())
        assertEquals(2, humano.especies.size)
    }
    @Test
    fun elHumanoMutaYEliminaLaFiebreContagiadaPorElMosquito() {

        ubicacionService.mover(mosquito.id!!, quilmes.id!!)

        humano = vectorService.recuperarVector(humano.id!!)
        assertTrue(humano.estaInfectado())
        assertEquals(2, humano.especies.size)

        mutacionService.agregarMutacion(gripe.id!!, supresion)

        ubicacionService.expandir(quilmes.id!!)

        humano = vectorService.recuperarVector(humano.id!!)
        assertTrue(humano.estaInfectado())
        assertEquals(1, humano.mutaciones.size)
        assertEquals(1, humano.especies.size)
        assertEquals(gripe, humano.especies.first())
    }

    @Test
    fun InsectoNoPuedeContagiarAInsecto() {
        ubicacionService.mover(mosquito.id!!,quilmes.id!!)
        mosquito = vectorService.recuperarVector(mosquito.id!!)
        assertEquals(0, mosquito.mutaciones.size)

        var bicho:Vector = Insecto(bernal)
        vectorService.crearVector(bicho)
        ubicacionService.mover(mosquito.id!!,bernal.id!!)
        bicho = vectorService.recuperarVector(bicho.id!!)
        assertEquals(0, bicho.especies.size)
    }
    @Test
    fun InsectoAlMutarConBioalteracionInsectoContagiaAInsecto() {
        mutacionService.agregarMutacion(fiebre.id!!, bioalteracion)
        assertEquals(0, mosquito.mutaciones.size)
        ubicacionService.mover(mosquito.id!!,quilmes.id!!)
        mosquito = vectorService.recuperarVector(mosquito.id!!)
        assertEquals(1, mosquito.mutaciones.size)

        var bicho:Vector = Insecto(bernal)
        vectorService.crearVector(bicho)
        assertEquals(0, bicho.especies.size)
        ubicacionService.mover(mosquito.id!!,bernal.id!!)
        bicho = vectorService.recuperarVector(bicho.id!!)
        assertEquals(1, bicho.especies.size)
    }


    @AfterEach
    fun cleanup() {
        dataService.cleanAll()
        ubicacionService.deleteAll()
    }
}