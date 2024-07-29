package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.exceptions.EntidadNoPersistidaException
import ar.edu.unq.eperdemic.exceptions.IdInvalidoExcepcion
import ar.edu.unq.eperdemic.exceptions.NoVectoresException
import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.utils.Randomizador
import ar.edu.unq.eperdemic.modelo.vectores.Vector
import ar.edu.unq.eperdemic.persistencia.dao.EspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.PatogenoDAO
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.VectorDAO
import ar.edu.unq.eperdemic.services.PatogenoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class PatogenoServiceImpl() : PatogenoService {

    @Autowired
    private lateinit var patogenoDAO: PatogenoDAO;

    @Autowired
    private lateinit var especieDAO: EspecieDAO;

    @Autowired
    private lateinit var ubicacionDAO: UbicacionDAO;

    @Autowired
    private lateinit var vectorDAO: VectorDAO

    override fun crearPatogeno(patogeno: Patogeno): Patogeno {
        return patogenoDAO.save(patogeno)
    }

    override fun recuperarPatogeno(id: Long): Patogeno? {
        return patogenoDAO.findByIdOrNull(id)
    }

    override fun recuperarATodosLosPatogenos(): List<Patogeno> {
        return patogenoDAO.findAll().toList()
    }

    override fun actualizar(patogeno: Patogeno): Patogeno {
        if (patogeno.id == null || patogenoDAO.findByIdOrNull(patogeno.id!!)==null)
            throw EntidadNoPersistidaException("Patogeno no esta persistido")

        return patogenoDAO.save(patogeno)
    }

    override fun agregarEspecie(
        idDePatogeno: Long,
        nombreEspecie: String,
        paisDeOrigen: String,
        ubicacionId: Long
    ): Especie {

        val ubicacion = ubicacionDAO.findByIdOrNull(ubicacionId)
            ?: throw IdInvalidoExcepcion("No existe ubicacion con el id ${ubicacionId}")
        val vectores = vectorDAO.recuperarVectoresEn(ubicacion.nombre)
        if (vectores.isEmpty()) {
            throw NoVectoresException("no existen vectores en la ubicacion dada")
        }

        val patogeno =
            patogenoDAO.findByIdOrNull(idDePatogeno)
                ?: throw IdInvalidoExcepcion("Patogeno con id: ${idDePatogeno} no existe")
        val especie = patogeno.crearEspecie(nombreEspecie, paisDeOrigen)
        especieDAO.save(especie)

        infectarAlAzar(vectores, especie)
        return especie

    }


    override fun eliminarPatogeno(patogenoId: Long) {
        patogenoDAO.deleteById(patogenoId)
    }

    override fun especiesDePatogeno(patogenoId: Long): List<Especie> {
        return patogenoDAO.findAllEspecies(patogenoId)

    }

    override fun esPandemia(especieId: Long): Boolean {
        val locaciones = ubicacionDAO.cantidadUbicaciones()
        val res = ubicacionDAO.cantUbicacionesConEspecie(especieId)
        if (res == 0) throw IdInvalidoExcepcion("Especie con id: $especieId no existe")
        return res > locaciones / 2

    }

    private fun infectarAlAzar(vectores: List<Vector>, especie: Especie) {

        val vector = Randomizador.getElemRandom(vectores.toList())
        vector!!.infectar(especie)
        vectorDAO.save(vector)
        especieDAO.save(especie)
    }

}