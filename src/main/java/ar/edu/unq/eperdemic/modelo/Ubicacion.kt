package ar.edu.unq.eperdemic.modelo

import java.util.*
import javax.persistence.*
import kotlin.jvm.Transient

@Entity(name = "Ubicacion")
class Ubicacion() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(unique = true)
    lateinit var nombre: String

//    @OneToOne(cascade = [CascadeType.ALL])
    @Transient
    var coordenada: Pair<Double,Double>? = null

    constructor(nombre: String, coordenada:  Pair<Double,Double>? = null): this(){
        this.nombre = nombre.lowercase()
        this.coordenada = coordenada
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val ubicacion = o as Ubicacion?
        return id == ubicacion!!.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }
}