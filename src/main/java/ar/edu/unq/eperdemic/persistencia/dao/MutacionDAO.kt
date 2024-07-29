package ar.edu.unq.eperdemic.persistencia.dao

import ar.edu.unq.eperdemic.modelo.mutaciones.Mutacion
import org.springframework.data.repository.CrudRepository

interface MutacionDAO: CrudRepository<Mutacion, Long> {
}