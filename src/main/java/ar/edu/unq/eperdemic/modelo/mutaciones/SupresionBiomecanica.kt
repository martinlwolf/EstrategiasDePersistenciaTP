package ar.edu.unq.eperdemic.modelo.mutaciones

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.utils.ValidadorPorcentaje
import ar.edu.unq.eperdemic.modelo.vectores.Vector
import javax.persistence.Entity

@Entity
class SupresionBiomecanica(): Mutacion() {
    constructor(potencia:Int) : this() {
        this.potencia = ValidadorPorcentaje.validar(potencia)
    }

    var potencia:Int = 0;

    override fun esComodinPara(tipoVector: String): Boolean {
        return false
    }

    override fun esMasFuerteQue(defensa: Int): Boolean {
        return this.potencia > defensa
    }

    override fun eliminarEspeciesDe(vector: Vector, especie: Especie) {
        vector.eliminarEspecies(this.potencia, especie)
    }
}