package com.example.ded2

import androidx.lifecycle.ViewModel
import org.example.controlePersonagem.Racas.*

class PersonagemViewModel() : ViewModel() {

    var classeRaca : raca = humano()

    fun setClassRaca(selectedRaca: String) : raca {
        when(selectedRaca){
            "Alto Elfo" -> this.classeRaca = altoElfo()
            "Anão" -> this.classeRaca = anao()
            "Anão da Colina" -> this.classeRaca = anaoColina()
            "Anão da montanha" -> this.classeRaca = anaoMontanha()
            "Draconato" -> this.classeRaca = draconato()
            "Drow" -> this.classeRaca = drow()
            "Elfo" -> this.classeRaca = elfo()
            "Elfo da Floresta" -> this.classeRaca = elfoFloresta()
            "Gnomo" -> this.classeRaca = gnomo()
            "Gnomo das Rochas" -> this.classeRaca = gnomoRochas()
            "Halfling" -> this.classeRaca = halfling()
            "Halfling Pés-Leves" -> this.classeRaca = halflingPesLeves()
            "Halfling Robusto" -> this.classeRaca = halflingRobusto()
            "Humano" -> this.classeRaca = humano()
            "Meio Helfo" -> this.classeRaca = meioElfo()
            "Meio Orc" -> this.classeRaca = meioOrc()
            "Tiefling" -> this.classeRaca = tiefling()
        }

        return this.classeRaca
    }
}