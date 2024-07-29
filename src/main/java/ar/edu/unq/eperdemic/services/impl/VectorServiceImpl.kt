package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.exceptions.EntidadNoPersistidaException
import ar.edu.unq.eperdemic.exceptions.IdInvalidoExcepcion
import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.vectores.Vector
import ar.edu.unq.eperdemic.persistencia.dao.EspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.VectorDAO
import ar.edu.unq.eperdemic.services.VectorService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class VectorServiceImpl() : VectorService {

    @Autowired
    private lateinit var vectorDAO: VectorDAO

    @Autowired
    private lateinit var especieDAO: EspecieDAO

    override fun crearVector(vector: Vector): Vector {
        if (vector.id == null || vectorDAO.findByIdOrNull(vector.id!!) == null) {
            return vectorDAO.save(vector)
        } else {
            throw IdInvalidoExcepcion("Vector con id: ${vector.id} no existe")
        }
    }

    override fun recuperarVector(vectorId: Long): Vector {
        val vector = vectorDAO.findByIdOrNull(vectorId)
        if (vector != null) {
            return vector
        } else {
            throw IdInvalidoExcepcion("Vector con id: ${vectorId} no existe")
        }
    }

    override fun recuperarATodosLosVectores(): List<Vector> {
        return vectorDAO.findAll().toList()
    }

    override fun actualizarVector(vector: Vector): Vector {
        if (vector.id == null || vectorDAO.findByIdOrNull(vector.id!!)==null)
            throw EntidadNoPersistidaException("Vector no esta persistido")

        return vectorDAO.save(vector)
    }

    override fun eliminarVector(vectorId: Long) {
        vectorDAO.deleteById(vectorId)
    }

    override fun infectar(vectorId: Long, especieId: Long) {
        val vector =vectorDAO.findByIdOrNull(vectorId)
            ?: throw IdInvalidoExcepcion("Vector con id: ${vectorId} no existe")
        val especie = especieDAO.findByIdOrNull(especieId)
            ?: throw IdInvalidoExcepcion("Especie con id ${especieId} no existe")
        vector.infectar(especie)
        vectorDAO.save(vector)
        especieDAO.save(especie)
    }

    override fun enfermedades(vectorId: Long): List<Especie> {
        return vectorDAO.recuperarEspecies(vectorId)
    }
}