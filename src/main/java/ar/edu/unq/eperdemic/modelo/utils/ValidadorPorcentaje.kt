package ar.edu.unq.eperdemic.modelo.utils

import ar.edu.unq.eperdemic.exceptions.ValorDeCapacidadNoValido

class ValidadorPorcentaje {

    companion object {
        fun validar(capacidad: Int): Int {
            if (capacidad in 0..100) {
                return capacidad
            } else throw ValorDeCapacidadNoValido("Error: el porcentaje ingresado no es valido")
        }
    }
}