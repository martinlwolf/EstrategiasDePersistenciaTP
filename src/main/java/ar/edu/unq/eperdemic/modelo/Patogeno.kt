package ar.edu.unq.eperdemic.modelo

import ar.edu.unq.eperdemic.exceptions.TipoDeVectorDesconocido
import ar.edu.unq.eperdemic.modelo.utils.ValidadorPorcentaje
import java.util.*
import javax.persistence.*

@Entity
class Patogeno(

)  {

    constructor(
        capacidadDeContagioHumanos: Int,
        capacidadDeContagioAnimales: Int,
        capacidadDeContagioInsectos: Int,
        defensaMicroorganismos: Int,
        capacidadDeBiomecanizacion: Int,
        tipo: String
    ) : this() {
        this.tipo = tipo
        this.capacidadDeContagioHumanos = ValidadorPorcentaje.validar(capacidadDeContagioHumanos)
        this.capacidadDeContagioAnimales = ValidadorPorcentaje.validar(capacidadDeContagioAnimales)
        this.capacidadDeContagioInsectos = ValidadorPorcentaje.validar(capacidadDeContagioInsectos)
        this.defensaMicroorganismos = ValidadorPorcentaje.validar(defensaMicroorganismos)
        this.capacidadDeBiomecanizacion = ValidadorPorcentaje.validar(capacidadDeBiomecanizacion)

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null


    var capacidadDeContagioHumanos: Int = 0
    var capacidadDeContagioAnimales: Int = 0
    var capacidadDeContagioInsectos: Int = 0
    var defensaMicroorganismos: Int = 0
    var capacidadDeBiomecanizacion: Int = 0
    var tipo: String? = null


    @OneToMany(mappedBy = "patogeno", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var especies: MutableSet<Especie> = HashSet()

    override fun toString(): String {
        return tipo!!
    }

    fun crearEspecie(nombreEspecie: String, paisDeOrigen: String): Especie {
        val especie = Especie(this, nombreEspecie, paisDeOrigen)
        this.especies.add(especie)

        return especie
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val patogeno = other as Patogeno?
        return id == patogeno!!.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }

    fun getCapacidadContagio(tipoVectorContagiador: String): Int {
        when (tipoVectorContagiador) {
            "Animal" -> return this.capacidadContagioFinal(this.capacidadDeContagioAnimales)
            "Humano" -> return this.capacidadContagioFinal(this.capacidadDeContagioHumanos)
            "Insecto" -> return this.capacidadContagioFinal(this.capacidadDeContagioInsectos)
             else -> throw TipoDeVectorDesconocido("Error: tipo de vector desconocido")
        }
    }

    private fun capacidadContagioFinal(capacidadContagio: Int): Int{
        return capacidadContagio + this.capacidadDeBiomecanizacion - this.defensaMicroorganismos
    }

}

