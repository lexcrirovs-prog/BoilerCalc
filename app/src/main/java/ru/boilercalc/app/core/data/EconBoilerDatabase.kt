package ru.boilercalc.app.core.data

import ru.boilercalc.app.core.model.EconBoilerModel

object EconBoilerDatabase {
    val steam: List<EconBoilerModel> = listOf(
        EconBoilerModel("s500", "Premium S - 500", 39.0, 1864570, 0.92),
        EconBoilerModel("s1000", "Premium S - 1000", 78.0, 2833410, 0.92),
        EconBoilerModel("s1500", "Premium S - 1500", 116.0, 3100600, 0.92, 1000000),
        EconBoilerModel("s2000", "Premium S - 2000", 155.0, 3455323, 0.92, 1245000),
        EconBoilerModel("s2500", "Premium S - 2500", 193.0, 3629127, 0.92, 1450000),
        EconBoilerModel("s3000", "Premium S - 3000", 247.0, 4217792, 0.92, 1600000),
        EconBoilerModel("s3500", "Premium S - 3500", 270.0, 5079409, 0.92, 1800000),
        EconBoilerModel("s4000", "Premium S - 4000", 308.0, 5760302, 0.92, 1951000),
        EconBoilerModel("s5000", "Premium S - 5000", 384.0, 6739180, 0.92, 2250000)
    )

    val water: List<EconBoilerModel> = listOf(
        EconBoilerModel("c250", "Premium C - 250", 28.8, 550000, 0.92),
        EconBoilerModel("c500", "Premium C - 500", 54.6, 700000, 0.92),
        EconBoilerModel("c1000", "Premium C - 1000", 110.2, 1250000, 0.92),
        EconBoilerModel("c1500", "Premium C - 1500", 165.2, 1650000, 0.92),
        EconBoilerModel("c2000", "Premium C - 2000", 217.5, 2300000, 0.92),
        EconBoilerModel("c2500", "Premium C - 2500", 271.0, 2500000, 0.92),
        EconBoilerModel("c3000", "Premium C - 3000", 326.6, 2950000, 0.92),
        EconBoilerModel("c4000", "Premium C - 4000", 426.7, 3925000, 0.92)
    )

    val waterE: List<EconBoilerModel> = listOf(
        EconBoilerModel("e1000", "Premium E - 1000", 109.0, 1600000, 0.93),
        EconBoilerModel("e2000", "Premium E - 2000", 218.0, 3000000, 0.93),
        EconBoilerModel("e2500", "Premium E - 2500", 273.0, 3780000, 0.93),
        EconBoilerModel("e3000", "Premium E - 3000", 327.0, 4200000, 0.93),
        EconBoilerModel("e3500", "Premium E - 3500", 382.0, 5200000, 0.93),
        EconBoilerModel("e4000", "Premium E - 4000", 436.0, 5400000, 0.93),
        EconBoilerModel("e5000", "Premium E - 5000", 545.0, 5950000, 0.93),
        EconBoilerModel("e6000", "Premium E - 6000", 655.0, 6850000, 0.93),
        EconBoilerModel("e7000", "Premium E - 7000", 764.0, 8375000, 0.93),
        EconBoilerModel("e8000", "Premium E - 8000", 873.0, 9800000, 0.93),
        EconBoilerModel("e10000", "Premium E - 10000", 1091.0, 12000000, 0.93),
        EconBoilerModel("e12000", "Premium E - 12000", 1309.0, 14500000, 0.93)
    )

    val all: List<EconBoilerModel> get() = steam + water + waterE
}
