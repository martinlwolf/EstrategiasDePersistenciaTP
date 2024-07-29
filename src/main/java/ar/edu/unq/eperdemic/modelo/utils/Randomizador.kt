package ar.edu.unq.eperdemic.modelo.utils

import kotlin.random.Random

object Randomizador {

    private var ambiente: Enum<Ambiente> = Ambiente.PROD

    fun <T> getElemRandom(lista : List<T>): T?{
        if (lista.isEmpty()){ return null }
        var indiceAleatorio = Random.nextInt(0, lista.size)
        if(ambiente == Ambiente.TEST){
            indiceAleatorio = 0
        }
        return lista[indiceAleatorio]
    }

    fun getIntRandom(): Int {
        var randomInt = Random.nextInt(1, 11)
        if(ambiente == Ambiente.TEST){
            randomInt = 10
        }
        return  randomInt
    }

    fun cambiarAmbiente(modo: String){
        if(modo == "Test"){
            ambiente = Ambiente.TEST
        }
    }
}

enum class Ambiente {
    TEST, PROD
}