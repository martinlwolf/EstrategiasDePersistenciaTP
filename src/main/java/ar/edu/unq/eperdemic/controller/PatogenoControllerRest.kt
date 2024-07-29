package ar.edu.unq.eperdemic.controller

import ar.edu.unq.eperdemic.controller.dtos.EspecieDTO
import ar.edu.unq.eperdemic.controller.dtos.EspecieSimpleDTO
import ar.edu.unq.eperdemic.controller.dtos.PatogenoDTO
import ar.edu.unq.eperdemic.services.PatogenoService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@CrossOrigin
@RequestMapping("/patogeno")
class PatogenoControllerRest(private val patogenoService: PatogenoService) {

    @PostMapping("/crear")
    fun crearPatogeno(@RequestBody patogeno: PatogenoDTO) = patogenoService.crearPatogeno(patogeno.aModelo())

    @PutMapping("/actualizar")
    fun actualizarPatogeno(@RequestBody patogeno: PatogenoDTO) {
            patogenoService.actualizar(patogeno.aModelo())
    }

    @GetMapping("/{patogenoId}")
    fun recuperarPatogeno(@PathVariable patogenoId: Long): PatogenoDTO {
        val patogeno = patogenoService.recuperarPatogeno(patogenoId) ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "El patógeno con ID $patogenoId no se encontró"
        )
        return PatogenoDTO.desdeModelo(patogeno)
    }

    @GetMapping("/recuperarTodos")
    fun todosLosPatogenos() = patogenoService.recuperarATodosLosPatogenos().map { PatogenoDTO.desdeModelo(it) }

    @DeleteMapping("/eliminar/{patogenoId}")
    fun eliminarPatogeno(@PathVariable patogenoId: Long) = patogenoService.eliminarPatogeno(patogenoId)

    @PostMapping("/agregarEspecie/{ubicacionId}/{patogenoId}")
    fun agregarEspecie(
        @RequestBody especie: EspecieSimpleDTO,
        @PathVariable ubicacionId: Long,
        @PathVariable patogenoId: Long
    ) =
        EspecieDTO.desdeModelo(
            patogenoService.agregarEspecie(
                patogenoId,
                especie.nombre,
                especie.paisDeOrigen,
                ubicacionId
            )
        )

    @GetMapping("/{patogenoId}/especies")
    fun especiesDePatogeno(@PathVariable patogenoId: Long) =
        patogenoService.especiesDePatogeno(patogenoId).map { EspecieDTO.desdeModelo(it) }

    @GetMapping("/esPandemia/{especieId}")
    fun esPandemia(@PathVariable especieId: Long) = patogenoService.esPandemia(especieId)
}