package ar.edu.unq.eperdemic.controller.dtos

import ar.edu.unq.eperdemic.modelo.Ubicacion

class UbicacionDTO (
    val id: Long?,
    val nombre: String,
    val coordenada: Pair<Double,Double>? = null
) {

    companion object {
        fun desdeModelo(ubicacion: Ubicacion) =
            UbicacionDTO (
                id = ubicacion.id,
                nombre = ubicacion.nombre,
                coordenada = ubicacion.coordenada
            )
    }

    fun aModelo(): Ubicacion {
        val ubicacion = Ubicacion(this.nombre,this.coordenada)
        ubicacion.id = this.id
        return ubicacion
    }

}