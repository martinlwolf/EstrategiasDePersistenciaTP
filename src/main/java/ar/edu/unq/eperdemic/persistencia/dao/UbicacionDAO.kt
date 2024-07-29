package ar.edu.unq.eperdemic.persistencia.dao

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.vectores.Vector
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface UbicacionDAO : CrudRepository<Ubicacion, Long> {

    fun findByNombreOrNombreNull(nombreUbicacion: String): Ubicacion?

    @Query("SELECT COUNT(u) FROM Ubicacion u")
    fun cantidadUbicaciones(): Int

    @Query(
        """
            SELECT COUNT(DISTINCT v.ubicacion)
            FROM Vector v
            JOIN v.especies especie
            WHERE especie.id = ?1
        """
    )
    fun cantUbicacionesConEspecie(especieId: Long): Int

    @Query(
        """
            SELECT DISTINCT v
            FROM Vector v
            JOIN v.especies especie
            WHERE v.ubicacion.nombre = ?1
        """
    )
    fun vectoresInfectados(nombre: String): List<Vector>

    @Query(
        """
            SELECT COUNT(v)
            FROM Vector v
            WHERE v.ubicacion.nombre = ?1
        """
    )
    fun cantVectoresEnUbicacion(nombre: String): Long

    @Query(
        """
            SELECT DISTINCT v.ubicacion.nombre
            FROM Vector v
            JOIN v.especies especie
        """
    )
    fun ubicacionesInfectadas(): List<String>

    @Query(
        """
            SELECT COUNT(v.especies)
            FROM Vector v
            WHERE v.ubicacion.nombre = ?1
        """
    )
    fun cantEspeciesEnUbicacion(nombre: String): Long

    @Query(
        """
            SELECT especie
            FROM Especie especie
            WHERE especie IN (
                SELECT especie2
                FROM Vector v
                JOIN v.especies especie2
                WHERE v.ubicacion.nombre = ?1
            )
            GROUP BY especie
            ORDER BY COUNT(*) DESC
        """
    )
    fun especiesDominantesEnUbicacion(nombre: String, pageable: Pageable): Page<Especie>

    @Query(
        """
            SELECT COUNT(u)
            FROM Ubicacion u
            WHERE u.nombre = ?1
        """
    )
    fun cantUbicacionesConNombre(nombre: String): Long

    @Query(
        """
            SELECT v
            FROM Vector v
            WHERE v.ubicacion.nombre = ?1
        """
    )
    fun vectoresEnUbicacion(nombre: String): List<Vector>

}