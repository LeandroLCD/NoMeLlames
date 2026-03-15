package cl.blipblipcode.prefixsapp.domain.model.maper

interface DomainMapper<T> {
    fun mapToDomain():T
}