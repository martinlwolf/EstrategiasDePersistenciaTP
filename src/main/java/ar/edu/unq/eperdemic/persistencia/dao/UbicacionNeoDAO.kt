package ar.edu.unq.eperdemic.persistencia.dao

import ar.edu.unq.eperdemic.persistencia.dto.UbicacionNEODTO
import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.data.neo4j.repository.query.Query

interface UbicacionNeoDAO : Neo4jRepository<UbicacionNEODTO, Long?> {


    @Query(
        """
        MATCH (u1:UbicacionNEO {nombre:${'$'}nombreDeUbicacion1})
        MATCH (u2:UbicacionNEO {nombre:${'$'}nombreDeUbicacion2})
        CREATE (u1)-[c:CAMINO {tipoCamino: ${'$'}tipoCamino}]->(u2)
        """
    )
    fun conectar(nombreDeUbicacion1: String, nombreDeUbicacion2: String, tipoCamino: String)

    @Query(
        """
        CREATE (u:UbicacionNEO {idJPA: ${'$'}ubicacionId, nombre: ${'$'}nombreUbicacion})
        RETURN u
        """
    )
    fun guardar(ubicacionId: Long, nombreUbicacion: String): UbicacionNEODTO

    @Query(
        """
        MATCH (u:UbicacionNEO {idJPA:${'$'}ubicacionId})
        DELETE u
        """
    )
    fun deleteByIdJPA(ubicacionId: Long)

    @Query(
        """
        MATCH (u:UbicacionNEO {idJPA:${'$'}ubicacionId})
        SET u.nombre = ${'$'}nombreUbicacion
        RETURN u
        """
    )
    fun actualizar(ubicacionId: Long, nombreUbicacion: String): UbicacionNEODTO

    @Query(
        """
        MATCH (u:UbicacionNEO {nombre:${'$'}nombreDeUbicacion})-[*1]->(relacionados)
        RETURN DISTINCT relacionados
        """
    )
    fun conectados(nombreDeUbicacion: String): List<UbicacionNEODTO>

    @Query(

        """      
        RETURN EXISTS {
            MATCH (q:UbicacionNEO {nombre:${'$'}nombreDeUbicacion1})-[*1]->(b:UbicacionNEO {nombre:${'$'}nombreDeUbicacion2})
        } AS existeCamino
        """
    )
    fun estaConectadaCon(nombreDeUbicacion1: String, nombreDeUbicacion2: String): Boolean

    @Query(
        """
        RETURN EXISTS {
            MATCH (start:UbicacionNEO {nombre: ${'$'}nombreDeUbicacion1}), (end:UbicacionNEO {nombre: ${'$'}nombreDeUbicacion2})
            MATCH path = (start)-[*1]->(end)
            WHERE ALL(camino IN relationships(path) WHERE camino.tipoCamino IN ${'$'}listCaminos)
        } AS existeCamino
        """
    )
    fun estaConectadaCon2(nombreDeUbicacion1: String, nombreDeUbicacion2: String, listCaminos: List<String>): Boolean

    @Query("MATCH(n) DETACH DELETE n")
    fun detachDelete()

    @Query(
        """
        RETURN EXISTS {
            MATCH (start:UbicacionNEO {nombre: ${'$'}nombreDeUbicacion1}), (end:UbicacionNEO {nombre: ${'$'}nombreDeUbicacion2})
            MATCH path = (start)-[*]->(end)
            WHERE ALL(camino IN relationships(path) WHERE camino.tipoCamino IN ${'$'}listCaminos)
        } AS existeCamino
        """
    )
    fun esAlcanzable(nombreDeUbicacion1: String, nombreDeUbicacion2: String, listCaminos: List<String>): Boolean

    @Query(
        """
        MATCH p = (ubicacionActual:UbicacionNEO {nombre:${'$'}nombreDeUbicacion1})-[:CAMINO*]->(ubicacionDestino:UbicacionNEO {nombre:${'$'}nombreDeUbicacion2})
        WHERE ALL(camino IN relationships(p) WHERE camino.tipoCamino IN ${'$'}listCaminos)
        WITH p
        LIMIT 1
        UNWIND nodes(p) AS ubicacion
        WITH ubicacion
        WHERE ubicacion.nombre <> ${'$'}nombreDeUbicacion1
        RETURN DISTINCT ubicacion.nombre
        """
    )
    fun ubicacionesIntermedias(nombreDeUbicacion1: String, nombreDeUbicacion2: String,listCaminos: List<String>):List<String>

    @Query(
        """
        MATCH (ubicacionActual:UbicacionNEO {nombre:${'$'}nombreDeUbicacion1}), (ubicacionDestino:UbicacionNEO {nombre:${'$'}nombreDeUbicacion2}),
        path = shortestPath((ubicacionActual)-[*]->(ubicacionDestino))
        WHERE ALL(camino IN relationships(path) WHERE camino.tipoCamino IN ${'$'}caminosRecorribles)
        UNWIND nodes(path) AS ubicacion
        WITH ubicacion
        WHERE ubicacion.nombre <> ${'$'}nombreDeUbicacion1
        RETURN DISTINCT ubicacion.nombre
        """
    )
    fun ubicacionesDelCaminoMasCorto(nombreDeUbicacion1: String, nombreDeUbicacion2: String, caminosRecorribles: List<String>): List<String>
}
