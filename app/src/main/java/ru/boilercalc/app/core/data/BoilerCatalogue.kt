package ru.boilercalc.app.core.data

import ru.boilercalc.app.core.model.BoilerModel
import ru.boilercalc.app.core.model.WaterBoilerModel

object BoilerCatalogue {
    val steamBoilers: List<BoilerModel> = listOf(
        BoilerModel("s500", "Premium S - 500", 250, 500, 39),
        BoilerModel("s1000", "Premium S - 1000", 500, 1000, 78),
        BoilerModel("s1500", "Premium S - 1500", 750, 1500, 116),
        BoilerModel("s2000", "Premium S - 2000", 1000, 2000, 155),
        BoilerModel("s2500", "Premium S - 2500", 1250, 2500, 193),
        BoilerModel("s3000", "Premium S - 3000", 1500, 3000, 233),
        BoilerModel("s3500", "Premium S - 3500", 1750, 3500, 270),
        BoilerModel("s4000", "Premium S - 4000", 2000, 4000, 308),
        BoilerModel("s5000", "Premium S - 5000", 2500, 5000, 384),
        BoilerModel("s15000", "Premium S - 15000 (до 15т)", 0, 15000, 1152)
    )

    val waterBoilers: List<WaterBoilerModel> = listOf(
        WaterBoilerModel("Premium C", 250, false),
        WaterBoilerModel("Premium C", 500, false),
        WaterBoilerModel("Premium C", 750, false),
        WaterBoilerModel("Premium C", 1000, false),
        WaterBoilerModel("Premium C", 1250, false),
        WaterBoilerModel("Premium C", 1500, false),
        WaterBoilerModel("Premium C", 1750, false),
        WaterBoilerModel("Premium C", 2000, false),
        WaterBoilerModel("Premium C", 2500, false),
        WaterBoilerModel("Premium C", 3000, false),
        WaterBoilerModel("Premium C", 3500, false),
        WaterBoilerModel("Premium C", 4000, false),
        WaterBoilerModel("Premium C", 5000, false),
        WaterBoilerModel("Premium C", 6000, false),
        WaterBoilerModel("Premium E", 7000, true),
        WaterBoilerModel("Premium E", 8000, true),
        WaterBoilerModel("Premium E", 9000, true),
        WaterBoilerModel("Premium E", 10000, true)
    )
}
