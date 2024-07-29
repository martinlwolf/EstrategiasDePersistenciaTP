package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.exceptions.*
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
import ar.edu.unq.eperdemic.services.impl.UbicacionServiceImpl
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.geo.Point
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon
import org.springframework.test.context.junit.jupiter.SpringExtension


@ExtendWith(SpringExtension::class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UbicacionServiceImplTest {

    @Autowired
    private lateinit var distritoService: DistritoService
    @Autowired
    lateinit var ubicacionService: UbicacionServiceImpl
    @Autowired
    lateinit var patogenoService: PatogenoService
    @Autowired
    lateinit var vectorService: VectorService
    @Autowired
    lateinit var dataService: DataService

    lateinit var quilmes: Ubicacion
    lateinit var avellaneda: Ubicacion
    lateinit var bernal: Ubicacion
    lateinit var ezpeleta: Ubicacion
    lateinit var quilmesOeste: Ubicacion
    lateinit var bernalOeste: Ubicacion
    lateinit var distrito: Distrito

    lateinit var humano: Vector
    lateinit var humano2: Vector
    lateinit var insecto: Vector
    lateinit var animal: Vector

    lateinit var virus: Patogeno
    lateinit var gripe: Especie

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

        quilmes = ubicacionService.crearUbicacion(Ubicacion("Quilmes", Pair(1.5, 1.5)))
        avellaneda = ubicacionService.crearUbicacion(Ubicacion("Avellaneda", Pair(2.0, 2.0)))
        bernal = ubicacionService.crearUbicacion(Ubicacion("Bernal", Pair(2.0, 1.5)))
        ezpeleta = ubicacionService.crearUbicacion(Ubicacion("Ezpeleta", Pair(4.0, 4.0)))
        quilmesOeste = ubicacionService.crearUbicacion(Ubicacion("QuilmesOeste", Pair(5.0, 5.0)))
        bernalOeste = ubicacionService.crearUbicacion(Ubicacion("BernalOeste", Pair(6.0, 6.0)))

        humano = Humano(quilmes)
        humano2 = Humano(bernal)
        insecto = Insecto(bernal)
        animal = Animal(bernal)

        humano = vectorService.crearVector(humano)

        this.virus =
            Patogeno(
                95,
                95,
                95,
                10,
                10,
                "Virus"
            )
    }

    @Test
    fun testAlCrearYLuegoRecuperarSeObtieneUnaUbicacionEquivalente() {

        val otroBernal = ubicacionService.recuperarUbicacion(bernal.id!!)

        assertEquals(bernal.nombre, otroBernal.nombre)
        assertEquals(bernal, otroBernal)
    }

    @Test
    fun testAlCrearDosUbicacionesConElMismoNombreSeLanzaUnaExcepcion() {
        val quilmesDuplicado = Ubicacion("Quilmes", Pair(1.0, 1.0))

        val exception = assertThrows(NombreDuplicadoExcepcion::class.java) {
            ubicacionService.crearUbicacion(quilmesDuplicado)
        }

        assertEquals("El nombre de la ubicación ya existe.", exception.message)
    }

    @Test
    fun testAlActualizarYLuegoRecuperarSeObtieneLaUbicacionActualizada() {
        quilmes.nombre = "Wilde"
        ubicacionService.actualizarUbicacion(quilmes)

        val wilde = ubicacionService.recuperarUbicacion(quilmes.id!!)

        assertNotEquals("Quilmes", wilde.nombre)
        assertEquals("Wilde", wilde.nombre)
    }

    @Test
    fun testAlRecuperarSeObtieneLaUbicacionEsperada() {
        val quilmes = ubicacionService.recuperarUbicacion(quilmes.id!!)

        assertEquals("quilmes", quilmes.nombre)
    }

    @Test
    fun testAlRecuperarUbicacionConIdInvalidoSeLanzaUnaExcepcion() {
        val exception = assertThrows(IdInvalidoExcepcion::class.java) {
            ubicacionService.recuperarUbicacion(900)
        }

        assertEquals("Ubicacion con id: 900 no existe", exception.message)
    }

    @Test
    fun testAlRecuperarTodasCuandoHayUbicacionesSeObtieneUnaListaConLasEsperadas() {
        val ubicaciones = ubicacionService.recuperarATodasLasUbicaciones()

        assertEquals(6, ubicaciones.size)
        assertTrue(ubicaciones.contains(quilmes))
        assertTrue(ubicaciones.contains(avellaneda))
    }

    @Test
    fun testAlRecuperarTodasCuandoNoHayUbicacionesSeObtieneUnaListaVacia() {
        this.cleanup()

        val ubicaciones = ubicacionService.recuperarATodasLasUbicaciones()

        assertTrue(ubicaciones.isEmpty())
    }

    @Test
    fun testAlMoverUnVectorCambiaDeUbicacion() {

        humano = vectorService.recuperarVector(humano.id!!)
        quilmes = ubicacionService.recuperarUbicacion(quilmes.id!!)
        avellaneda = ubicacionService.recuperarUbicacion(avellaneda.id!!)

        ubicacionService.conectar(quilmes.nombre, avellaneda.nombre, "Terrestre")

        assertEquals(quilmes, humano.ubicacion)

        ubicacionService.mover(humano.id!!, avellaneda.id!!)

        humano = vectorService.recuperarVector(humano.id!!)
        quilmes = ubicacionService.recuperarUbicacion(quilmes.id!!)
        avellaneda = ubicacionService.recuperarUbicacion(avellaneda.id!!)

        assertEquals(avellaneda, humano.ubicacion)
    }

    @Test
    fun testAlExpandirCasoHumano() {
        this.casoHumano()

        humano2 = vectorService.recuperarVector(humano2.id!!)

        assertTrue(humano2.estaInfectado())
        assertEquals(1, humano2.especies.size)

        ubicacionService.expandir(bernal.id!!)

        humano2 = vectorService.recuperarVector(humano2.id!!)
        insecto = vectorService.recuperarVector(insecto.id!!)
        animal = vectorService.recuperarVector(animal.id!!)

        assertTrue(humano2.estaInfectado())
        assertEquals(1, humano2.especies.size)

        assertTrue(insecto.estaInfectado())
        assertFalse(animal.estaInfectado())
    }

    @Test
    fun testAlExpandirCasoInsecto() {
        this.casoInsecto()

        insecto = vectorService.recuperarVector(insecto.id!!)

        assertTrue(insecto.estaInfectado())
        assertEquals(1, insecto.especies.size)

        ubicacionService.expandir(bernal.id!!)

        insecto = vectorService.recuperarVector(insecto.id!!)
        humano2 = vectorService.recuperarVector(humano2.id!!)
        animal = vectorService.recuperarVector(animal.id!!)

        assertTrue(insecto.estaInfectado())
        assertEquals(1, insecto.especies.size)

        assertTrue(humano2.estaInfectado())
        assertTrue(animal.estaInfectado())
    }

    @Test
    fun testAlExpandirCasoAnimal() {
        this.casoAnimal()

        animal = vectorService.recuperarVector(animal.id!!)

        assertTrue(animal.estaInfectado())
        assertEquals(1, animal.especies.size)

        ubicacionService.expandir(bernal.id!!)

        animal = vectorService.recuperarVector(animal.id!!)
        humano2 = vectorService.recuperarVector(humano2.id!!)
        insecto = vectorService.recuperarVector(insecto.id!!)

        assertTrue(animal.estaInfectado())
        assertEquals(1, animal.especies.size)

        assertTrue(humano2.estaInfectado())
        assertTrue(insecto.estaInfectado())
    }

    @Test
    fun testAlExpandirCuandoSoloHayUnVectorEnUnaUbicacionNoHaceNada() {

        crearVirusYAgregarGripe(quilmes)

        ubicacionService.expandir(quilmes.id!!)

        humano = vectorService.recuperarVector(humano.id!!)

        assertTrue(humano.estaInfectado())
        assertEquals(1, humano.especies.size)
    }

    private fun casoHumano() {
        humano2 = vectorService.crearVector(humano2)
        insecto = vectorService.crearVector(insecto)
        animal = vectorService.crearVector(animal)

        crearVirusYAgregarGripe(bernal)
    }

    private fun casoInsecto() {
        insecto = vectorService.crearVector(insecto)
        humano2 = vectorService.crearVector(humano2)
        animal = vectorService.crearVector(animal)

        crearVirusYAgregarGripe(bernal)
    }

    private fun casoAnimal() {
        animal = vectorService.crearVector(animal)
        humano2 = vectorService.crearVector(humano2)
        insecto = vectorService.crearVector(insecto)

        crearVirusYAgregarGripe(bernal)
    }

    private fun crearVirusYAgregarGripe(ubicacion: Ubicacion) {
        virus = patogenoService.crearPatogeno(virus)

        this.gripe =
            patogenoService.agregarEspecie(
                virus.id!!,
                "Gripe",
                "Argentina",
                ubicacion.id!!
            )
    }

    @Test
    fun testConectar() {
        ubicacionService.conectar("Avellaneda", "Quilmes", "Terrestre")
        ubicacionService.conectar("Bernal", "Avellaneda", "Terrestre")

        var ubicacionesConectadas = ubicacionService.conectados("Avellaneda")
        assertTrue(ubicacionesConectadas.contains(quilmes))
        assertFalse(ubicacionesConectadas.contains(bernal))
        assertTrue(ubicacionService.estaConectadaCon("Avellaneda", "Quilmes"))
    }

    @Test
    fun conectadas() {
        val exception = assertThrows<NombreInvalidoException> {
            ubicacionService.conectados("bsas")
        }
        assertEquals("No existe ubicacion bsas", exception.message)
    }

    @Test
    fun testEsAlcanzableVerdadero() {

        ubicacionService.conectar("Quilmes", "Avellaneda", "Terrestre")
        ubicacionService.conectar("Avellaneda", "Bernal", "Maritimo")

        assertTrue(ubicacionService.esAlcanzable(humano, "Bernal"))
    }

    @Test
    fun testEsAlcanzableFalso() {

        ubicacionService.conectar("Quilmes", "Avellaneda", "Aereo")
        ubicacionService.conectar("Avellaneda", "Bernal", "Maritimo")

        assertFalse(ubicacionService.esAlcanzable(humano, "Bernal"))
    }

    @Test
    fun moverVectorInexistente() {

        val exception = assertThrows(IdInvalidoExcepcion::class.java) {
            ubicacionService.mover(0, quilmes.id!!)
        }
        assertEquals("No existe vector con el id 0", exception.message)
    }

    @Test
    fun moverVectorAUbicacionInexistente() {

        val exception = assertThrows(IdInvalidoExcepcion::class.java) {
            ubicacionService.mover(humano.id!!, 0)
        }
        assertEquals("No existe ubicacion con el id 0", exception.message)
    }

    @Test
    fun moverVectorAUbicacionMuyLejana() {


        val exception = assertThrows(UbicacionMuyLejana::class.java) {
            ubicacionService.mover(humano.id!!, avellaneda.id!!)
        }
        assertEquals("No es posible llegar a avellaneda", exception.message)
    }

    @Test
    fun moverVectorAUbicacionNoAlcanzable() {

        ubicacionService.conectar(quilmes.nombre, avellaneda.nombre, "Aereo")

        val exception = assertThrows(UbicacionNoAlcanzable::class.java) {
            ubicacionService.mover(humano.id!!, avellaneda.id!!)
        }
        assertEquals("No es posible atravesar los caminos hasta avellaneda", exception.message)
    }

    @Test
    fun alMoverVectorSeInfectaEnLaNuevaUbicacion() {
        ubicacionService.conectar(quilmes.nombre, "Bernal", "Terrestre")

        crearVirusYAgregarGripe(quilmes)
        humano.infectar(gripe)
        vectorService.crearVector(Humano(bernal))

        assertTrue(ubicacionService.vectoresInfectados("Bernal").isEmpty())

        ubicacionService.mover(humano.id!!, bernal.id!!)

        assertFalse(ubicacionService.vectoresInfectados("Bernal").isEmpty())
    }

    @Test
    fun elVectorSeMuevePorElCaminoMasCorto() {

        crearVirusYAgregarGripe(quilmes)
        humano.infectar(gripe)
        vectorService.crearVector(Humano(bernal))
        vectorService.crearVector(Humano(avellaneda))

        ubicacionService.conectar(quilmes.nombre, bernal.nombre, "Terrestre")
        ubicacionService.conectar(bernal.nombre, avellaneda.nombre, "Terrestre")

        ubicacionService.conectar(quilmes.nombre, ezpeleta.nombre, "Terrestre")
        ubicacionService.conectar(ezpeleta.nombre, quilmesOeste.nombre, "Terrestre")
        ubicacionService.conectar(quilmesOeste.nombre, bernalOeste.nombre, "Terrestre")
        ubicacionService.conectar(bernalOeste.nombre, avellaneda.nombre, "Terrestre")

        assertTrue(ubicacionService.vectoresInfectados("Bernal").isEmpty())
        assertTrue(ubicacionService.vectoresInfectados("Avellaneda").isEmpty())

        ubicacionService.moverPorCaminoMasCorto(humano.id!!, avellaneda.nombre)

        humano = vectorService.recuperarVector(humano.id!!)

        val caminoMasCorto =
            this.ubicacionService.nombresUbicacionesDelCaminoMasCorto(quilmes.nombre, avellaneda.nombre, humano)

        assertEquals(listOf(bernal.nombre, avellaneda.nombre), caminoMasCorto)
        assertEquals(avellaneda, humano.ubicacion)

        assertFalse(ubicacionService.vectoresInfectados("Bernal").isEmpty())
        assertFalse(ubicacionService.vectoresInfectados("Avellaneda").isEmpty())
    }

    @Test
    fun moverVectorPorCaminoMasCortoAUbicacionNoAlcanzable() {

        ubicacionService.conectar(quilmes.nombre, avellaneda.nombre, "Aereo")

        val exception = assertThrows(UbicacionNoAlcanzable::class.java) {
            ubicacionService.moverPorCaminoMasCorto(humano.id!!, avellaneda.nombre)
        }
        assertEquals("No es posible atravesar los caminos hasta avellaneda", exception.message)
    }

    @Test
    fun moverVectorPorCaminoMasCortoVectorInexistente() {

        val exception = assertThrows(IdInvalidoExcepcion::class.java) {
            ubicacionService.moverPorCaminoMasCorto(0, avellaneda.nombre)
        }
        assertEquals("No existe vector con el id 0", exception.message)
    }

    @Test
    fun moverVectorPorCaminoMasCortoMismaUbicacion() {

        val exception = assertThrows(UbicacionInvalidaException::class.java) {
            ubicacionService.moverPorCaminoMasCorto(humano.id!!, humano.ubicacion.nombre)
        }
        assertEquals("El vector ya esta en ${humano.ubicacion.nombre}", exception.message)
    }

    @Test
    fun testAlMoverAUnaUbicacionAMasDe100KmDeDistanciaSeLanzaUnaExcepcion() {
        // están a mas de 100 km una de la otra
        var ub1: Ubicacion = Ubicacion("ub1", Pair(1.0, 1.0))
        var ub2: Ubicacion = Ubicacion("ub2", Pair(2.0, 2.0))

        ub1 = ubicacionService.crearUbicacion(ub1)
        ub2 = ubicacionService.crearUbicacion(ub2)

        ubicacionService.conectar(ub1.nombre, ub2.nombre, "Terrestre")

        var vector: Vector = Humano(ub1)
        vector = vectorService.crearVector(vector)

        val exception = assertThrows(UbicacionMuyLejana::class.java) {
            ubicacionService.mover(vector.id!!, ub2.id!!)
        }
        assertEquals("No es posible llegar a ub2 por que esta a mas de 100KM", exception.message)
    }

    @AfterEach
    fun cleanup() {
        dataService.cleanAll()
        ubicacionService.deleteAll()
    }
}