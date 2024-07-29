package ar.edu.unq.eperdemic.persistencia.dao

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface PatogenoDAO : CrudRepository<Patogeno, Long> {

    @Query(
        "SELECT especie FROM Patogeno p JOIN p.especies especie WHERE p.id = ?1"
    )
    fun findAllEspecies(idPatogeno: Long): List<Especie>
}