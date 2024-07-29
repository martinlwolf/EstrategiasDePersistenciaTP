package ar.edu.unq.eperdemic.persistencia.dao

import ar.edu.unq.eperdemic.persistencia.dto.UbicacionMongoDTO
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

interface UbicacionMongoDAO: MongoRepository<UbicacionMongoDTO, String> {

    @Query("{idJPA:'?0'}")
    fun findByIdJPAOrIdJPANull(idJPA: Long): UbicacionMongoDTO?

    @Query(
            "{'idJPA': ?0,'coordenadas': {\$nearSphere: {\$geometry: {'type': 'Point','coordinates': [?1, ?2]}, \$maxDistance: 100000}}}"
    )
    fun existeUbicacionAMenosDeCienKm(idJPA: String, longitud: Double, latitud: Double): UbicacionMongoDTO?
}