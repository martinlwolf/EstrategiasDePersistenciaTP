package ar.edu.unq.eperdemic.modelo.vectores

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.mutaciones.Mutacion
import ar.edu.unq.eperdemic.modelo.utils.Randomizador
import java.util.*
import javax.persistence.*
import kotlin.jvm.Transient

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipoVector", discriminatorType = DiscriminatorType.STRING)
abstract class Vector() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @ManyToOne
    lateinit var ubicacion: Ubicacion

    @ManyToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    @JoinTable(
        name = "vector_especie",
        joinColumns = [JoinColumn(name = "vector_id")],
        inverseJoinColumns = [JoinColumn(name = "especie_id")]
    )
    var especies: MutableSet<Especie> = HashSet()

    @ManyToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    @JoinTable(
        name = "vector_mutacion",
        joinColumns = [JoinColumn(name = "vector_id")],
        inverseJoinColumns = [JoinColumn(name = "mutacion_id")]
    )
    var mutaciones: MutableSet<Mutacion> = HashSet()

    @Transient
    var especiesRecolectadas: MutableSet<Especie> = HashSet()

    @Transient
    var eliminacionAutomatica: Boolean = true

    constructor(ubicacion: Ubicacion) : this() {
        this.ubicacion = ubicacion
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val vector = o as Vector?
        return id == vector!!.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }

    fun infectar(especie: Especie) {
        this.especies.add(especie)
        especie.incrementarCantidadInfectados()
    }

    fun contagiarse(especie: Especie, vectorContagiador: Vector) {
        if (!this.equals(vectorContagiador) && vectorContagiador.puedeContagiarA(this, especie)) {
            this.infectarse(especie, vectorContagiador)
        }
    }

    private fun infectarse(especie: Especie, vectorContagiador: Vector) {
        var capacidadDeContagio: Int = especie.getCapacidadContagio(vectorContagiador.getTipoVector())
        var random: Int = Randomizador.getIntRandom()
        if ((random + capacidadDeContagio) >= 100) {
            this.infectar(especie)
            vectorContagiador.mutar(especie)
        }
    }

    fun mutar(especie: Especie) {
        if (especie.getCapacidadBiomecanizacion() >= 50 && especie.tieneMutaciones()) {
            val mutacion = especie.getMutacionRandom(this.mutaciones.toMutableList())
            if (mutacion != null && !mutaciones.contains(mutacion)) {
                this.mutaciones.add(mutacion)
                mutacion.eliminarEspeciesDe(this, especie)
            }
        }
    }

    fun puedeContagiarA(vector: Vector, especie: Especie): Boolean {
        return !vector.puedeDefenderse(especie.getDefensa()) && this.puedeContagiarSegunVector(vector,especie)
    }

    fun puedeDefenderse(defensa: Int): Boolean {
        return this.mutaciones.any { mutacion -> mutacion.esMasFuerteQue(defensa) }
    }

    abstract fun puedeContagiarSegunVector(vector: Vector, especieAContagiar: Especie): Boolean

    fun getTipoVector(): String {
        return this::class.java.simpleName
    }

    fun estaInfectado(): Boolean {
        return this.especies.isNotEmpty()
    }

    fun contagiarseEnfermedadesDe(vectorContagiador: Vector) {
        vectorContagiador.especies.forEach { especie -> this.contagiarse(especie, vectorContagiador) }
    }

    fun tieneComodin(tipoVector: String, especieAContagiar: Especie): Boolean {
        return this.mutaciones.any { mutacion -> especieAContagiar.mutaciones.contains(mutacion) && mutacion.esComodinPara(tipoVector) }
    }

    fun eliminarEspecies(potencia: Int, especie: Especie) {
        this.recolectarEspeciesAEliminar(potencia, especie)

        if (eliminacionAutomatica) {
            this.eliminarEspeciesRecolectadas()
        }
    }

    private fun recolectarEspeciesAEliminar(potencia: Int, especie: Especie) {
        val especiesAEliminar = this.especies.filter { e -> e != especie && potencia > e.getDefensa() }.toHashSet()
        this.especiesRecolectadas = especiesAEliminar
    }

    fun eliminarEspeciesRecolectadas() {
        this.especies.removeAll(this.especiesRecolectadas)
    }

    abstract fun caminosRecorribles() : List<String>

}