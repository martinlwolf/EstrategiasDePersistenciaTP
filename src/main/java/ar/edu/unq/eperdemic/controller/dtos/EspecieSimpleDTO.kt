package ar.edu.unq.eperdemic.controller.dtos

import ar.edu.unq.eperdemic.modelo.Especie

class EspecieSimpleDTO (
    val id: Long?,
    val cantidadDeInfectados: Int,
    val nombre: String,
    val paisDeOrigen: String) {

    fun aModelo(): Especie {
        val especie = Especie()
        especie.id = this.id
        especie.cantidadDeInfectados = this.cantidadDeInfectados
        especie.nombre = this.nombre
        especie.paisDeOrigen = this.paisDeOrigen

        return especie
    }

    companion object {
        fun desdeModelo(especies: Especie) =
            EspecieSimpleDTO(
                id = especies.id,
                cantidadDeInfectados = especies.cantidadDeInfectados,
                nombre = especies.nombre,
                paisDeOrigen = especies.paisDeOrigen
            )
    }
}