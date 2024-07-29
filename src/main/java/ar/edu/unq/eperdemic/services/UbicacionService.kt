package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.vectores.Vector

interface UbicacionService {

    fun crearUbicacion(ubicacion: Ubicacion): Ubicacion
    fun actualizarUbicacion(ubicacion: Ubicacion):Ubicacion
    fun recuperarUbicacion(id: Long): Ubicacion
    fun recuperarATodasLasUbicaciones(): List<Ubicacion>
    fun eliminarUbicacion(id: Long)
    fun mover(vectorId: Long, ubicacionId: Long)
    fun moverPorCaminoMasCorto(vectorId:Long, nombreDeUbicacion:String)
    fun expandir(ubicacionId: Long)
    fun vectoresInfectados(nombreUbicacion: String): List<Vector>
    fun conectar(nombreDeUbicacion1:String, nombreDeUbicacion2:String, tipoCamino:String)
    fun conectados(nombreDeUbicacion:String): List<Ubicacion>
    fun estaConectadaCon(nombreDeUbicacion1: String, nombreDeUbicacion2: String): Boolean
    fun esAlcanzable(vector: Vector, nombreDeUbicacionDestino: String): Boolean
    fun deleteAll()
    fun nombresUbicacionesDelCaminoMasCorto(nombreUbicacion1: String, nombreUbicacion2: String, vector: Vector): List<String>
    fun ubicacionesInfectadas(): List<String>
}