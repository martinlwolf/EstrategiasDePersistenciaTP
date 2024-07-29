package ar.edu.unq.eperdemic.modelo.vectores

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Ubicacion
import javax.persistence.DiscriminatorValue
import javax.persistence.Entity

@Entity
@DiscriminatorValue("Humano")
class Humano(): Vector() {
    constructor(ubicacion: Ubicacion) : this() {
        this.ubicacion = ubicacion
    }

    override fun puedeContagiarSegunVector(vector: Vector, especieAContagiar: Especie): Boolean {

        return  this.tieneComodin(vector.getTipoVector(), especieAContagiar) || vector.getTipoVector() != "Animal"
    }

    override fun caminosRecorribles(): List<String> {
        return mutableListOf("Terrestre", "Maritimo")
    }
}