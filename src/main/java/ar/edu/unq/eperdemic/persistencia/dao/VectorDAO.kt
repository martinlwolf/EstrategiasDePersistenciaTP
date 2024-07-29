package ar.edu.unq.eperdemic.persistencia.dao

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.vectores.Vector
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository


interface VectorDAO: CrudRepository<Vector, Long> {
    @Query(
        "SELECT especie FROM Vector v JOIN v.especies especie WHERE v.id = ?1"
    )
    fun recuperarEspecies(vectorId: Long): List<Especie>

    @Query(
        """
            select distinct v
            from Vector v
            where v.ubicacion.nombre = ?1
        """
    )
    fun recuperarVectoresEn(nombreUbicacion: String): List<Vector>
}