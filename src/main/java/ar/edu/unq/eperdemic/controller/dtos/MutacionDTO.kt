package ar.edu.unq.eperdemic.controller.dtos

import ar.edu.unq.eperdemic.exceptions.TipoDeVectorDesconocido
import ar.edu.unq.eperdemic.modelo.mutaciones.BioalteracionGenetica
import ar.edu.unq.eperdemic.modelo.mutaciones.Mutacion
import ar.edu.unq.eperdemic.modelo.mutaciones.SupresionBiomecanica

class MutacionDTO(var id: Long?,
                  var potencia: Int?,
                  var tipoVector: String?,
                  var tipoMutacion: String
){
    companion object {
        fun desdeModelo(mutacion: Mutacion) = this.getDTOSegunTipoMutacion(mutacion)

        private fun getDTOSegunTipoMutacion(mutacion: Mutacion): MutacionDTO {
            return when (mutacion.getTipoMutacion()) {
                "BioalteracionGenetica" -> return MutacionDTO(
                    id = mutacion.id,
                    tipoVector = (mutacion as BioalteracionGenetica).tipoVector,
                    potencia = null,
                    tipoMutacion = mutacion.getTipoMutacion()
                )
                "SupresionBiomecanica" -> return MutacionDTO(
                    id = mutacion.id,
                    tipoVector = null,
                    potencia = (mutacion as SupresionBiomecanica).potencia,
                    tipoMutacion = mutacion.getTipoMutacion()
                )
                else -> { throw TipoDeVectorDesconocido("tipo de mutacion desconocida")
                }
            }
        }
    }

    fun aModelo(): Mutacion {
        val mutacion = this.crearSubclaseMutacion()
        mutacion.id = this.id

        return mutacion
    }

    private fun crearSubclaseMutacion(): Mutacion {
        return when (this.tipoMutacion) {
            "BioalteracionGenetica" -> return BioalteracionGenetica(this.tipoVector!!)
            "SupresionBiomecanica" -> return SupresionBiomecanica(this.potencia!!)
            else -> { throw TipoDeVectorDesconocido("tipo de mutacion desconocida")
            }
        }
    }
}