package com.example.acadtrack_beta.ui.util

import java.text.Normalizer

// Quita tildes/diacríticos y pasa a minúsculas.
fun String.normalizarBusqueda(): String {
    val sinTildes = Normalizer.normalize(this, Normalizer.Form.NFD)
        .replace(Regex("\\p{Mn}+"), "")
    return sinTildes.lowercase().trim()
}

// true si el texto de búsqueda está vacío (no filtra nada) o si aparece
// dentro de al menos uno de los valores dados (título, descripción, nombre, etc.)
fun coincideConBusqueda(texto: String, valores: List<String>): Boolean {
    if (texto.isBlank()) return true
    val consulta = texto.normalizarBusqueda()
    return valores.any { it.normalizarBusqueda().contains(consulta) }
}

