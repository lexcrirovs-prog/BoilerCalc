package ru.boilercalc.app.core.data

data class VideoEpisode(
    val title: String,
    val duration: String
)

object ProductionVideos {
    val episodes: List<VideoEpisode> = listOf(
        VideoEpisode("Входной контроль металла", "4:32"),
        VideoEpisode("Раскрой и гибка листов", "5:18"),
        VideoEpisode("Сварка котловых элементов", "6:04"),
        VideoEpisode("Контроль сварных швов (УЗК)", "3:47"),
        VideoEpisode("Сборка топочной камеры", "7:12"),
        VideoEpisode("Монтаж трубного пучка", "5:55"),
        VideoEpisode("Гидравлические испытания", "4:21"),
        VideoEpisode("Обмуровка и теплоизоляция", "6:38"),
        VideoEpisode("Установка горелочного устройства", "5:09"),
        VideoEpisode("Автоматика и КИП", "4:44"),
        VideoEpisode("Покраска и маркировка", "3:15"),
        VideoEpisode("Упаковка и отгрузка", "4:02")
    )
}
