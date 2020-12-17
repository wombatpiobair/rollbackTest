package com.iwombat.rollbacks.persist.model

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "motorcycle")
data class Motorcycle(

        @Id
        var id: UUID = UUID.randomUUID(),

        @Column(nullable = false)
        @Convert(converter = MarqueConverter::class)
        var marque: Marque = Marque.UNKNOWN,

        @Column(nullable = false)
        var model: String = "",

        @Column(nullable = false, unique = true)
        var vin: String = "",

        @Column
        var location: String = "",

        @Column(nullable = false)
        var year: Int = 0,

        @Version
        var version: Int = 0
) {

}

@Converter(autoApply=true)
class MarqueConverter : AttributeConverter<Marque, String> {
        override fun convertToDatabaseColumn(marque: Marque?): String {
                return marque?.name ?: Marque.UNKNOWN.name
        }

        override fun convertToEntityAttribute(name: String): Marque {
                return Marque.from(name)
        }

}