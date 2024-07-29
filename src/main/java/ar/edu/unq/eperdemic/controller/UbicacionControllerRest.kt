package ar.edu.unq.eperdemic.controller

import ar.edu.unq.eperdemic.controller.dtos.UbicacionDTO
import ar.edu.unq.eperdemic.services.UbicacionService
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin
@RequestMapping("/ubicacion")
class UbicacionControllerRest(private val ubicacionService: UbicacionService) {

    @PostMapping("/crear")
    fun crearUbicacion(@RequestBody ubicacion: UbicacionDTO) = UbicacionDTO.desdeModelo(ubicacionService.crearUbicacion(ubicacion.aModelo()))

    @PutMapping("/actualizar")
    fun actualizarUbicacion(@RequestBody ubicacion: UbicacionDTO) =
        UbicacionDTO.desdeModelo(ubicacionService.actualizarUbicacion(ubicacion.aModelo()))

    @GetMapping("/{ubicacionId}")
    fun recuperarUbicacion(@PathVariable ubicacionId: Long) =
        UbicacionDTO.desdeModelo(ubicacionService.recuperarUbicacion(ubicacionId))

    @GetMapping("/recuperarTodos")
    fun recuperarTodas() =
        ubicacionService.recuperarATodasLasUbicaciones().map { ubicacion -> UbicacionDTO.desdeModelo(ubicacion) }

    @DeleteMapping("/eliminar/{ubicacionId}")
    fun eliminarUbicacion(@PathVariable ubicacionId: Long) = ubicacionService.eliminarUbicacion(ubicacionId)

    @RequestMapping("/mover/{vectorId}/{ubicacionId}")
    fun mover(@PathVariable vectorId: Long, @PathVariable ubicacionId: Long) =
        ubicacionService.mover(vectorId, ubicacionId)

    @RequestMapping("/expandir/{ubicacionId}")
    fun expandir(@PathVariable ubicacionId: Long) = ubicacionService.expandir(ubicacionId)

    @RequestMapping("/conectar/{nombreDeUbicacion1}/{nombreDeUbicacion2}/{tipoCamino}")
    fun conectar(
        @PathVariable nombreDeUbicacion1: String,
        @PathVariable nombreDeUbicacion2: String,
        @PathVariable tipoCamino: String
    ) = ubicacionService.conectar(nombreDeUbicacion1, nombreDeUbicacion2,tipoCamino)

    @RequestMapping("/conectados/{nombreDeUbicacion}")
    fun conectados(@PathVariable nombreDeUbicacion: String): List<UbicacionDTO> =
        ubicacionService.conectados(nombreDeUbicacion).map { ubicacion -> UbicacionDTO.desdeModelo(ubicacion) }

    @RequestMapping("/moverCaminoCorto/{vectorId}/{nombreDeUbicacion}")
    fun moverPorCaminoMasCorto(@PathVariable vectorId: Long,@PathVariable nombreDeUbicacion: String) =
        ubicacionService.moverPorCaminoMasCorto(vectorId,nombreDeUbicacion)
}