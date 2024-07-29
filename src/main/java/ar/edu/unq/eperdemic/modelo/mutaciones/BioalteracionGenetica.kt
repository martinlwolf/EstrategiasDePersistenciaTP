package ar.edu.unq.eperdemic.modelo.mutaciones

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.vectores.Vector
import javax.persistence.Entity

@Entity
class BioalteracionGenetica(val tipoVector:String):Mutacion() {

    override fun esComodinPara(tipoVector: String): Boolean {
        return  this.tipoVector == tipoVector
    }

    override fun esMasFuerteQue(defensa: Int): Boolean {
        return false
    }

    override fun eliminarEspeciesDe(vector: Vector, especie: Especie) {}
}
