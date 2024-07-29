package ar.edu.unq.eperdemic.controller

import ar.edu.unq.eperdemic.controller.dtos.MutacionDTO
import ar.edu.unq.eperdemic.services.MutacionService
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin
@RequestMapping("/mutacion")
class MutacionControllerREST(private val mutacionService: MutacionService) {

    @PostMapping("/{especieId}")
    fun agregarMutacion(@PathVariable especieId: Long, @RequestBody mutacion: MutacionDTO) {
            mutacionService.agregarMutacion(especieId, mutacion.aModelo())
    }
}