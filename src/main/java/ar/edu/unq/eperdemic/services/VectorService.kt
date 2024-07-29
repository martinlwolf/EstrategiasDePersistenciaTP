package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.vectores.Vector

interface VectorService {

    fun crearVector(vector: Vector): Vector
    fun recuperarVector(vectorId: Long): Vector
    fun recuperarATodosLosVectores(): List<Vector>
    fun actualizarVector(vector: Vector):Vector
    fun eliminarVector(vectorId: Long)
    fun infectar(vectorId: Long, especieId: Long)
    fun enfermedades(vectorId: Long): List<Especie>
}