package dev.andresfelipecaicedo.nomellames.domain.model.maper

interface DomainMapper<T> {
    fun mapToDomain():T
}