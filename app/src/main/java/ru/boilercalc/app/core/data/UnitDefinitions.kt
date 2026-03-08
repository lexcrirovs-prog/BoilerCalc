package ru.boilercalc.app.core.data

import ru.boilercalc.app.core.model.UnitDef
import ru.boilercalc.app.core.model.UnitGroup

object UnitDefinitions {
    val groups: List<UnitGroup> = listOf(
        // 0: Давление (базовая: Па)
        UnitGroup("Давление", listOf(
            UnitDef("Па", { it }, { it }),
            UnitDef("кПа", { it * 1000.0 }, { it / 1000.0 }),
            UnitDef("МПа", { it * 1_000_000.0 }, { it / 1_000_000.0 }),
            UnitDef("бар", { it * 100_000.0 }, { it / 100_000.0 }),
            UnitDef("мбар", { it * 100.0 }, { it / 100.0 }),
            UnitDef("кгс/см²", { it * 98066.5 }, { it / 98066.5 }),
            UnitDef("атм", { it * 101325.0 }, { it / 101325.0 }),
            UnitDef("мм рт.ст.", { it * 133.322 }, { it / 133.322 }),
            UnitDef("мм вод.ст.", { it * 9.80665 }, { it / 9.80665 }),
            UnitDef("psi", { it * 6894.76 }, { it / 6894.76 })
        )),
        // 4: Тепловая мощность (базовая: Вт)
        UnitGroup("Тепловая мощность", listOf(
            UnitDef("Вт", { it }, { it }),
            UnitDef("кВт", { it * 1000.0 }, { it / 1000.0 }),
            UnitDef("МВт", { it * 1_000_000.0 }, { it / 1_000_000.0 }),
            UnitDef("Гкал/ч", { it * 1_163_000.0 }, { it / 1_163_000.0 }),
            UnitDef("ккал/ч", { it * 1.163 }, { it / 1.163 }),
            UnitDef("BTU/h", { it * 0.29307 }, { it / 0.29307 })
        )),
        // 2: Температура (базовая: °C)
        UnitGroup("Температура", listOf(
            UnitDef("°C", { it }, { it }),
            UnitDef("K", { it - 273.15 }, { it + 273.15 }),
            UnitDef("°F", { (it - 32.0) * 5.0 / 9.0 }, { it * 9.0 / 5.0 + 32.0 })
        )),
        // 3: Паропроизводительность (базовая: кг/ч; МВт и кВт при r=2000 кДж/кг)
        UnitGroup("Паропроизводительность", listOf(
            UnitDef("кг/ч", { it }, { it }),
            UnitDef("т/ч", { it * 1000.0 }, { it / 1000.0 }),
            UnitDef("кг/с", { it * 3600.0 }, { it / 3600.0 }),
            UnitDef("т/сут", { it * 1000.0 / 24.0 }, { it / 1000.0 * 24.0 }),
            UnitDef("МВт", { it * 1800.0 }, { it / 1800.0 }),
            UnitDef("кВт", { it * 1.8 }, { it / 1.8 })
        )),
        // 7: Энтальпия (базовая: кДж/кг)
        UnitGroup("Энтальпия", listOf(
            UnitDef("кДж/кг", { it }, { it }),
            UnitDef("ккал/кг", { it * 4.1868 }, { it / 4.1868 }),
            UnitDef("BTU/lb", { it * 2.326 }, { it / 2.326 })
        )),
        // 8: Удельный объём (базовая: м³/кг)
        UnitGroup("Удельный объём", listOf(
            UnitDef("м³/кг", { it }, { it }),
            UnitDef("л/кг", { it / 1000.0 }, { it * 1000.0 }),
            UnitDef("ft³/lb", { it / 0.062428 }, { it * 0.062428 })
        )),
        // 9: Расход топлива (базовая: м³/ч)
        UnitGroup("Расход топлива", listOf(
            UnitDef("м³/ч", { it }, { it }),
            UnitDef("нм³/ч", { it * 1.0 }, { it * 1.0 }),
            UnitDef("тыс.м³/сут", { it * 1000.0 / 24.0 }, { it / 1000.0 * 24.0 }),
            UnitDef("кг/ч", { it / 0.72 }, { it * 0.72 })
        )),
        // 10: Теплотворная способность (базовая: МДж/м³)
        UnitGroup("Теплотворность", listOf(
            UnitDef("МДж/м³", { it }, { it }),
            UnitDef("ккал/м³", { it / 238.846 }, { it * 238.846 }),
            UnitDef("кВт·ч/м³", { it / 3.6 }, { it * 3.6 }),
            UnitDef("MJ/kg", { it * 1.0 }, { it * 1.0 })
        )),
        // 11: Объёмный расход (базовая: м³/ч)
        UnitGroup("Объёмный расход", listOf(
            UnitDef("м³/ч", { it }, { it }),
            UnitDef("л/с", { it * 3.6 }, { it / 3.6 }),
            UnitDef("л/мин", { it * 0.06 }, { it / 0.06 }),
            UnitDef("GPM", { it * 0.227125 }, { it / 0.227125 })
        ))
    )
}
