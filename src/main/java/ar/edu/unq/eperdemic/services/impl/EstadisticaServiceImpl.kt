package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.exceptions.NoVectoresException
import ar.edu.unq.eperdemic.exceptions.NombreInvalidoException
import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.ReporteDeContagios
import ar.edu.unq.eperdemic.modelo.vectores.Vector
import ar.edu.unq.eperdemic.persistencia.dao.EspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionDAO
import ar.edu.unq.eperdemic.services.EstadisticaService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class EstadisticaServiceImpl() : EstadisticaService {

    @Autowired
    private lateinit var ubicacionDAO: UbicacionDAO
    @Autowired
    private lateinit var especieDAO: EspecieDAO

    override fun especieLider(): Especie {
           return especieDAO.especieLider(PageRequest.of(0, 1)).content.first()
    }

    override fun lideres(): List<Especie> {
        return especieDAO.lideres()
    }

    override fun reporteDeContagios(nombreDeLaUbicacion: String): ReporteDeContagios {

            val vectores = this.recuperarVectoresPorNombre(nombreDeLaUbicacion)
            val vectoresInfectados = ubicacionDAO.vectoresInfectados(nombreDeLaUbicacion)
            val especieDominante = this.especieDominanteEn(nombreDeLaUbicacion)

            return ReporteDeContagios(vectores.size, vectoresInfectados.size, especieDominante)
    }

    private fun recuperarVectoresPorNombre(nombreDeLaUbicacion: String): List<Vector> {
        val ubicacionCount = ubicacionDAO.cantUbicacionesConNombre(nombreDeLaUbicacion)
        if (ubicacionCount == 0L) {
            throw NombreInvalidoException("No se encontró ninguna ubicación con el nombre '$nombreDeLaUbicacion'.")
        }
        return ubicacionDAO.vectoresEnUbicacion(nombreDeLaUbicacion)
    }

    private fun especieDominanteEn(nombreDeLaUbicacion: String): Especie{

        val ubicacionCount = ubicacionDAO.cantUbicacionesConNombre(nombreDeLaUbicacion)

        if (ubicacionCount == 0L) {
            throw NombreInvalidoException("No se encontró ninguna ubicación con el nombre '$nombreDeLaUbicacion'.")
        }

        val vectorCount = ubicacionDAO.cantVectoresEnUbicacion(nombreDeLaUbicacion)

        if (vectorCount == 0L) {
            throw NoVectoresException("No existen vectores en la ubicación con el nombre '$nombreDeLaUbicacion'.")
        }

        return ubicacionDAO.especiesDominantesEnUbicacion(nombreDeLaUbicacion, PageRequest.of(0, 1)).content.first()
    }
}