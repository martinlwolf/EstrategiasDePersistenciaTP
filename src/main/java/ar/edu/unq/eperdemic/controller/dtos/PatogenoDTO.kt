package ar.edu.unq.eperdemic.controller.dtos

import ar.edu.unq.eperdemic.modelo.Patogeno

class PatogenoDTO(
    val id: Long?,
    val capacidadDeContagioHumanos: Int,
    val capacidadDeContagioAnimales: Int,
    val capacidadDeContagioInsectos: Int,
    val defensaMicroorganismos: Int,
    val capacidadDeBiomecanizacion: Int,
    val tipo: String?,
    val especies: MutableSet<EspecieSimpleDTO>
) {
    companion object {
        fun desdeModelo(patogeno: Patogeno) =
            PatogenoDTO(
                id = patogeno.id,
                capacidadDeContagioHumanos = patogeno.capacidadDeContagioHumanos,
                capacidadDeContagioAnimales = patogeno.capacidadDeContagioAnimales,
                capacidadDeContagioInsectos = patogeno.capacidadDeContagioInsectos,
                defensaMicroorganismos = patogeno.defensaMicroorganismos,
                capacidadDeBiomecanizacion = patogeno.capacidadDeBiomecanizacion,
                tipo = patogeno.tipo,
                especies = patogeno.especies
                    .map { especie -> EspecieSimpleDTO.desdeModelo(especie) }
                    .toCollection(HashSet())
            )
    }


    fun aModelo(): Patogeno {
        val patogeno = Patogeno(
            capacidadDeContagioHumanos,
            capacidadDeContagioAnimales,
            capacidadDeContagioInsectos,
            defensaMicroorganismos,
            capacidadDeBiomecanizacion,
            tipo!!
        )
        patogeno.id= this.id
        patogeno.especies = this.especies
            .map { it.aModelo() }.toCollection(HashSet())
        return patogeno
    }

}
