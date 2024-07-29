package ar.edu.unq.eperdemic.modelo.mutaciones

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.vectores.Vector
import java.util.*
import javax.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
abstract class Mutacion() {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "my_entity_seq")
    @TableGenerator(name = "my_entity_seq", table = "hibernate_sequences", pkColumnName = "sequence_name", valueColumnName = "next_val", pkColumnValue = "my_entity_seq", initialValue = 1, allocationSize = 1)
    var id: Long? = null

    abstract fun esComodinPara(tipoVector: String): Boolean

    abstract fun esMasFuerteQue(defensa: Int): Boolean

    abstract fun eliminarEspeciesDe(vector: Vector, especie: Especie)

    fun getTipoMutacion(): String{
        return this::class.java.simpleName
    }


    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val mutacion = o as Mutacion?
        return id == mutacion!!.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }
}