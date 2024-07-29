package ar.edu.unq.eperdemic.persistencia.dao

import ar.edu.unq.eperdemic.modelo.Distrito
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon
import org.springframework.data.mongodb.repository.Aggregation
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query


interface DistritoDAO : MongoRepository<Distrito, String> {
    @Query("{ 'coordenadasDistrito': { \$geoIntersects: { \$geometry: ?0 } } }", exists = true)
    fun existenDistritosInterceptadosPorCoordenadas(poligono: GeoJsonPolygon): Boolean

    @Query("{ 'nombreDistrito': ?0 }", exists = true)
    fun existeDistritoConElNombre (nombreDistrito: String): Boolean

    @Aggregation( pipeline = [
        "{\$match: {tipo: 'Distrito'}}",
        "{\$project: {_id: 1, distrito: '\$nombreDistrito' ,ubicacionesInf: {\$size: {\$setIntersection: ['\$ubicacionesDistrito.nombreUbicacion', ?0]}}}}",
        "{\$match: {ubicacionesInf: {\$gt: 0}}}",
        "{\$sort: {ubicacionesInf: -1}}",
        "{\$limit: 1}" ])
    fun findDistritoMasEnfermo(ubicacionesInfectadas: List<String>): String?

    @Query("{ 'coordenadasDistrito': { \$geoIntersects: { \$geometry: { type: 'Point', coordinates: [?0, ?1] } } } }")
    fun findDistritoPorCoordenadas(longitude: Double, latitude: Double): Distrito?

    fun findDistritoByNombreDistrito(nombre: String): Distrito?

    @Query("{'tipo':'Distrito'}")
    fun distritos():List<Distrito>
    }


