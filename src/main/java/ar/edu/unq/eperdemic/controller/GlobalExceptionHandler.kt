package ar.edu.unq.eperdemic.controller

import ar.edu.unq.eperdemic.exceptions.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestController

@ControllerAdvice
@RestController
class GlobalExceptionHandler {

    @ExceptionHandler(
        EntidadNoPersistidaException::class,
        IdInvalidoExcepcion::class,
        NombreInvalidoException::class)
    fun handleNotFoundException(ex: Exception): ResponseEntity<Any> {
        return ResponseEntity(ex.message, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(
        CampoInvalidoExcepcion::class,
        NombreDuplicadoExcepcion::class,
        TipoDeVectorDesconocido::class,
        ValorDeCapacidadNoValido::class,
        NoEspeciesException::class,
        NoVectoresException::class,
        UbicacionMuyLejana::class,
        UbicacionNoAlcanzable::class
    )
    fun handleBadRequestException(ex: Exception): ResponseEntity<Any> {
        return ResponseEntity(ex.message, HttpStatus.BAD_REQUEST)
    }


}