package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.exceptions.CampoInvalidoExcepcion
import ar.edu.unq.eperdemic.modelo.mutaciones.Mutacion
import ar.edu.unq.eperdemic.modelo.utils.Randomizador
import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
open class Especie(): Serializable {

    var cantidadDeInfectados: Int = 0
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @ManyToOne
    lateinit var patogeno: Patogeno
    lateinit var nombre: String
    lateinit var paisDeOrigen: String

    @ManyToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    @JoinTable(
        name = "especie_mutacion",
        joinColumns = [JoinColumn(name = "especie_id")],
        inverseJoinColumns = [JoinColumn(name = "mutacion_id")]
    )
    var mutaciones: MutableList<Mutacion> = mutableListOf()

    constructor(patogeno: Patogeno, nombre: String, paisDeOrigen: String, mutaciones: MutableList<Mutacion>?= mutableListOf()):this(){
        if (nombre.isBlank()){
            throw CampoInvalidoExcepcion("El nombre de la especie no puede ser vacio")
        }
        if (paisDeOrigen.isBlank()){
            throw CampoInvalidoExcepcion("El pais de origen no puede ser vacio")
        }
        this.patogeno = patogeno
        this.nombre = nombre
        this.paisDeOrigen = paisDeOrigen
        mutaciones?.let { this.mutaciones = it }
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val especie = o as Especie?
        return id == especie!!.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }

    fun getCapacidadContagio(tipoVectorContagiador: String): Int {
        return patogeno.getCapacidadContagio(tipoVectorContagiador)
    }

    fun incrementarCantidadInfectados() {
        this.cantidadDeInfectados +=1
    }

    fun getDefensa(): Int {
        return this.patogeno.defensaMicroorganismos
    }

    fun getCapacidadBiomecanizacion(): Int {
        return this.patogeno.capacidadDeBiomecanizacion
    }

    fun getMutacionRandom(mutaciones: List<Mutacion>): Mutacion? {
        val lista = this.mutaciones.filter { mutacion -> !mutaciones.contains(mutacion) }
        return Randomizador.getElemRandom(lista)
    }

    fun tieneMutaciones(): Boolean {
        return this.mutaciones.isNotEmpty()
    }

    fun agregarMutacion(mutacion: Mutacion) {
        this.mutaciones.add(mutacion)
    }
}