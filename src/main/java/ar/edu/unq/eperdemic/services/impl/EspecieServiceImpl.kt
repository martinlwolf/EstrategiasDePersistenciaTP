package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.exceptions.EntidadNoPersistidaException
import ar.edu.unq.eperdemic.exceptions.IdInvalidoExcepcion
import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.persistencia.dao.EspecieDAO
import ar.edu.unq.eperdemic.services.EspecieService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class EspecieServiceImpl(): EspecieService {

    @Autowired
    private lateinit var especieDAO: EspecieDAO;

    override fun actualizarEspecie(especie : Especie): Especie{
        if (especie.id == null || especieDAO.findByIdOrNull(especie.id!!)==null)
            throw EntidadNoPersistidaException("Especie no esta persistido")

        return especieDAO.save(especie)
    }
    override fun recuperarEspecie(id: Long): Especie {
        return  especieDAO.findByIdOrNull(id) ?: throw IdInvalidoExcepcion("Especie con id: ${id} no existe")
    }
    override fun recuperarATodasLasEspecies(): List<Especie> {
        return especieDAO.findAll().toList()
    }
    override fun cantidadDeInfectados(especieId: Long): Int {
        return especieDAO.cantidadDeInfectados(especieId).toInt()
    }
}