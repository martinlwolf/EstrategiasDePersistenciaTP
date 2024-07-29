package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.exceptions.CoordenadasNoValidasExcepcion
import ar.edu.unq.eperdemic.exceptions.NoDistritosException
import ar.edu.unq.eperdemic.exceptions.NoEnfermosException
import ar.edu.unq.eperdemic.exceptions.NombreDuplicadoExcepcion
import ar.edu.unq.eperdemic.modelo.Distrito
import ar.edu.unq.eperdemic.persistencia.dao.DistritoDAO
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionMongoDAO
import ar.edu.unq.eperdemic.services.DistritoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DistritoServiceImpl: DistritoService {
    @Autowired
    private lateinit var ubicacionDAO: UbicacionDAO

    @Autowired
    private lateinit var ubicacionMongoDAO: UbicacionMongoDAO

    @Autowired
    private lateinit var distritoDAO: DistritoDAO

    override fun crear(distrito: Distrito): Distrito {
        if (distritoDAO.existeDistritoConElNombre(distrito.nombreDistrito))
            throw NombreDuplicadoExcepcion("ya existe un distrito con el nombre ${distrito.nombreDistrito}")
        if (distritoDAO.existenDistritosInterceptadosPorCoordenadas(distrito.coordenadasDistrito))
            throw CoordenadasNoValidasExcepcion("Las coordenadas proporcionadas intersectan a un distrito existente")
        return this.distritoDAO.save(distrito)
    }

    override fun distritoMasEnfermo(): Distrito {

        var distrito = distritoDAO.findDistritoMasEnfermo(ubicacionDAO.ubicacionesInfectadas())?:throw NoEnfermosException("No existen enfermos en los distritos")

        return distritoDAO.findDistritoByNombreDistrito(distrito)!!
    }

    override fun deleteAll() {
        this.distritoDAO.deleteAll()
    }
}