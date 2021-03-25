package org.entur.lahmu.domain.gbfs.v2_2

typealias Position = DoubleArray

val Position.lon: Double
    get() = this[0]

val Position.lat: Double
    get() = this[1]

val Position.alt: Double?
    get() = if (size > 2) this[2] else null
