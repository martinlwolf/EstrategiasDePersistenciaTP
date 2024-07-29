package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno

interface PatogenoService {
    fun crearPatogeno(patogeno: Patogeno): Patogeno
    fun recuperarPatogeno(id: Long): Patogeno?
    fun recuperarATodosLosPatogenos(): List<Patogeno>
    fun actualizar(patogeno: Patogeno): Patogeno
    fun agregarEspecie(idDePatogeno: Long, nombreEspecie: String, paisDeOrigen : String,ubicacionId: Long) : Especie
    fun eliminarPatogeno(patogenoId: Long)
    fun especiesDePatogeno(patogenoId: Long): List<Especie>
    fun esPandemia(especieId: Long): Boolean
}