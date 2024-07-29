package ar.edu.unq.eperdemic.model

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.mutaciones.BioalteracionGenetica
import ar.edu.unq.eperdemic.modelo.mutaciones.SupresionBiomecanica
import ar.edu.unq.eperdemic.modelo.utils.Randomizador
import ar.edu.unq.eperdemic.modelo.vectores.Animal
import ar.edu.unq.eperdemic.modelo.vectores.Humano
import ar.edu.unq.eperdemic.modelo.vectores.Insecto
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS

@TestInstance(PER_CLASS)
class VectorTest {

    lateinit var murcielago: Animal
    lateinit var pedro: Humano
    lateinit var maria: Humano
    lateinit var mosquito: Insecto

    lateinit var quilmes: Ubicacion
    lateinit var dengue: Especie
    lateinit var gripe: Especie
    lateinit var covid: Especie
    lateinit var sarampion: Especie
    lateinit var micosis: Especie
    lateinit var virus: Patogeno
    lateinit var bacteria: Patogeno
    lateinit var hongo: Patogeno

    @BeforeEach
    fun setup() {
        Randomizador.cambiarAmbiente("Test")

        quilmes = Ubicacion("quilmes", Pair(1.0,1.0))

        murcielago = Animal(quilmes)
        pedro = Humano(quilmes)
        maria = Humano(quilmes)
        mosquito = Insecto(quilmes)

        maria.id = 2

        virus= Patogeno(
            50,
            60,
            55,
            10,
            50,
            "virus"
        )

        hongo= Patogeno(
            60,
            60,
            60,
            51,
            99,
            "hongo"
        )

        bacteria= Patogeno(
            40,
            50,
            50,
            10,
            40,
            "bacteria"
        )

        covid =  Especie(virus,"covid","china", mutableListOf(SupresionBiomecanica(30)))
        dengue = Especie(virus,"dengue","argentina")
        gripe =  Especie(virus,"gripe","inglaterra")
        sarampion = Especie(bacteria,"sarampion","guatemala")
        micosis = Especie(hongo,"micosis","guatemala")

        covid.id = 1
        dengue.id = 2
        gripe.id = 3
        sarampion.id = 4
        micosis.id = 5
    }

    @Test
    fun vectorHumanoSinMutacionesEsContagiadoYContagiadoresNoMutan(){

        pedro.contagiarse(covid,murcielago)
        pedro.contagiarse(dengue,mosquito)
        pedro.contagiarse(gripe,maria)

        Assertions.assertEquals(3,pedro.especies.size)
    }

    @Test
    fun vectorHumanoSinMutacionesEsContagiado_ElMurcielagoMutaConSupresion_MariaNoMutaPorBajaBiomecanizacion(){
        virus.capacidadDeBiomecanizacion = 49
        virus.capacidadDeContagioHumanos = 99

        pedro.contagiarse(covid,maria)

        Assertions.assertEquals(1,pedro.especies.size)
        Assertions.assertEquals(0, maria.mutaciones.size)
    }

    @Test
    fun vectorHumanoSinMutacionesEsContagiado_ElMurcielagoMutaConSupresion_MariaMuta(){

        pedro.contagiarse(covid,maria)

        Assertions.assertEquals(1,pedro.especies.size)
        Assertions.assertEquals(1, maria.mutaciones.size)
    }

    @Test
    fun vectorHumanoSinMutacionesEsContagiado_ElMurcielagoMutaConSupresionYEliminaEspecieConBajaDefensa(){

        murcielago.contagiarse(dengue,mosquito)
        pedro.contagiarse(covid,murcielago)


        Assertions.assertEquals(0,murcielago.especies.size)
        Assertions.assertEquals(1, murcielago.mutaciones.size)
    }

    @Test
    fun vectorHumanoSinMutacionesEsContagiado_ElMurcielagoMutaConSupresionYNoPuedeEliminarEspecieConAltaDefensa(){

        murcielago.contagiarse(micosis,mosquito)
        pedro.contagiarse(covid,murcielago)


        Assertions.assertEquals(1,murcielago.especies.size)
        Assertions.assertEquals(1, murcielago.mutaciones.size)
    }

     @Test
     fun vectorHumanoConSupresionIntentaSerContagiadoYSeDefiende(){
         maria.contagiarse(covid, pedro)
         pedro.contagiarse(dengue,mosquito)
         Assertions.assertEquals(1, pedro.mutaciones.size)
         Assertions.assertEquals(0, pedro.especies.size)
     }

    @Test
    fun vectorHumanoConSupresionIntentaSerContagiadoYNoSeDefiende(){
        hongo.defensaMicroorganismos = 45
        maria.contagiarse(covid, pedro)
        pedro.contagiarse(micosis,mosquito)
        Assertions.assertEquals(1, pedro.mutaciones.size)
        Assertions.assertEquals(1, pedro.especies.size)
    }

    @Test
    fun vectorHumanoConBioalteracionIntentaSerContagiadoYNoSeDefiende(){
        hongo.defensaMicroorganismos = 45
        micosis.agregarMutacion(BioalteracionGenetica("Animal"))
        maria.contagiarse(micosis, pedro)
        pedro.contagiarse(dengue,mosquito)
        Assertions.assertEquals(1, pedro.mutaciones.size)
        Assertions.assertEquals(1, pedro.especies.size)
    }

    @Test
    fun vectorHumanoConBioalteracionDeAnimalPuedeContagiarAnimalPorMutacion(){
        micosis.agregarMutacion(BioalteracionGenetica("Animal"))
        maria.contagiarse(micosis,pedro)
        murcielago.contagiarse(micosis,pedro)
        Assertions.assertEquals(1, pedro.mutaciones.size)
        Assertions.assertEquals(1, murcielago.especies.size)
    }

    @Test
    fun vectorHumanoConBioalteracionNoPuedeContagiarAnimalPorQueSuMutacionNoProvieneDeLaMismaEspecie(){
        micosis.agregarMutacion(BioalteracionGenetica("Animal"))
        maria.contagiarse(micosis,pedro)
        murcielago.contagiarse(dengue,pedro)
        Assertions.assertEquals(1, pedro.mutaciones.size)
        Assertions.assertEquals(0, murcielago.especies.size)
    }

    @Test
    fun vectorHumanoConBioalteracionDeInsectoNoPuedeContagiarAnimalPorMutacion(){
        micosis.agregarMutacion(BioalteracionGenetica("Insecto"))
        maria.contagiarse(micosis,pedro)
        murcielago.contagiarse(dengue,pedro)
        Assertions.assertEquals(1, pedro.mutaciones.size)
        Assertions.assertEquals(0, murcielago.especies.size)
    }

    @Test
    fun vectorHumanoConSupresionNoPuedeContagiarAnimal(){
        maria.contagiarse(covid,pedro)
        murcielago.contagiarse(dengue,pedro)
        Assertions.assertEquals(1, pedro.mutaciones.size)
        Assertions.assertFalse(murcielago.estaInfectado())
    }

    @Test
    fun vectorAnimalEsContagiado(){

        murcielago.contagiarse(covid,murcielago)
        murcielago.contagiarse(gripe,maria)
        murcielago.contagiarse(dengue,mosquito)

        var especiesVectorContagiado = murcielago.especies

        Assertions.assertTrue(especiesVectorContagiado.contains(dengue))
        Assertions.assertEquals(1,especiesVectorContagiado.size)
    }

    @Test
    fun vectorInsectoEsContagiado(){

        mosquito.contagiarse(dengue,mosquito)
        mosquito.contagiarse(sarampion,maria)
        mosquito.contagiarse(gripe,maria)
        mosquito.contagiarse(covid,murcielago)

        var especiesVectorContagiado = mosquito.especies
        var especiesEsperadas = listOf(gripe,covid)

        Assertions.assertTrue(especiesVectorContagiado.containsAll(especiesEsperadas))
        Assertions.assertEquals(2,especiesVectorContagiado.size)
    }

    @Test
    fun vectorSeContagiaDeTodasLasEnfermedadesDe(){
        maria.infectar(gripe)
        maria.infectar(dengue)
        pedro.contagiarseEnfermedadesDe(maria)

        var especiesVectorContagiado = pedro.especies

        Assertions.assertEquals(2, especiesVectorContagiado.size)
    }
}