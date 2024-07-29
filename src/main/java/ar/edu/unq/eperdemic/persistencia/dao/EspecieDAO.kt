package ar.edu.unq.eperdemic.persistencia.dao

import ar.edu.unq.eperdemic.modelo.Especie
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface EspecieDAO : CrudRepository<Especie, Long> {

    @Query("SELECT e.cantidadDeInfectados FROM Especie e WHERE e.id = ?1")
    fun cantidadDeInfectados(idDeEspecie: Long) : Long

    @Query("""
        SELECT es FROM Vector v
        JOIN v.especies es
        WHERE TYPE(v) = Humano
        GROUP BY es.id
        ORDER BY COUNT(es.id) DESC
            """)
    fun especieLider(pageable: Pageable): Page<Especie>

    @Query("""
        FROM Especie especie
        WHERE especie IN (
            SELECT especie2
            FROM Vector vector
            JOIN vector.especies especie2
            WHERE TYPE(vector) = Humano
            OR TYPE(vector) = Animal
        )
        GROUP BY especie
        ORDER BY COUNT(*) DESC
    """)
    fun lideres():List<Especie>
}