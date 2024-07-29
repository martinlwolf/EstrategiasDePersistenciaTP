package ar.edu.unq.eperdemic.controller.dtos

import ar.edu.unq.eperdemic.modelo.Patogeno

class PatogenoSimpleDTO (
    val id: Long?,
    val capacidadDeContagioHumanos: Int,
    val capacidadDeContagioAnimales: Int,
    val capacidadDeContagioInsectos: Int,
    val defensaMicroorganismos: Int,
    val capacidadDeBiomecanizacion: Int,
    val tipo: String?
) {
    companion object {
        fun desdeModelo(patogeno: Patogeno) =
            PatogenoSimpleDTO(
                id = patogeno.id,
                capacidadDeContagioHumanos = patogeno.capacidadDeContagioHumanos,
                capacidadDeContagioAnimales = patogeno.capacidadDeContagioAnimales,
                capacidadDeContagioInsectos = patogeno.capacidadDeContagioInsectos,
                defensaMicroorganismos = patogeno.defensaMicroorganismos,
                capacidadDeBiomecanizacion = patogeno.capacidadDeBiomecanizacion,
                tipo = patogeno.tipo
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
        return patogeno
    }

}
