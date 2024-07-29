package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.modelo.Especie

interface EspecieService {

    fun actualizarEspecie(especie: Especie): Especie
    fun recuperarEspecie(id: Long): Especie
    fun recuperarATodasLasEspecies(): List<Especie>
    fun cantidadDeInfectados(especieId: Long ): Int
}