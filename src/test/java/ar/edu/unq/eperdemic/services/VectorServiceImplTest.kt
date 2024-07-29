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
import ar.edu.unq.eperdemic.modelo.vectores.Insecto
import ar.edu.unq.eperdemic.modelo.vectores.Vector
import ar.edu.unq.eperdemic.services.impl.VectorServiceImpl
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.geo.Point
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VectorServiceImplTest {

    @Autowired lateinit var vectorService: VectorServiceImpl
    @Autowired lateinit var ubicacionService: UbicacionService
    @Autowired lateinit var patogenoService: PatogenoService
    @Autowired lateinit var dataService: DataService
    @Autowired lateinit var distritoService: DistritoService


    lateinit var pedro: Humano
    lateinit var mosquito: Insecto

    lateinit var quilmes: Ubicacion
    lateinit var berazategui: Ubicacion


    lateinit var dengue: Especie

    lateinit var virus: Patogeno
    lateinit var distrito: Distrito

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


        quilmes = ubicacionService.crearUbicacion(Ubicacion("Quilmes", Pair(1.0,1.0)))
        berazategui = ubicacionService.crearUbicacion(Ubicacion("Berazategui", Pair(10.0,10.0)))

        mosquito = Insecto(quilmes)
        vectorService.crearVector(mosquito)

        pedro = Humano(quilmes)
        vectorService.crearVector(pedro)

        quilmes = ubicacionService.recuperarUbicacion(quilmes.id!!)

        virus= Patogeno(
            50,
            50,
            5,
            10,
            50,
            "virus"
        )
        patogenoService.crearPatogeno(virus)

        dengue = patogenoService.agregarEspecie(virus.id!!, "dengue", "arg", quilmes.id!!)

    }

    @Test
    fun recuperarVector(){
        var mosquitoCreado = vectorService.recuperarVector(mosquito.id!!)

        Assertions.assertEquals(quilmes.nombre, mosquitoCreado.ubicacion.nombre)
        Assertions.assertEquals(1, mosquitoCreado.especies.size)
    }

    @Test
    fun recuperarVectorInexistente(){
        val exception = Assertions.assertThrows(IdInvalidoExcepcion::class.java) {
            vectorService.recuperarVector(999999)
        }
        Assertions.assertEquals("Vector con id: 999999 no existe", exception.message)
    }

    @Test
    fun testCrearVector() {
        var mosquitoCreado = vectorService.recuperarVector(mosquito.id!!)

        Assertions.assertNotNull(mosquitoCreado.id)

        Assertions.assertEquals(quilmes.nombre, mosquitoCreado.ubicacion.nombre)
        Assertions.assertEquals(1, mosquitoCreado.especies.size)
    }

    @Test
    fun recuperarTodosLosVectores(){
        var vectoresRecuperados = vectorService.recuperarATodosLosVectores()

        Assertions.assertEquals(2,vectoresRecuperados.size)
        Assertions.assertTrue(vectoresRecuperados.contains(mosquito))
        Assertions.assertTrue(vectoresRecuperados.contains(pedro))
    }

    @Test
    fun recuperarTodosLosVectoresEmpty(){
        dataService.cleanAll()
        var vectoresRecuperados = vectorService.recuperarATodosLosVectores()

        Assertions.assertEquals(0,vectoresRecuperados.size)
    }

    @Test
    fun updateVector(){
        var mosquitoRecuperado: Vector = vectorService.recuperarVector(mosquito.id!!)
        mosquitoRecuperado.ubicacion = berazategui
        vectorService.actualizarVector(mosquitoRecuperado)
        var mosquitoCreado = vectorService.recuperarVector(mosquito.id!!)

        Assertions.assertEquals(berazategui.nombre, mosquitoCreado.ubicacion.nombre)
        Assertions.assertEquals(1, mosquitoCreado.especies.size)
    }

    @Test
    fun updateVectorConIdInexistente(){
        var mecaMosquito = Insecto(quilmes)
        mecaMosquito.ubicacion = berazategui

        val exception = Assertions.assertThrows(EntidadNoPersistidaException::class.java) {
            vectorService.actualizarVector(mecaMosquito)
        }
        Assertions.assertEquals("Vector no esta persistido", exception.message)
    }

    @Test
    fun infectarALaFuerza(){

        vectorService.infectar(mosquito.id!!,dengue.id!!)

        var mosquitoInfectado = vectorService.recuperarVector(mosquito.id!!)

        Assertions.assertEquals(1,mosquitoInfectado.especies.size)
    }

    @Test
    fun infectarALaFuerzaMosquitoInexistente(){

        val exception = Assertions.assertThrows(IdInvalidoExcepcion::class.java) {
            vectorService.infectar(99999,dengue.id!!)
        }
        Assertions.assertEquals("Vector con id: 99999 no existe", exception.message)
    }

    @Test
    fun getEnfermedades(){
        Assertions.assertEquals(1,vectorService.enfermedades(mosquito.id!!).size)
    }

    @Test
    fun getEnfermedadesEmpty(){
        dataService.cleanAll()
        Assertions.assertEquals(0,vectorService.enfermedades(mosquito.id!!).size)
    }


    @AfterEach
    fun cleanup() {
        dataService.cleanAll()
        ubicacionService.deleteAll()
    }
}