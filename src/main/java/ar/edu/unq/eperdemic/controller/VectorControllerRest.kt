package ar.edu.unq.eperdemic.controller

import ar.edu.unq.eperdemic.controller.dtos.EspecieDTO
import ar.edu.unq.eperdemic.controller.dtos.VectorDTO
import ar.edu.unq.eperdemic.services.VectorService
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin
@RequestMapping("/vector")
class VectorControllerRest(private val vectorService: VectorService) {

    @PostMapping("/crear")
    fun crearVector(@RequestBody vector: VectorDTO) = VectorDTO.desdeModelo(vectorService.crearVector(vector.aModelo()))

    @GetMapping("/{vectorId}")
    fun recuperarVector(@PathVariable vectorId: Long): VectorDTO =
        VectorDTO.desdeModelo(vectorService.recuperarVector(vectorId))

    @PutMapping("/actualizar")
    fun actualizarVector(@RequestBody vector: VectorDTO) =  VectorDTO.desdeModelo(vectorService.actualizarVector(vector.aModelo()))

    @GetMapping("/recuperarTodos")
    fun recuperarATodosLosVectores(): List<VectorDTO> =
        vectorService.recuperarATodosLosVectores().map { vector -> VectorDTO.desdeModelo(vector) }

    @DeleteMapping("/eliminar/{vectorId}")
    fun eliminarVector(@PathVariable vectorId: Long) = vectorService.eliminarVector(vectorId)

    @RequestMapping("/infectar/{vectorId}/{especieId}")
    fun infectar(@PathVariable vectorId: Long, @PathVariable especieId: Long) =
        vectorService.infectar(vectorId, especieId)

    @GetMapping("/enfermedades/{vectorId}")
    fun enfermedades(@PathVariable vectorId: Long): List<EspecieDTO> =
        vectorService.enfermedades(vectorId).map { especie -> EspecieDTO.desdeModelo(especie) }
}