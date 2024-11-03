package com.example.ded2

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "personagem")

class PersonagemBD {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var nome: String = ""
    var raca: String = ""
    var nivel: Int = 0
    var xp: Double = 0.0
    var forca: Int = 0
    var destreza: Int = 0
    var constituicao: Int = 0
    var inteligencia: Int = 0
    var sabedoria: Int = 0
    var carisma: Int = 0
    var vida: Int = 0
}