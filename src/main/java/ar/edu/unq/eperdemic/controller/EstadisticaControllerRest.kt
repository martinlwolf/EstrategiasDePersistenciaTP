package ar.edu.unq.eperdemic.controller

import ar.edu.unq.eperdemic.controller.dtos.EspecieDTO
import ar.edu.unq.eperdemic.controller.dtos.ReporteDeContagiosDTO
import ar.edu.unq.eperdemic.services.EstadisticaService
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin
@RequestMapping("/estadistica")
class EstadisticaControllerRest(private val estadisticaService: EstadisticaService) {


    @GetMapping("/especieLider")
    fun especieLider(): EspecieDTO = EspecieDTO.desdeModelo(estadisticaService.especieLider())

    @GetMapping("/lideres")
    fun lideres(): List<EspecieDTO> = estadisticaService.lideres().map { EspecieDTO.desdeModelo(it) }

    @GetMapping("/reportes/{nombreUbicacion}")
    fun reporteDeContagios(@PathVariable nombreUbicacion: String): ReporteDeContagiosDTO = ReporteDeContagiosDTO.desdeModelo(estadisticaService.reporteDeContagios(nombreUbicacion))

}