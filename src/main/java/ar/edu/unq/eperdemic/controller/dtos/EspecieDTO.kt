package ar.edu.unq.eperdemic.controller.dtos

import ar.edu.unq.eperdemic.modelo.Especie

class EspecieDTO(
                 val id: Long?,
                 val cantidadDeInfectados: Int,
                 val patogeno: PatogenoSimpleDTO,
                 val nombre: String,
                 val paisDeOrigen: String,
                 var mutaciones: MutableList<MutacionDTO> = mutableListOf()
) {

    fun aModelo(): Especie {
        val especie = Especie()
        especie.id = this.id
        especie.cantidadDeInfectados = this.cantidadDeInfectados
        especie.nombre = this.nombre
        especie.patogeno = this.patogeno.aModelo()
        especie.paisDeOrigen = this.paisDeOrigen
        especie.mutaciones = this.mutaciones.map { mutacionDTO  -> mutacionDTO.aModelo() }.toCollection(mutableListOf())

        return especie
    }

    companion object {
        fun desdeModelo(especie: Especie) =
            EspecieDTO(
                id = especie.id,
                cantidadDeInfectados = especie.cantidadDeInfectados,
                patogeno = PatogenoSimpleDTO.desdeModelo(especie.patogeno),
                nombre = especie.nombre,
                paisDeOrigen = especie.paisDeOrigen,
                mutaciones = especie.mutaciones
                    .map { mutacion -> MutacionDTO.desdeModelo(mutacion) }
                    .toCollection(mutableListOf())
            )
    }

}