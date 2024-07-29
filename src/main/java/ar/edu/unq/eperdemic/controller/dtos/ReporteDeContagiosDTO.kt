package ar.edu.unq.eperdemic.controller.dtos

import ar.edu.unq.eperdemic.modelo.ReporteDeContagios

class ReporteDeContagiosDTO(
    var vectoresPresentes:Int,
    var vectoresInfectados: Int,
    var especieDominante: EspecieDTO?
) {
    companion object {
        fun desdeModelo(reporte: ReporteDeContagios) =
            ReporteDeContagiosDTO(
                vectoresPresentes = reporte.vectoresPresentes,
                vectoresInfectados = reporte.vectoresInfectados,
                especieDominante = EspecieDTO.desdeModelo(reporte.especieDominante!!)
            )
    }
}