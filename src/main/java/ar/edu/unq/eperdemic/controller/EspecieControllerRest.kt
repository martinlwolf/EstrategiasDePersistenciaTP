package ar.edu.unq.eperdemic.controller

import ar.edu.unq.eperdemic.controller.dtos.EspecieDTO
import ar.edu.unq.eperdemic.services.EspecieService
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin
@RequestMapping("/especie")
class EspecieControllerRest (private val especieService: EspecieService){

    @PostMapping("/actualizar")
    fun actualizarEspecie(@RequestBody especie: EspecieDTO) = especieService.actualizarEspecie(especie.aModelo())

    @GetMapping("/{especieId}")
    fun recuperarEspecie(@PathVariable especieId: Long) = EspecieDTO.desdeModelo(especieService.recuperarEspecie(especieId))

    @GetMapping("/recuperarTodos")
    fun recuperarATodasLasEspecies() = especieService.recuperarATodasLasEspecies().map { especie -> EspecieDTO.desdeModelo(especie) }

    @GetMapping("/cantInfectados/{especieId}")
    fun cantidadDeInfectados (@PathVariable especieId: Long) = especieService.cantidadDeInfectados(especieId)
}