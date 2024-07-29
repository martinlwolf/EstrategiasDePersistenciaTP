package ar.edu.unq.eperdemic.controller

import ar.edu.unq.eperdemic.controller.dtos.DistritoDTO
import ar.edu.unq.eperdemic.services.DistritoService
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin
@RequestMapping("/distrito")
class DistritoControllerRest (private val distritoService: DistritoService) {

    @PostMapping("/crear")
    fun crear(@RequestBody distrito: DistritoDTO) = DistritoDTO.desdeModelo(distritoService.crear(distrito.aModelo()))

    @GetMapping("/masEnfermo")
    fun distritoMasEnfermo(): DistritoDTO = DistritoDTO.desdeModelo(distritoService.distritoMasEnfermo())
}